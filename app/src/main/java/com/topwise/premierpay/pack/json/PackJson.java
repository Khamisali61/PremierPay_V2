package com.topwise.premierpay.pack.json;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.topwise.manager.AppLog;
import com.topwise.manager.utlis.DataUtils;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.trans.model.Component;
import com.topwise.premierpay.trans.model.ETransType;
import com.topwise.premierpay.trans.model.TransData;
import com.topwise.premierpay.trans.model.TransResult;
import com.topwise.premierpay.pack.IPacker;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.transmit.json.JsonMessage;

public abstract class PackJson implements IPacker<TransData, String> {
    private static final String TAG = TopApplication.APPNANE + PackJson.class.getSimpleName();
    protected PackListener listener;

    protected Gson jsonGson;
    protected JsonMessage sendData;
//  protected JSONObject jsonBody;

    public PackJson(PackListener listener) {
        this.listener = listener;
        initEntity();
    }

    private void initEntity(){
        jsonGson =  new GsonBuilder().create();
        sendData = new JsonMessage();
//      jsonBody = new JSONObject();
    }

    protected String pack(boolean isNeedMac){
        return jsonGson.toJson(sendData);
    }

    private int checkRecvData(JsonMessage jRecv){
        return TransResult.SUCC;
    }

    /**
     * h,m, field 3, field 25, field 41,field 42
     * @param transData
     * @return
     */
    protected int setMandatoryData(TransData transData) {
        ETransType transType = ETransType.valueOf(transData.getTransType());
        try {
            String temp = "";
            // msgType
            if (transData.isReversal()) {
                temp = transType.getDupMsgType();
                if (!TextUtils.isEmpty(temp))
                    sendData.setMsgType(temp);
            } else {
                temp = transType.getMsgType();
                if (!TextUtils.isEmpty(temp))
                    sendData.setMsgType(temp);
            }

            //3
            temp = transType.getProcCode();
            if (!TextUtils.isEmpty(temp))
                sendData.setF003(temp);
//                jsonBody.put("F003",temp);

            //25
            temp = transType.getServiceCode();
            if (!TextUtils.isEmpty(temp))
                sendData.setF025(temp);

            temp = transData.getTermID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF041(temp);


            temp = transData.getMerchID();
            if (!TextUtils.isEmpty(temp))
                sendData.setF042(temp);

            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransResult.ERR_PACK;
    }

    /**
     * field 2, 4, 11, 14, 22, 23, 26, 35, 36, 49, 52, 53
     * @param transData
     * @return
     */
    protected int setCommonData(TransData transData) {
        try {
            String temp = "";
            int enterMode = transData.getEnterMode();
            ETransType transType = ETransType.valueOf(transData.getTransType());
            if (enterMode == Component.EnterMode.MANAUL) {
                //2
                temp = transData.getPan();
                if (!TextUtils.isEmpty(temp))
                    sendData.setF002(temp);

                //14
                temp = transData.getExpDate();
                if (!TextUtils.isEmpty(temp))
                    sendData.setF014(temp);
             //35 //36
            } else if (enterMode == Component.EnterMode.SWIPE) {
                //35
                temp = transData.getTrack2();
                if (!TextUtils.isEmpty(temp)) {
                    if (transData.isEncTrack()) {

                        AppLog.i("onEncTrack","DataKsn == "+transData.getDataKsn());
                        byte[] track = listener.onEncTrack(temp.getBytes());
                        if (track != null) {
//                            String sF35 = topApplication.convert.bcdToStr(track);
                            String sF35 = new String(track);
                            AppLog.i("onEncTrack","out == "+sF35);
                            sendData.setF035(sF35);
                        }
                        temp = transData.getDataKsn();
                        if (!TextUtils.isEmpty(temp)) {
                            //53 You need to send in the DE35 encrypted.With DE36 carrying the KSN value
                            sendData.setF036(temp);
                        }
                    } else {
                        sendData.setF035(temp);
                    }
                }
                //2 14 23 35
            } else {
                //2
                temp = transData.getPan();
                if (!TextUtils.isEmpty(temp))
//                    jsonBody.put("F002",temp);

                //14
                temp = transData.getExpDate();
                if (!TextUtils.isEmpty(temp))
                    sendData.setF014(temp);
                //23
                temp = transData.getCardSerialNo();
                if (!TextUtils.isEmpty(temp))
                    sendData.setF023(temp);

                //35
                temp = transData.getTrack2();
                if (!TextUtils.isEmpty(temp)) {
                    AppLog.d(TAG,"Track2 == " +temp);
                    if (transData.isEncTrack()) {
                        AppLog.i("onEncTrack","DataKsn == "+transData.getDataKsn());
                        byte[] track = listener.onEncTrack(temp.getBytes());
                        if (track != null) {
//                            String sF35 = topApplication.convert.bcdToStr(track);
                            String sF35 = new String(track);
                            AppLog.i("onEncTrack","out == "+sF35);
                            sendData.setF035(sF35);
                        }
                        temp = transData.getDataKsn();
                        if (!TextUtils.isEmpty(temp)) {
                            //53 You need to send in the DE35 encrypted.With DE36 carrying the KSN value
                            sendData.setF036(temp);
                        }
                    } else {
                        sendData.setF035(temp);
                    }
                }
            }

            if (ETransType.TRANS_OFFINE_SALE == transType) {
                temp = transData.getPan();
                if (!TextUtils.isEmpty(temp)) {
                    sendData.setF002(temp);
                }
            }

            //4
//            if (transType == ETransType.TRANS_SALE_WITH_CASH){
//
//                String cashAmount = transData.getCashAmount();
//                String cardAmount = transData.getCardAmount();
//                AppLog.d("cashAmount " +cashAmount);
//                AppLog.d("cardAmount " +cardAmount);
//                Long sum =  Long.valueOf(cashAmount) + Long.valueOf(cardAmount);
//                temp = String.valueOf(sum);
//                AppLog.d("sum " +sum);
//                transData.setAmount(temp);
//                if (!TextUtils.isEmpty(temp)) {
//                    String formatAmount = String.format("%012d", Long.valueOf(temp));
//                    sendData.setF004(formatAmount);
//                }
//            }else {
//                temp = transData.getAmount();
//                if (!TextUtils.isEmpty(temp)) {
//                    String formatAmount = String.format("%012d", Long.valueOf(temp));
//                    sendData.setF004(formatAmount);
//                }
//            }
            temp = transData.getAmount();
            if (!TextUtils.isEmpty(temp)) {
                String formatAmount = String.format("%012d", Long.valueOf(temp));
                sendData.setF004(formatAmount);
            }

            //11
            temp = String.format("%06d",transData.getTransNo());
            if (!TextUtils.isEmpty(temp))
                sendData.setF011(temp);

            //22
            temp = getInputMethod(enterMode, transData.isHasPin());
            transData.setField22(temp);
            sendData.setF022(temp);

            //49

            if (ETransType.TRANS_OFFINE_SALE != transType) {
                //52
                if (transData.isHasPin()) {
                    sendData.setF052(transData.getPin());
                    temp = transData.getPinKsn();
                    if (!TextUtils.isEmpty(temp)) {
                        //53 PLease share the KSN in the DE53.
                        sendData.setF053(temp);
                    }
                }
            }

            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransResult.ERR_PACK;
    }

    /**
     * 设置冲正公共类数据
     * 设置域
     * <p>
     * filed 2, field 4,field 11,field 14,field 22,field 23,field 38,
     * <p>
     * field 39,field 49,field 55,field 61
     * @param transData
     * @return
     */
    protected int setRevCommonData(TransData transData) {
        String temp = "",temp1 = "",temp2 = "";
        int ret = 0;
        // h, m, field 3, field 25, field 41, field 42
        ret = setMandatoryData(transData);
        if (ret != TransResult.SUCC) {
            return ret;
        }

        int enterMode = transData.getEnterMode();

        //4
        temp = transData.getAmount();
        if (!TextUtils.isEmpty(temp)) {
            String formatAmount = String.format("%012d", Long.valueOf(temp));
            sendData.setF004(formatAmount);
        }

        //11
        temp = String.format("%06d",transData.getTransNo());
        if (!TextUtils.isEmpty(temp))
            sendData.setF011(temp);

        //22
        temp = transData.getField22();
        if (!TextUtils.isEmpty(temp))
            sendData.setF022(temp);


        //39
        temp = transData.getReason();
        if (!TextUtils.isEmpty(temp))
            sendData.setF039(temp);

        //44

        //55
//        temp = transData.getSendIccData();
        temp = transData.getICPositiveData();
        if (!TextUtils.isEmpty(temp)) {
            temp1 = transData.getScriptTag();
            if (!DataUtils.isNullString(temp1)) {
                sendData.setF055(temp + temp1);
            } else {
                sendData.setF055(temp);
            }
        }

        //57
        temp = String.format("%06d",transData.getBatchNo());
        if (!TextUtils.isEmpty(temp))
            sendData.setF057(temp);

        //62
        temp = transData.getField62();
        if (TextUtils.isEmpty(temp)) {
            temp = String.format("%06d",transData.getOrigTransNo());
        }
        sendData.setF062(temp);

        return TransResult.ERR_PACK;
    }
    /**
     *
     * @param enterMode
     * @param hasPin
     * @return
     */
    protected String getInputMethod(int enterMode, boolean hasPin) {
        String inputMethod = "";
        switch (enterMode) {
            case Component.EnterMode.MANAUL:
                inputMethod = "01";
                break;
            case Component.EnterMode.SWIPE:
                inputMethod = "90";
                break;
            case Component.EnterMode.INSERT:
                inputMethod = "05";
                break;
            case Component.EnterMode.QPBOC:
                inputMethod = "07";
                break;
            case Component.EnterMode.FALLBACK:
                inputMethod = "80";
                break;
            case Component.EnterMode.PHONE:
                inputMethod = "92";
                break;
            case Component.EnterMode.MOBILE:
                inputMethod = "96";
                break;
            case Component.EnterMode.CLSS_PBOC:
                inputMethod = "98";
                break;
            case Component.EnterMode.QR:
                inputMethod = "03"; // 文档上要求04，实际上银联要求03
                break;

            default:
                break;
        }

        inputMethod += "1";

//        if (hasPin) {
//            inputMethod += "1";
//        } else {
//            inputMethod += "2";
//        }

        return inputMethod;
    }

    protected int setFinancialData(TransData transData) {
        try {
            String temp = "";
            int ret = 0;
            // h,m, field 3, field 25, field 41,field 42
            ret = setMandatoryData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            //field 2, 4, 11, 14, 22, 23, 26, 35,36,49,52, 53
            ret = setCommonData(transData);
            if (ret != TransResult.SUCC) {
                return ret;
            }
            // field 55
            temp = transData.getSendIccData();
            if (!TextUtils.isEmpty(temp)) {
                sendData.setF055(temp);
            }
            return TransResult.SUCC;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return TransResult.ERR_PACK;
    }

    @Override
    public int unpack(TransData transData, String jsonRecv) {
        String temp = "";
        ETransType transType = ETransType.valueOf(transData.getTransType());
        JsonMessage jRecv = jsonGson.fromJson(jsonRecv, JsonMessage.class);
        // 检查39域应答码
        temp = jRecv.getF039();
        if (TextUtils.isEmpty(temp)) {
            return TransResult.ERR_BAG;
        }
        transData.setResponseCode(temp);

        //检查
        int ret = checkRecvData(jRecv);
        if (ret != TransResult.SUCC) {
            return ret;
        }

        //消息类型
        temp = jRecv.getMsgType();
        AppLog.d(TAG,"unpack MsgType = " + temp );

        //3 域
        temp = jRecv.getF003();
        if (!TextUtils.isEmpty(temp)) {
            String origField3 = transData.getField3();
            if (origField3 != null && origField3.length() > 0) {
                if (!origField3.equals(temp)) {
                    return TransResult.ERR_PROC_CODE;
                }
            }
            transData.setProcCode(temp);
            AppLog.d(TAG,"unpack F003 = " + temp );
        }

        //4 域
        temp = jRecv.getF004();
        if (!TextUtils.isEmpty(temp)) {
            transData.setAmount(temp);
            AppLog.d(TAG,"unpack F004 = " + temp );
        }

        //11 域
        temp = jRecv.getF011();
        if (!TextUtils.isEmpty(temp)) {
            long transNo = Long.parseLong(temp.trim());
            if (transNo >0)
                transData.setTransNo(transNo);
            AppLog.d(TAG,"unpack F011 = " + temp );
        }

        // field 12 受卡方所在地时间
        temp = jRecv.getF012();;
        if (!TextUtils.isEmpty(temp)) {
            transData.setTime(temp);
            AppLog.d(TAG,"unpack F012 = " + temp );

        }

        // field 13 受卡方所在地日期
        // Calendar date = Calendar.getInstance();
        // String yeardate = String.valueOf(date.get(Calendar.YEAR));
        temp = jRecv.getF013();;
        if (!TextUtils.isEmpty(temp)) {
            transData.setDate(temp);

            AppLog.d(TAG,"unpack F013 = " + temp );
        }

        // field 14 卡有效期
        temp = jRecv.getF014();;
        if (!TextUtils.isEmpty(temp)) {
            if (!temp.equals("0000")) {
                transData.setExpDate(temp);
            }
            AppLog.d(TAG,"unpack F014 = " + temp );
        }

        // field 15清算日期
        temp = jRecv.getF015();;
        if (!TextUtils.isEmpty(temp)) {
            transData.setSettleDate(temp);
            AppLog.d(TAG,"unpack F015 = " + temp );
        }

        // field 22
        temp = jRecv.getF022();;
        if (!TextUtils.isEmpty(temp)) {
            transData.setField22(temp);
            AppLog.d(TAG,"unpack F022 = " + temp );
        }
        // field 23 卡片序列号
        temp = jRecv.getF023();;
        if (!TextUtils.isEmpty(temp)) {
            transData.setCardSerialNo(temp);
            AppLog.d(TAG,"unpack F023 = " + temp );
        }
        // field 25
        // field 26

        // field 32 受理方标识码
        temp = jRecv.getF032();
        if (!TextUtils.isEmpty(temp)) {
            transData.setAcqCenterCode(temp);
            AppLog.d(TAG,"unpack F032 = " + temp );
        }

        // field 35
        // field 36

        // field 37 检索参考号
        temp = jRecv.getF037();
        if (!TextUtils.isEmpty(temp)) {
            transData.setRefNo(temp);
            AppLog.d(TAG,"unpack F037 = " + temp );
        }

        // field 38 授权码
        temp = jRecv.getF038();
        if (!TextUtils.isEmpty(temp)) {
            transData.setAuthCode(temp);

            AppLog.d(TAG,"unpack F038 = " + temp );
        }

        // field 41 校验终端号
        temp = jRecv.getF041();
        if (!TextUtils.isEmpty(temp)) {
            transData.setTermID(temp);
            AppLog.d(TAG,"unpack F041 = " + temp );
        }

        // field 42 校验商户号
        temp = jRecv.getF042();
        if (!TextUtils.isEmpty(temp)) {
            transData.setMerchID(temp);
            AppLog.d(TAG,"unpack F042 = " + temp );
        }

        // field 43

        // field 44
        temp = jRecv.getF044();
        if (!TextUtils.isEmpty(temp)) {
            AppLog.d(TAG,"unpack F044 = " + temp );

        }
        //46
        temp = jRecv.getF046();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField46(temp);
            AppLog.d(TAG,"unpack F046 = " + temp );
        }
        // field 48
        temp = jRecv.getF048();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField48(temp);
            AppLog.d(TAG,"unpack F048 = " + temp );
        }

        // field 52
        temp = jRecv.getF052();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField52(temp);
            AppLog.d(TAG,"unpack F052 = " + temp );
        }

        // field 53

        // field 54
        temp = jRecv.getF054();
        if (!TextUtils.isEmpty(temp)) {
//            transData.setBalanceFlag(temp.substring(7, 8));
//            transData.setBalance(temp.substring(temp.length() - 12, temp.length()));
            AppLog.d(TAG,"unpack F054 = " + temp );
        }

        // field 55
        temp = jRecv.getF055();
        if (!TextUtils.isEmpty(temp)) {
            transData.setRecvIccData(temp);
            AppLog.d(TAG,"unpack F055 = " + temp );
        }
        // field 57
        temp = jRecv.getF057();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField57(temp);
            AppLog.d(TAG,"unpack F057 = " + temp );
        }
        // field 58
        temp = jRecv.getF058();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField58(temp);
            AppLog.d(TAG,"unpack F058 = " + temp );
        }

        // field 62
        temp = jRecv.getF062();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField62(temp);
            AppLog.d(TAG,"unpack F062 = " + temp );
        }

        temp = jRecv.getF063();
        if (!TextUtils.isEmpty(temp)) {
            transData.setField63(temp);
            AppLog.d(TAG,"unpack F063 = " + temp );
        }

        return TransResult.SUCC;
    }
}
