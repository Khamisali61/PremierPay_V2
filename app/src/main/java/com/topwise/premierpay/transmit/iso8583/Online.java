package com.topwise.premierpay.transmit.iso8583;

import android.os.RemoteException;

import com.topwise.cloudpos.aidl.tm.AidlTM;
import com.topwise.cloudpos.aidl.tm.AidlTMListener;
import com.topwise.cloudpos.aidl.tm.MessageType;
import com.topwise.manager.AppLog;
import com.topwise.toptool.api.convert.IConvert;
import com.topwise.premierpay.BuildConfig;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TestParam;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.pack.IPacker;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.trans.model.TransStatusSum;
import com.topwise.premierpay.transmit.TransProcessListener;
import com.topwise.premierpay.utils.ConfiUtils;
import com.topwise.premierpay.utils.NetWorkUtils;

public class Online {
    private static final String TAG =  TopApplication.APPNANE + Online.class.getSimpleName();
    private static Online online;

    private Online() {

    }

    public static Online getInstance() {
        if (online == null) {
            online = new Online();
        }
        return online;
    }

    class PackListenerImpl implements PackListener {
        private TransProcessListener listener;

        public PackListenerImpl(TransProcessListener listener) {
            this.listener = listener;
        }

        @Override
        public byte[] onCalcMac(byte[] data) {
//            if (BuildConfig.CHANNEL.equals("topwise")) {
//                return  new byte[8];
//            }

            if (listener != null) {
                return listener.onCalcMac(data);
            }
            return null;
        }

        @Override
        public byte[] onEncTrack(byte[] track) {
            if (listener != null) {
                return listener.onEncTrack(track);
            }
            return null;
        }
    }

    private ACommunicate comm;
    private DupTransdata dupTransdata;

    public int online(TransData transData, TransProcessListener listener) {
        int ret = -1;
        long oldTime = 0l;
        TransStatusSum transStatusSum = transData.getTransStatusSum();

        try {
            if (!ConfiUtils.isDebug) { // Not test
                if (netChoose(transData) != TransResult.SUCC) {
                    return ret;
                }
                ETransType transType = ETransType.valueOf(transData.getTransType());
                AppLog.d("online","transType=== " + transType);
                PackListener packListener = new PackListenerImpl(listener);
                // 准备打包器
                IPacker<TransData, byte[]> packager = transType.getpackager(packListener);
                IPacker<TransData, byte[]> dupPackager = transType.getDupPackager(packListener);
                // 打包
                comm = getCommClient();
                comm.setTransProcessListener(listener);

                //             连接
                oldTime = System.currentTimeMillis();

                byte[] req = null;
                if (transData.isReversal()) {
                    req = dupPackager.pack(transData);
                } else {
                    req = packager.pack(transData);
                }
                if (req == null) {
                    return TransResult.ERR_PACK;
                }
                byte[] sendData = new byte[2 + req.length];
                    sendData[0] = (byte) (req.length / 256);
                    sendData[1] = (byte) (req.length % 256);
                    System.arraycopy(req, 0, sendData, 2, req.length);
                    transStatusSum.setPackTime(System.currentTimeMillis() - oldTime);

                    // 冲正交易不需要增加流水号
                    // 联机交易标识
                    transData.setIsOnlineTrans(true);
                    // 冲正处理,移前,防止测试断电时，冲正记录未保存至数据库中
                    if (dupPackager != null && !transData.isReversal()) { // 当前交易非冲正交易，则记录数据到冲正库表
                        // 初始化 dupTransdata
                        dupTransdata = Component.transInitDup(transData);
                        boolean deleteDup = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                        boolean save = DaoUtilsStore.getInstance().getmDupTransDaoUtils().save(dupTransdata);
                    }

                    // 连接
                    AppLog.i(TAG, "SEND:" + TopApplication.convert.bcdToStr(sendData));

                oldTime = System.currentTimeMillis();
                int count =0;
                for(count =0; count<3; count++) {
                    AppLog.i(TAG, "comm.onConnect() ret0:" + ret);
                    ret = comm.onConnect();
                    AppLog.i(TAG, "comm.onConnect() ret1:" + ret);
                    if (ret == TransResult.SUCC) {
                        break;
                    }
                }
                AppLog.i(TAG, "comm.onConnect() ret2:" + ret);

                // 连接
                transStatusSum.setConnCount(count);
                transStatusSum.setConnectTime(System.currentTimeMillis() - oldTime);
                if (ret != 0) {
                    return TransResult.ERR_CONNECT;
                }
                //            if (req.length > 0){
                //                return TransResult.SUCC;
                //            }
                AppLog.i(TAG, "comm.onSend() oldTime1:" + oldTime);

                // 发送
                oldTime = System.currentTimeMillis();
                AppLog.i(TAG, "comm.onSend() oldTime2:" + oldTime);
                ret = comm.onSend(sendData);
                transStatusSum.setSendTime(System.currentTimeMillis() - oldTime);
                AppLog.d(TAG, "onSend = " + ret);
                if (ret != 0) {
                    if (dupPackager != null && !transData.isReversal()) {
                        // TransData.deleteDupRecord(); // 当前交易未发送成功时，不发冲正
                    }
                    return TransResult.ERR_SEND;
                }

                // 接收
                oldTime = System.currentTimeMillis();
                CommResponse commResponse = comm.onRecv();
                transStatusSum.setReceiveTime(System.currentTimeMillis() - oldTime);
                AppLog.d(TAG, "onRecv getRetCode = " + commResponse.getRetCode());
                if (commResponse.getRetCode() != TransResult.SUCC) {
                    // 更新冲正原因
                    if (dupPackager != null && !transData.isReversal()) {
                        AppLog.d(TAG, "updateDupReason_REASON_NO_RECV>>");
                        dupTransdata.setReason(Component.REASON_NO_RECV);
                        dupTransdata.setOrigDate(transData.getDate());
                        DaoUtilsStore.getInstance().getmDupTransDaoUtils().update(dupTransdata);
                    }
                    return TransResult.ERR_RECV;
                }

                sendMes2TMS(TopApplication.convert.bcdToStr(sendData));

                AppLog.i(TAG, "RECV:" + TopApplication.convert.bcdToStr(commResponse.getData()));

                // 如果是冲正交易
                if (transData.isReversal()) {
                    return dupPackager.unpack(transData, commResponse.getData());
                }

                oldTime = System.currentTimeMillis();
                ret = packager.unpack(transData, commResponse.getData());
//                ret = packager.unpack(transData, null);
                transStatusSum.setUnpackTime(System.currentTimeMillis() - oldTime);

                // 非冲正类交易，如消费等交易，需要更新冲正原因/时间，MAC校验错误
                if (ret == TransResult.ERR_MAC && dupPackager != null && !transData.isReversal()) {
                    AppLog.d(TAG, "updateDupReason_ERR_MAC>>");
                    dupTransdata.setReason(Component.REASON_MACWRONG);
                    dupTransdata.setOrigDate(transData.getDate());
                    DaoUtilsStore.getInstance().getmDupTransDaoUtils().update(dupTransdata);
                }

                // 如果39域返回null,删除冲正文件, 或者解包3， 4， 11，
                // 41，42域与请求不同时，删除冲正(BCTC要求下笔交易不发冲正)
                if (ret == TransResult.ERR_BAG || ret == TransResult.ERR_PROC_CODE || ret == TransResult.ERR_TRANS_AMT
                        || ret == TransResult.ERR_TRACE_NO || ret == TransResult.ERR_TERM_ID || ret == TransResult.ERR_MERCH_ID
                        || ret == TransResult.ERR_HOST_REJECT || ret == TransResult.ERR_NEED_ENTER_PWD || ret == TransResult.ERR_NEED_INSTER_CARD) {
                    DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                }
                return ret;
            } else { // Test
                ETransType transType = ETransType.valueOf(transData.getTransType());
                IPacker<TransData, byte[]> packager = transType.getpackager(new PackListenerImpl(listener));
                byte[] req = packager.pack(transData);
                if (req == null) {
                    return TransResult.ERR_PACK;
                 }
                 byte[] sendData = new byte[2 + req.length];
                 sendData[0] = (byte) (req.length / 256);
                 sendData[1] = (byte) (req.length % 256);
                 System.arraycopy(req, 0, sendData, 2, req.length);
                 AppLog.d(TAG, "Send = " + TopApplication.convert.bcdToStr(sendData));

                // 联机交易标识
                transData.setIsOnlineTrans(true);

                CommResponse commResponse = new CommResponse(0, getResponseData(transType, transData.getEnterMode()));
                AppLog.d(TAG, "onRecv getRetCode = " + commResponse.getRetCode());
                if (commResponse.getRetCode() != TransResult.SUCC) {
                    // 更新冲正原因
                    return TransResult.ERR_RECV;
                }
                AppLog.i(TAG, "RECV:" + TopApplication.convert.bcdToStr(commResponse.getData()));

                ret = packager.unpack(transData, commResponse.getData());

                // 如果39域返回null,删除冲正文件, 或者解包3， 4， 11，
                // 41，42域与请求不同时，删除冲正(BCTC要求下笔交易不发冲正)
                if (ret == TransResult.ERR_BAG || ret == TransResult.ERR_PROC_CODE || ret == TransResult.ERR_TRANS_AMT
                        || ret == TransResult.ERR_TRACE_NO || ret == TransResult.ERR_TERM_ID
                        || ret == TransResult.ERR_MERCH_ID) {
                    DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                }
                return ret;
            }
        } finally {
            // TODO: MODEM 此时还不能关闭
            if (comm != null) {
                comm.onClose();
            }
        }
    }

    private ACommunicate getCommClient() {
        if ("topwise".startsWith(BuildConfig.CHANNEL)) {
           return new TcpNoSslCommunicate();
        } else {
//            return new TcpCupSslCommunicate(null);
            return new TcpNoSslCommunicate();
        }
    }

    private  void sendMes2TMS(String message) {
        AppLog.d(TAG, "sendMes2TMS  >>>>>>>>>>>>>>>>>>>>> ");
        AidlTM aidltm = TopApplication.usdkManage.getTmsManager();
        if (aidltm == null) {
            return;
        }

        try {
            aidltm.uploadMessageData(new AidlTMListener.Stub() {
                @Override
                public void onResult(String s) throws RemoteException {
                    AppLog.d(TAG, "sendMes2TMS onResult >> "+ s);
                }

                @Override
                public void onError(String s) throws RemoteException {
                    AppLog.d(TAG, "sendMes2TMS onError >> "+ s);
                }

                @Override
                public void onTimeOut() throws RemoteException {
                    AppLog.d(TAG, "sendMes2TMS onTimeOut >> ");
                }
            }, message, MessageType.MESSAGE_TYPE_TRANSACTION);
        } catch (RemoteException e) {
            AppLog.d(TAG, "sendMes2TMS error >> ");
        }
    }

    private byte[] getResponseData(ETransType transType, int enterModel) {
        String responseStr="";
        if (transType == ETransType.LOGON) {
            responseStr ="60000006016032003220120810003800010AC0001400002322191703220800155800323231393137323031303736303030353137373036373839383434303135333131333530370011000005120030004023F6C142C8706C9C6ADECF71E3034D5E3559235FB54CD5370E4C966800000000000000003AB4EEF1";
        } else if ((transType == ETransType.TRANS_SALE||transType == ETransType.TRANS_AUTO_SALE)&&enterModel==Component.EnterMode.QPBOC) {
            responseStr ="60000006016032003220120210703E02810AD08213166214832011141319000000000000000001000052222210032228060322000100080015580032323232313032303232383530303035313737303637383938343430313533313133353037223033303830303030202020343830323538303020202031353600179F36020719910AA27DBF42742B1E1B303000142200051200060000034355503139364139313545";
        } else if ((transType == ETransType.TRANS_SALE||transType == ETransType.TRANS_AUTO_SALE)&&enterModel==Component.EnterMode.INSERT) {
            responseStr ="60000006016032003220120210703E02810AD08213166214832011141319000000000000000001000053222255032228060322000100080015580032323232353532393635383330303035313737303637383938343430313533313133353037223033303830303030202020343830323538303020202031353600179F3602071A910A4A7639C99A319981303000142200051200060000034355503634443735354241";
        } else if ((transType == ETransType.TRANS_SALE||transType == ETransType.TRANS_AUTO_SALE)&&enterModel==Component.EnterMode.SWIPE) {
            responseStr ="60000006016032003220120210703E02810AD08213166214832011141319000000000000000001000056211900051728060517000100080015580032313139303032323636383130303035313737303637383938343430313533313133353037223033303830303030202020343830323538303020202031353600179F3602073A910AAC52839A0BB11648303000142200051200060000034355504337354142443536";
        } else if (transType == ETransType.TRANS_QR_SALE&&enterModel==Component.EnterMode.QR) {
            responseStr ="60000006016032003220120210303A00810AD0803300000000000000000100006821561805260526000800155800323135363138323538373336303030353137373036373839383434303135333131333530372236353035353831302020203438303235383030202020313536002541343032303538323130353236373839373839353730383032001122000512000000034355503934353735394239";
        } else {
            responseStr ="60000006016032003220120210703E02810AD08213166214832011141319000000000000000001000052222210032228060322000100080015580032323232313032303232383530303035313737303637383938343430313533313133353037223033303830303030202020343830323538303020202031353600179F36020719910AA27DBF42742B1E1B303000142200051200060000034355503139364139313545";
        }
        responseStr = "7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E7E30323130723C06002EE0920431363534323730373436303038323739373530303030303030303030303030303130303030373136313535343433303030303336313535343433303731363237303830353130303133373534323730373436303038323739373544323730383232363030303031303030303030323735313937313030303030333630303030353430303030303030323139303030303030303131353034363031303030303030303030303030303030303030303030303030303030303030546F7057697365506179343034343632363242444443414239454242433032343931304132393330383642344345363837383537303031323030383632303030303030";
        return TopApplication.convert.strToBcd(responseStr, IConvert.EPaddingPosition.PADDING_RIGHT);
    }

    private int netChoose(TransData transData) {
        if (!transData.isStressTest()) {
            return 0;
        }

        TestParam testParam = DaoUtilsStore.getInstance().getTestParam();
        if (testParam == null) {
            return 0;
        }

        TransStatusSum statusSum = transData.getTransStatusSum();

        /***** 0: wifi ,1 mobile********/
        int commType ;
        if (testParam.getCommType() == 2) {
            if (System.currentTimeMillis()%2 == 0) {
                commType =0;
            } else {
                commType =1;
            }
        } else if (testParam.getCommType() == 1) {
            commType =1;
        } else {
            commType =0;
        }
        statusSum.setCommType(commType);
        if (commType == 0) {
            if (!NetWorkUtils.isWifiConnected(TopApplication.mApp)) {
                return TransResult.ERR_CONNECT;
            }
            NetWorkUtils.switchNet(TopApplication.mApp,0);
            statusSum.setSingleValue(NetWorkUtils.getWifSignal(TopApplication.mApp));
        } else {
            if (!NetWorkUtils.isMobileConnected(TopApplication.mApp)) {
                return TransResult.ERR_CONNECT;
            }
            NetWorkUtils.switchNet(TopApplication.mApp,1);
            statusSum.setSingleValue(NetWorkUtils.getMobileSignal());
        }
        return 0;
    }
}
