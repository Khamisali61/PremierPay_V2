package com.topwise.premierpay.trans.model;

import com.topwise.premierpay.R;
import com.topwise.premierpay.app.TopApplication;
import com.topwise.premierpay.pack.iso8583.PackAuth;
import com.topwise.premierpay.pack.iso8583.PackAuthCmd;
import com.topwise.premierpay.pack.iso8583.PackAuthVoid;
import com.topwise.premierpay.pack.iso8583.PackBalance;
import com.topwise.premierpay.pack.iso8583.PackBatchUp;
import com.topwise.premierpay.pack.iso8583.PackBatchUpNotice;
import com.topwise.premierpay.pack.iso8583.PackCousume;
import com.topwise.premierpay.pack.iso8583.PackDownloadParam;
import com.topwise.premierpay.pack.iso8583.PackEcho;
import com.topwise.premierpay.pack.iso8583.PackEmvParamDownload;
import com.topwise.premierpay.pack.iso8583.PackEmvParamQuery;
import com.topwise.premierpay.pack.iso8583.PackIcTcBat;
import com.topwise.premierpay.pack.iso8583.PackIso8583;
import com.topwise.premierpay.pack.PackListener;
import com.topwise.premierpay.pack.iso8583.PackLogon;
import com.topwise.premierpay.pack.iso8583.PackOfflineBat;
import com.topwise.premierpay.pack.iso8583.PackOfflineTransSend;
import com.topwise.premierpay.pack.iso8583.PackQrRefund;
import com.topwise.premierpay.pack.iso8583.PackQrSale;
import com.topwise.premierpay.pack.iso8583.PackQrvoid;
import com.topwise.premierpay.pack.iso8583.PackRefund;
import com.topwise.premierpay.pack.iso8583.PackReveral;
import com.topwise.premierpay.pack.iso8583.PackSettle;
import com.topwise.premierpay.pack.iso8583.PackVoid;
import com.topwise.premierpay.pack.json.JsonPackHandshake;
import com.topwise.premierpay.pack.json.JsonPackLogOn;
import com.topwise.premierpay.pack.json.JsonPackLstStatus;
import com.topwise.premierpay.pack.json.JsonPackNetWork;
import com.topwise.premierpay.pack.json.JsonPackOfflineSale;
import com.topwise.premierpay.pack.json.JsonPackParamDownload;
import com.topwise.premierpay.pack.json.JsonPackPreAuth;
import com.topwise.premierpay.pack.json.JsonPackPreAuthCompletion;
import com.topwise.premierpay.pack.json.JsonPackSale;
import com.topwise.premierpay.pack.json.JsonPackTmk;
import com.topwise.premierpay.pack.json.PackJson;
import com.topwise.premierpay.pack.json.JsonPackReveral;

public enum ETransType {

    /************************************************ 交易类 ****************************************************/

    CONSUME("0200", "0210", "000000", "00", "22", "001", TopApplication.mApp.getString(R.string.title_consume), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /*********************************************
     *  管理类交易
     *********************************************/
    TRANS_HANDSHAKE("0800", "0810", "990000", "00", "22", "001", TopApplication.mApp.getString(R.string.title_consume), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackHandshake(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_NETWORK("0800", "0810", "990000", "00", "22", "001", TopApplication.mApp.getString(R.string.app_heartbeat_message), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackNetWork(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_GET_TMK("0800", "0810", "910000", "00", "22", "001", TopApplication.mApp.getString(R.string.title_consume), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackTmk(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    // 签到
    LOGON("0800", "0810", "", "", "00", "001", TopApplication.mApp.getString(R.string.log_in), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackLogon(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackLogOn(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * 获取公钥
     */
    LOGON1("0800", "0810", "930000", "00", "00", "000", TopApplication.mApp.getString(R.string.log_in), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackLogOn(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    // 签到
    LOGOUT("0800", "0810", "940000", "00", "00", "000", TopApplication.mApp.getString(R.string.log_out), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * 参数下载
     */
    PARAM_DOWNLOAD("0300", "0310", "000000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_parameter_download), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackParamDownload(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡公钥下载状态上送
     */
    EMV_MON_CA("0820", "", "", "", "00", "372", TopApplication.mApp.getString(R.string.emv_mon_ca), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamQuery(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡公钥下载
     */
    EMV_CA_DOWN("0800", "", "", "", "00", "370", TopApplication.mApp.getString(R.string.emv_ca_down), false, false, false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡公钥下载结束
     */
    EMV_CA_DOWN_END("0800", "", "", "", "00", "371", TopApplication.mApp.getString(R.string.emv_ca_down_end), false, false, false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡参数下载状态上送
     */
    EMV_MON_PARAM("0820", "", "", "", "00", "382", TopApplication.mApp.getString(R.string.emv_mon_param), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamQuery(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡AID参数下载
     */
    EMV_PARAM_DOWN("0800", "", "", "", "00", "380", TopApplication.mApp.getString(R.string.emv_param_down), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * IC卡AID参数下载结束
     */
    EMV_PARAM_DOWN_END("0800", "", "", "", "00", "381", TopApplication.mApp.getString(R.string.emv_param_down_end), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEmvParamDownload(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * 非接参数下载
     */
    PICC_DOWNLOAD_PARAM("0800", "", "", "", "00", "394", TopApplication.mApp.getString(R.string.picc_download_param), false, false, false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackDownloadParam(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * 非接参数下载结束
     */
    PICC_DOWNLOAD_PARAM_END("0800", "", "", "", "00", "395", TopApplication.mApp.getString(R.string.picc_download_param_end), false, false, false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackDownloadParam(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * 回响功能
     */
    ECHO("0820", "", "", "", "00", "301", TopApplication.mApp.getString(R.string.echo), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackEcho(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * settle
     */
    TRANS_SETTLE("0500", "", "", "", "00", "201", TopApplication.mApp.getString(R.string.settle), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackSettle(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }
        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /**
     * 通知类交易披上送，包括 退货、预授权完成通知、离线结算、结算调整、结算调整小费
     */
    NOTICE_TRANS_BAT("0320", "", "200000", "00", "25", "000", TopApplication.mApp.getString(R.string.batch_up), false, false, false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBatchUpNotice(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /*********************************************
     *  消费交易
     *********************************************/
    TRANS_SALE("0200", "0420", "000000", "00", "22", "000", TopApplication.mApp.getString(R.string.title_sale), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    /*********************************************
     *  自动消费测试
     *********************************************/
    TRANS_AUTO_SALE("0200", "0210", "000000", "00", "22", "000", TopApplication.mApp.getString(R.string.title_sale_test), false, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_QR_SALE("0200", "0400", "000000", "00", "22", "000", TopApplication.mApp.getString(R.string.app_qr_sale), true, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackQrSale(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_QR_VOID("0200", "0400", "200000", "00", "23", "000", TopApplication.mApp.getString(R.string.app_qr_void), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackQrvoid(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_QR_REFUND("0220", "", "200000", "00", "25", "000", TopApplication.mApp.getString(R.string.app_qr_refund), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackQrRefund(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_OFFINE_SALE("0220", "0000", "000000", "00", "00", "000", TopApplication.mApp.getString(R.string.title_offline_sale), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackOfflineSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_VOID("0200", "0400", "200000", "00", "23", "000", TopApplication.mApp.getString(R.string.app_void), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackVoid(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_SALE_WITH_CASH("0200", "0420", "090000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_cash_withdrawal), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_CASH("0200", "0420", "090000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_cash_deposit), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_PAYMENT("0200", "0420", "090000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_payment), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_BUY("0200", "0420", "090000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_buy), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_TRANSFER("0200", "0420", "090000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_transfer), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackCousume(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_REFUND("0200", "0420", "200000", "00", "25", "000", TopApplication.mApp.getString(R.string.app_refund), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackRefund(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_PRE_AUTH("0200", "0420", "300000", "01", "00", "000", TopApplication.mApp.getString(R.string.auth_trans), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackAuth(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackPreAuth(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_PRE_AUTH_CMP("0200", "0420", "400000", "01", "00", "000", TopApplication.mApp.getString(R.string.auth_cm), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackAuthCmd(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackPreAuthCompletion(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_PRE_AUTH_VOID("0200", "0420", "500000", "01", "00", "000", TopApplication.mApp.getString(R.string.auth_void), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return new PackAuthVoid(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return new PackReveral(listener);
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackPreAuthCompletion(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return new JsonPackReveral(listener);
        }
    },

    TRANS_LAST_STATUS("0620", "0110", "990000", "00", "00", "000", TopApplication.mApp.getString(R.string.app_lsat_query), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackLstStatus(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    BALANCE("0200", "0210", "310000", "00", "01", "000", TopApplication.mApp.getString(R.string.title_balance), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBalance(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    TRANS_LINK("0200", "0210", "610000", "00", "00", "000", TopApplication.mApp.getString(R.string.title_sale_link), true, true, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    BHARAT_QR("0200", "0210", "630000", "00", "00", "000", TopApplication.mApp.getString(R.string.title_sale_link), false, false, false) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {

            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {

            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return new JsonPackSale(listener);
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    OFFLINE_TRANS_SEND_BAT("0320", "", "000000", "00", "36", "000", TopApplication.mApp.getString(R.string.offline_trans_send_bat), true, true, true) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackOfflineBat(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    OFFLINE_TRANS_SEND("0200", "", "000000", "00", "36", "000", TopApplication.mApp.getString(R.string.offline_trans_send), true, true, true) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackOfflineTransSend(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    BATCH_UP_END("0320", "", "", "", "00", "207", TopApplication.mApp.getString(R.string.batch_up_end), false,
            false, false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBatchUp(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }

    },

    BATCH_UP("0320", "", "", "", "00", "201", TopApplication.mApp.getString(R.string.batch_up), false, false,
            false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackBatchUp(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }

    },

    IC_FAIL_BAT("0320", "", "", "", "00", "204", TopApplication.mApp.getString(R.string.batch_up), false, false,
            false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackIcTcBat(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }

    },

    IC_TC_BAT("0320", "", "", "", "00", "203", TopApplication.mApp.getString(R.string.batch_up), false, false,
            false) {

        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return new PackIcTcBat(listener);
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }

    },

    BT_CONNECT( TopApplication.mApp.getString(R.string.bluetooth_connect)) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },
    TRANS_QR_CODE( TopApplication.mApp.getString(R.string.title_qr_code)) {
        @Override
        public PackIso8583 getpackager(PackListener listener) {
            return null;
        }

        @Override
        public PackIso8583 getDupPackager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getpackJsonager(PackListener listener) {
            return null;
        }

        @Override
        public PackJson getDupPackJsonager(PackListener listener) {
            return null;
        }
    },

    ;

    private String msgType;
    private String dupMsgType;
    private String procCode;
    private String serviceCode;
    private String funcCode;
    private String netCode;
    private String transName;
    private boolean isDupSend;
    private boolean isScriptSend;
    private boolean isOfflineSend;

    /**
     *
     * @param msgType
     *            ：消息类型码
     * @param dupMsgType
     *            :冲正消息类型码
     * @param procCode
     *            : 3域 处理码
     * @param serviceCode
     *            ：25 服务码
     * @param funcCode
     *            ：60.1功能码
     * @param netCode
     *            ：60.3网络管理码
     * @param transName
     *            :交易名称
     * @param isDupSend
     *            ：是否冲正上送
     * @param isScriptSend
     *            ：是否脚本结果上送
     * @param isOfflineSend
     *            ：是否脱机交易上送
     */
    private ETransType(String msgType, String dupMsgType, String procCode, String serviceCode, String funcCode, String netCode, String transName, boolean isDupSend, boolean isScriptSend, boolean isOfflineSend) {
        this.msgType = msgType;
        this.dupMsgType = dupMsgType;
        this.procCode = procCode;
        this.serviceCode = serviceCode;
        this.funcCode = funcCode;
        this.netCode = netCode;
        this.transName = transName;
        this.isDupSend = isDupSend;
        this.isScriptSend = isScriptSend;
        this.isOfflineSend = isOfflineSend;
    }

    private ETransType(String transName) {
        this.transName = transName;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getDupMsgType() {
        return dupMsgType;
    }

    public void setDupMsgType(String dupMsgType) {
        this.dupMsgType = dupMsgType;
    }

    public String getProcCode() {
        return procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public String getNetCode() {
        return netCode;
    }

    public void setNetCode(String netCode) {
        this.netCode = netCode;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public boolean isDupSend() {
        return isDupSend;
    }

    public void setDupSend(boolean dupSend) {
        isDupSend = dupSend;
    }

    public boolean isScriptSend() {
        return isScriptSend;
    }

    public void setScriptSend(boolean scriptSend) {
        isScriptSend = scriptSend;
    }

    public boolean isOfflineSend() {
        return isOfflineSend;
    }

    public void setOfflineSend(boolean offlineSend) {
        isOfflineSend = offlineSend;
    }

    public abstract PackIso8583 getpackager(PackListener listener);

    public abstract PackIso8583 getDupPackager(PackListener listener);

    public abstract PackJson getpackJsonager(PackListener listener);

    public abstract PackJson getDupPackJsonager(PackListener listener);
}
