package com.topwise.premierpay.trans.model;

import com.tencent.mmkv.MMKV;

public class Controller {
    public static class Constant {
        public static final int YES = 1;
        public static final int NO = 0;
        /**
         * 批上送类型
         */
        public static final int RMBLOG = 1;
        public static final int FRNLOG = 2;
        public static final int ALLLOG = 3;
        public static final int ICLOG = 4;
        /**
         * 批上送状态
         */
        public static final int WORKED = 0;
        public static final int BATCH_UP = 1;
    }

    /**
     * 报文头处理要求A(1-8)
     */
    public static final String HEADER_PROC_REQ_A = "head_proc_req_A";
    /**
     * 报文头处理要求B(9-16)
     */
    public static final String HEADER_PROC_REQ_B = "header_proc_req_B";
    /**
     * 是否需要下载capk NO:不需要 YES:需要
     */
    public static final String NEED_DOWN_CAPK = "need_down_capk";
    /**
     * 是否需要下载aid NO:不需要 YES:需要
     */
    public static final String NEED_DOWN_AID = "need_down_aid";
    /**
     * 是否需要下载非接业务参数
     */
    public static final String NEED_DOWN_CLPARA = "need_down_clpara";
    /**
     * 是否需要下载卡BINB
     */
    public static final String NEED_DOWN_CLBINB = "need_down_clbinb";
    /**
     * 是否需要下载卡BINC
     */
    public static final String NEED_DOWN_CLBINC = "need_down_clbinc";
    /**
     * 是否需要下载黑名单 NO:不需要 YES:需要
     */
    public static final String NEED_DOWN_BLACK = "need_down_black";
    /**
     * 终端签到状态 NO:未签到 YES:已签到
     */
    public static final String POS_LOGON_STATUS = "pos_logon_status";
    /**
     * 操作员签到状态 NO:未签到 YES:已签到
     */
    public static final String OPERATOR_LOGON_STATUS = "operator_logon_status";

    public static final String CUR_OPERID = "operator";
    /**
     * 批上送状态 {@link Constant#WORKED}未进行批上送 , {@link Constant#BATCH_UP}:处于批上送状态
     */
    public static final String BATCH_UP_STATUS = "batch_up_status";
    /**
     * 批上送类型 RMBLOG: 上送内卡交易 FRNLOG 上送外卡交易 ALLLOG 上送所有交易 ICLOG 上送IC卡交易
     */
    public static final String BATCH_UP_TYPE = "batch_up_type";
    /**
     * 外卡对账结果
     */
    public static final String FRN_RESULT = "frnResult";
    /**
     * 内卡对账结果
     */
    public static final String RMB_RESULT = "rmbResult";
    /**
     * 批上送笔数
     */
    public static final String BATCH_NUM = "batch_num";
    /**
     * 是否需要清除交易记录: NO:不清除, YES:清除
     */
    public static final String CLEAR_LOG = "clearLog";

    private static final String fileName = "control";

    private static MMKV mkv;

    private Controller() {
        mkv = MMKV.mmkvWithID(fileName);

        boolean b = mkv.containsKey(BATCH_UP_STATUS);
        if (b ) return;

        mkv.encode(HEADER_PROC_REQ_A, Constant.NO);
        mkv.encode(HEADER_PROC_REQ_A, Constant.NO);
        mkv.encode(HEADER_PROC_REQ_B, Constant.NO);
        mkv.encode(NEED_DOWN_CAPK, Constant.YES);
        mkv.encode(NEED_DOWN_AID, Constant.YES);
        mkv.encode(NEED_DOWN_CLPARA, Constant.YES);
        mkv.encode(NEED_DOWN_CLBINB, Constant.YES);
        mkv.encode(NEED_DOWN_CLBINC, Constant.YES);
        mkv.encode(NEED_DOWN_BLACK, Constant.YES);
        mkv.encode(POS_LOGON_STATUS, Constant.NO);
        mkv.encode(OPERATOR_LOGON_STATUS, Constant.NO);
        mkv.encode(BATCH_UP_STATUS, Constant.NO);
    }

    public static Controller getInstance() {
        return Controller.SingletonHolder.sInstance;
    }

    //静态内部类
    private static class SingletonHolder {
        private static final Controller sInstance = new Controller();
    }

    public static int get(String key) {
        return mkv.decodeInt(key, 0);
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void set(String key, Object object) {
        if (object instanceof String) {
            mkv.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mkv.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mkv.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mkv.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mkv.encode(key, (Long) object);
        } else if (object instanceof Double) {
            mkv.encode(key, (Double) object);
        } else if (object instanceof byte[]) {
            mkv.encode(key, (byte[]) object);
        } else {
            mkv.encode(key, object.toString());
        }
    }

    public static String getString(String key) {
        return mkv.decodeString(key, "");
    }
}
