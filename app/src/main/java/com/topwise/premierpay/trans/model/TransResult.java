package com.topwise.premierpay.trans.model;

import android.content.Context;
import android.content.res.Resources;

import com.topwise.premierpay.R;

public class TransResult {
    /**
     * 交易成功
     */
    public static final int SUCC = 0;
    /**
     * 超时
     */
    public static final int ERR_TIMEOUT = -1;
    /**
     * 连接超时
     */
    public static final int ERR_CONNECT = -2;
    /**
     * 发送失败
     */
    public static final int ERR_SEND = -3;
    /**
     * 接收失败
     */
    public static final int ERR_RECV = -4;
    /**
     * 打包失败
     */
    public static final int ERR_PACK = -5;
    /**
     * 解包失败
     */
    public static final int ERR_UNPACK = -6;
    /**
     * 非法包
     */
    public static final int ERR_BAG = -7;
    /**
     * 解包mac错
     */
    public static final int ERR_MAC = -8;
    /**
     * 处理码不一致
     */
    public static final int ERR_PROC_CODE = -9;
    /**
     * 消息类型不一致
     */
    public static final int ERR_MSG = -10;
    /**
     * 交易金额不符
     */
    public static final int ERR_TRANS_AMT = -11;
    /**
     * 流水号不一致
     */
    public static final int ERR_TRACE_NO = -12;
    /**
     * 终端号不一致
     */
    public static final int ERR_TERM_ID = -13;
    /**
     * 商户号不一致
     */
    public static final int ERR_MERCH_ID = -14;
    /**
     * 无交易
     */
    public static final int ERR_NO_TRANS = -15;
    /**
     * 无原始交易
     */
    public static final int ERR_NO_ORIG_TRANS = -16;
    /**
     * 此交易已撤销
     */
    public static final int ERR_HAS_VOID = -17;
    /**
     * 此交易不可撤销
     */
    public static final int ERR_VOID_UNSUPPORT = -18;
    /**
     * 打开通讯口错误
     */
    public static final int ERR_COMM_CHANNEL = -19;
    /**
     * 失败
     */
    public static final int ERR_HOST_REJECT = -20;
    /**
     * 交易终止（终端不需要提示信息）
     */
    public static final int ERR_ABORTED = -21;
    /**
     * 预处理相关 终端未签到
     */
    public static final int ERR_NOT_LOGON = -22;
    /**
     * 预处理相关 交易笔数超限，立即结算
     */
    public static final int ERR_NEED_SETTLE_NOW = -23;
    /**
     * 预处理相关 交易笔数超限，稍后结算
     */
    public static final int ERR_NEED_SETTLE_LATER = -24;
    /**
     * 预处理相关 存储空间不足
     */
    public static final int ERR_NO_FREE_SPACE = -25;
    /**
     * 预处理相关 终端不支持该交易
     */
    public static final int ERR_NOT_SUPPORT_TRANS = -26;
    /**
     * 卡号不一致
     */
    public static final int ERR_CARD_NO = -27;
    /**
     * 密码错误
     */
    public static final int ERR_PASSWORD = -28;
    /**
     * 参数错误
     */
    public static final int ERR_PARAM = -29;

    /**
     * 终端批上送未完成
     */
    public static final int ERR_BATCH_UP_NOT_COMPLETED = -31;
    /**
     * 金额超限
     */
    public static final int ERR_AMOUNT = -33;
    /**
     * 工作密钥长度错误
     */
    public static final int ERR_TWK_LENGTH = -39;

    /**
     * 主管密码错
     */
    public static final int ERR_SUPPWD_WRONG = -40;
    /**
     * 主管密码错
     */
    public static final int ERR_LOG_ON_ERR = -41;

    /**
     * 接收数据有误
     */
    public static final int ERR_RECORD_DATA = -42;

    public static final int ERR_WRITE_PIN = -43;
    public static final int ERR_WRITE_MAK = -44;
    public static final int ERR_WRITE_TDK = -45;

    /**
     *
     */
    public static final int ERR_EMV_INIT = -46;
    public static final int ERR_NO_NET = -47;
    public static final int CONTINUE = -48;
    public static final int ERR_CHECK_CARD = -49;
    public static final int ERR_CANCEL = -59;
    public static final int ERR_NO_BT= -60;
    public static final int ERR_NO_BT_MODE= -61;
    public static final int ERR_BT_NO_OPEN= -62;

    public static final int ERR_RF_MULTI_CARD = -63;
    public static final int ERR_UNKOWN = -64;
    public static final int ERR_NEED_INSTER_CARD = -65;
    public static final int ERR_NEED_ENTER_PWD = -66;

    public static String getMessage(Context context, int ret) {
        String message = "Unknown Error";
        if (context == null) {
            return message;
        }
        Resources resource = context.getResources();
        switch (ret){
            case SUCC:
                message = resource.getString(R.string.result_sucess);
                break;
            case ERR_CONNECT:
                message = resource.getString(R.string.result_error_conn);
                break;
            case ERR_SEND:
                message = resource.getString(R.string.result_error_send);
                break;
            case ERR_RECV:
                message = resource.getString(R.string.result_error_rece);
                break;
            case ERR_PACK:
                message = resource.getString(R.string.result_error_pack);
                break;
            case ERR_UNPACK:
                message = resource.getString(R.string.result_error_unpack);
                break;
            case ERR_NOT_LOGON:
                message = resource.getString(R.string.err_not_logon);
                break;
            case ERR_NO_TRANS:
                message = resource.getString(R.string.err_no_record);
                break;
            case ERR_SUPPWD_WRONG:
                message = resource.getString(R.string.err_suppwd_wrong);
                break;
            case ERR_VOID_UNSUPPORT:
                message = "The transaction cannot be voided";
                break;
            case ERR_CARD_NO:
                message = "The card numbers don't match!";
                break;
            case ERR_NOT_SUPPORT_TRANS:
                message = "Do not support this transaction!";
                break;
            case ERR_LOG_ON_ERR:
                message = "Log On decryption failed!";
                break;
            case ERR_RECORD_DATA:
                message = "Record data error!";
                break;
            case ERR_WRITE_PIN:
                message = "PIN DATA KCV ERROR!";
                break;
            case ERR_WRITE_MAK:
                message = "MAK DATA KCV ERROR!";
                break;
            case ERR_WRITE_TDK:
                message = "TDK DATA KCV ERROR!";
                break;
            case ERR_NO_ORIG_TRANS:
                message = "No original record!";
                break;
            case ERR_HAS_VOID:
                message = "The transaction has been cancelled!";
                break;
            case ERR_EMV_INIT:
                message = "Err emv init!";
                break;
            case ERR_TIMEOUT:
                message = "Time Out";
                break;
            case ERR_NO_NET:
                message = "No Network";
                break;
            case ERR_CHECK_CARD:
                message = "Read Card Error";
                break;
            case ERR_NO_BT:
                message = "No Bluetooth Device";
                break;
            case ERR_NO_BT_MODE:
                message = "Please Connect Bluetooth";
                break;
            case ERR_BT_NO_OPEN:
                message = "Please Open Bluetooth";
                break;
            case ERR_HOST_REJECT:
                message = "Rejected By Host";
                break;

            default:
                message = resource.getString(R.string.result_error_unkown);
                break;
        }
        return message;
    }
}
