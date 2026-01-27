package com.topwise.premierpay.transmit.json;

import android.text.TextUtils;

import com.topwise.manager.AppLog;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.daoutils.DaoUtilsStore;
import com.topwise.premierpay.daoutils.entity.DupTransdata;
import com.topwise.premierpay.emv.EmvResultUtlis;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.pack.IPacker;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.transmit.TransProcessListener;

public class JsonOnline {
    private static final String TAG =  TopApplication.APPNANE + JsonOnline.class.getSimpleName();
    private static JsonOnline online;

    private JsonOnline() {

    }

    public static JsonOnline getInstance() {
        if (online == null) {
            online = new JsonOnline();
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
            if (listener != null) {
                return listener.onCalcMac(data);
            }
            return null;
        }

        @Override
        public byte[] onEncTrack(byte[] track) {
            if (listener != null) {
                return listener.
                        onEncTrack(track);
            }
            return null;
        }
    }

    private AOkhttpCommunicate comm;
    private DupTransdata dupTransdata;

    public int online(TransData transData, final TransProcessListener listener) {
        int ret = -1;
        try {
            ETransType transType = ETransType.valueOf(transData.getTransType());

            // 准备打包器
            IPacker<TransData, String> packager = transType.getpackJsonager(new PackListenerImpl(listener));
            IPacker<TransData, String> dupPackager = transType.getDupPackJsonager(new PackListenerImpl(listener));
            // 打包
            comm = getCommClient();
            comm.setTransProcessListener(listener);

//             连接
            ret = comm.onInitPath();
            if (ret != 0) {
                return TransResult.ERR_CONNECT;
            }

            String req = null;
            if (transData.isReversal()) {
//                Device.openRedLed();
                req = dupPackager.pack(transData);
            } else {
//                Device.openGreenLed();
                req = packager.pack(transData);
            }
            if (req == null) {
                return TransResult.ERR_PACK;
            }

            // 联机交易标识
            transData.setOnlineTrans(true);

            // 冲正处理,移前,防止测试断电时，冲正记录未保存至数据库中
            if (dupPackager != null && !transData.isReversal()) {
                //初始化 dupTransdata
                dupTransdata = Component.transInitDup(transData);
                boolean deleteDup = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                boolean save = DaoUtilsStore.getInstance().getmDupTransDaoUtils().save(dupTransdata);
            }
            // send data
            AppLog.i(TAG, "jsonPack SEND:" + req);
            AppLog.i(TAG, "jsonPack PINKSN: " + transData.getPinKsn());
            AppLog.i(TAG, "jsonPack DATAKSN: " + transData.getDataKsn());

            JsonResponse jResponse = comm.onSendAndRecv(req);
            AppLog.i(TAG, "jsonPack RECV CODE :" + jResponse.getRetCode());
            if (jResponse.getRetCode() == TransResult.ERR_RECV
                    && dupPackager != null && !transData.isReversal()){
                dupTransdata.setReason(Component.REASON_NO_RECV);
                dupTransdata.setOrigDate(transData.getDate());
                DaoUtilsStore.getInstance().getmDupTransDaoUtils().update(dupTransdata);
            }

            if (TextUtils.isEmpty(jResponse.getData())) {
                return jResponse.getRetCode();
            }
            AppLog.i(TAG, "jsonPack RECV:" + jResponse.getData());

            if (transData.isReversal()) {
                return dupPackager.unpack(transData, jResponse.getData());
            }
            ret = packager.unpack(transData, jResponse.getData());
            AppLog.i(TAG, "jsonPack unpack:" + ret);
            //check scipt
//            test
//            transData.setRecvIccData("8A0230309110A33C3F61839400000000000000000000");

//            transData.setRecvIccData("8A02303072149F180430303030860B8C180000068E04F76E21FF911015C62CAD808000000000000000000000");
//            transData.setResponseCode("00");
//            transData.setKernelType(13);

            if (transType.isScriptSend() && !TextUtils.isEmpty(transData.getRecvIccData())){
                AppLog.i(TAG, "jsonPack 交易类型支持脚本上送，且有返回脚本数据 ==");
                boolean b = EmvResultUtlis.checkRupayScriptContactlessResult(transData);
                AppLog.i(TAG, "jsonPack 解包后判断是否需要脚本处理 ==" + b);
                transData.setNeedScript(b);
            }

            if ((ret == TransResult.ERR_BAG || ret == TransResult.SUCC) &&
                dupPackager != null && !transData.isReversal()&& !transData.getNeedScript()){
                AppLog.i(TAG, "jsonPack unpack: getAmount" + transData.getAmount());
//                if (1000 != Long.valueOf(transData.getAmount())){
                    boolean deleteAll = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
                    AppLog.i(TAG, "DupTransDao deleteAll :" + deleteAll);
//                }
            }
//            boolean deleteDup = DaoUtilsStore.getInstance().getmDupTransDaoUtils().deleteAll();
//            AppLog.i(TAG, "getmDupTransDaoUtils deleteDup:" + deleteDup);
            return ret;
        }finally {

        }

    }
    private AOkhttpCommunicate getCommClient() {
//        return new TcpNoSslCommunicate();
        return new OkHttpCommunicate();
    }
}
