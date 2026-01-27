package com.topwise.premierpay.trans.action;

import android.content.Context;
import android.content.Intent;

import com.topwise.manager.card.entity.CardData;
import com.topwise.premierpay.trans.action.activity.SearchCardManagerNewActivity;
import com.topwise.premierpay.trans.core.AAction;
import com.topwise.premierpay.trans.core.ActionResult;
import com.topwise.premierpay.trans.model.EUIParamKeys;

public class ActionSearchCard extends AAction {
    private Context context;
    private byte mode;
    private boolean issecond;
    private String title;
    private String amount;
    private String cashAmount;
    private boolean isAutoTest = false;

    /**
     * 子类构造方法必须调用super设置ActionStartListener
     *
     * @param listener {@link ActionStartListener}
     */
    public ActionSearchCard(ActionStartListener listener) {
        super(listener);
    }

    /**
     * 设置action运行时参数
     * @param context
     * @param title
     * @param mode
     * @param amount
     */
    public void setParam(Context context, String title, byte mode, String amount) {
        this.context = context;
        this.title = title;
        this.mode = mode;
        this.amount = amount;
        this.issecond = false;
    }

    public void setParam(Context context,boolean isAutoTest, String title, byte mode, String amount) {
        this.context = context;
        this.isAutoTest = isAutoTest;
        this.title = title;
        this.mode = mode;
        this.amount = amount;
        this.issecond = false;
    }

    public void setParam(Context context, String title, byte mode, String amount,boolean issecond) {
        this.context = context;
        this.title = title;
        this.mode = mode;
        this.amount = amount;
        this.issecond = issecond;
    }

    public void setParam(Context context, String title, byte mode, String amount, String cashAmount) {
        this.context = context;
        this.title = title;
        this.mode = mode;
        this.amount = amount;
        this.cashAmount = cashAmount;
        this.issecond = false;
    }

    @Override
    protected void process() {
//      Intent intent = new Intent(context, SearchCardActivity.class);
//        Intent intent = new Intent(context, SearchCardManagerActivity.class);
        Intent intent = new Intent(context, SearchCardManagerNewActivity.class);
        intent.putExtra(EUIParamKeys.NAV_TITLE.toString(), title);
        intent.putExtra(EUIParamKeys.NAV_BACK.toString(), !isAutoTest);
        intent.putExtra(EUIParamKeys.TIKE_TIME.toString(), issecond);
        intent.putExtra(EUIParamKeys.TRANS_AMOUNT.toString(), amount);
        intent.putExtra(EUIParamKeys.TRANS_AMOUNT_CASH.toString(), cashAmount);
        intent.putExtra(EUIParamKeys.CARD_SEARCH_MODE.toString(), mode);
        context.startActivity(intent);
    }

    @Override
    public void setResult(ActionResult result) {
        super.setResult(result);
        context = null;
    }

    /**
     * 寻卡类型定义
     *
     * @author Steven.W
     *
     */
    public static class SearchMode {
        /**
         * 刷卡
         */
        public static final byte SWIPE = 0x01;
        /**
         * 插卡
         */
        public static final byte INSERT = 0x02;
        /**
         * 挥卡
         */
        public static final byte TAP = 0x04;
        /**
         * 支持手输
         */
        public static final byte KEYIN = 0x08;
        /**
         * 扫码支付
         */
        public static final byte QR = 0x03;

        /**
         * 外置非接
         */
        public static final byte PICCEXTERNAL = 0x08;
    }

    public static class CardInformation {
        private byte searchMode;
        private String track1;
        private String track2;
        private String track3;
        private String pan;
        private String ExpDate;
        private String serviceCode;

        public CardInformation(byte mode, String track1, String track2, String track3, String pan) {
            this.searchMode = mode;
            this.track1 = track1;
            this.track2 = track2;
            this.track3 = track3;
            this.pan = pan;
        }

        public CardInformation(byte mode) {
            this.searchMode = mode;
        }

        public CardInformation(byte mode, CardData cardData) {
            this.searchMode = mode;
            this.pan = cardData.getPan();
            this.track1 = cardData.getTrack1();
            this.track2 = cardData.getTrack2();
            this.track3 = cardData.getTrack3();
            this.ExpDate = cardData.getExpiryDate();
            this.serviceCode = cardData.getServiceCode();
        }

        public CardInformation(byte mode, String pan) {
            this.searchMode = mode;
            this.pan = pan;
        }

        public CardInformation() {

        }

        public byte getSearchMode() {
            return searchMode;
        }

        public void setSearchMode(byte searchMode) {
            this.searchMode = searchMode;
        }

        public String getTrack1() {
            return track1;
        }

        public void setTrack1(String track1) {
            this.track1 = track1;
        }

        public String getTrack2() {
            return track2;
        }

        public void setTrack2(String track2) {
            this.track2 = track2;
        }

        public String getTrack3() {
            return track3;
        }

        public void setTrack3(String track3) {
            this.track3 = track3;
        }

        public String getPan() {
            return pan;
        }

        public void setPan(String pan) {
            this.pan = pan;
        }

        public String getExpDate() {
            return ExpDate;
        }

        public void setExpDate(String expDate) {
            ExpDate = expDate;
        }
        public String getServiceCode() {
            return serviceCode;
        }

        public void setServiceCode(String serviceCode) {
            this.serviceCode = serviceCode;
        }
    }
}
