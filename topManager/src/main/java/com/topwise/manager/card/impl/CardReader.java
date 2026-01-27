package com.topwise.manager.card.impl;

import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.card.AidlCheckCard;
import com.topwise.cloudpos.aidl.card.AidlCheckCardListener;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.magcard.AidlMagCard;
import com.topwise.cloudpos.aidl.magcard.TrackData;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.manager.AppLog;
import com.topwise.manager.TopUsdkManage;

import com.topwise.manager.card.api.ICardReader;
import com.topwise.manager.card.entity.CardData;

/**
 * 创建日期：2021/6/9 on 13:50
 * 描述: 单例实现寻卡
 * 作者:wangweicheng
 */
public class CardReader implements ICardReader {
    private static final String TAG = CardReader.class.getSimpleName();
    private static CardReader instance;
    private CardData cardData;

    private onReadCardListener onReadCardListener;
    private AidlMagCard magCard = TopUsdkManage.getInstance().getMag();
    private AidlICCard icCard = TopUsdkManage.getInstance().getIcc();
    private AidlRFCard rfCard = TopUsdkManage.getInstance().getRf();
    private AidlShellMonitor aidlShellMonitor = TopUsdkManage.getInstance().getShellMonitor();

    private CardReader() {

    }

    public synchronized static CardReader getInstance() {
        if (instance == null) {
            instance = new CardReader();
        }
        return instance;
    }

    /**
     * open MAG
     * @return
     */
    private boolean openMag() {
        try {
            return magCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "openMag: false ==============");
            return false;
        }
    }

    /**
     * close Mag
     * @return
     */
    private boolean closeMag() {
        try {
            return magCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "closeMag: false ==============");
            return false;
        }
    }

    /**
     * open IC
     * @return
     */
    private boolean openIc() {
        try {
            return icCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "openIc: false ==============");
            return false;
        }
    }

    /**
     * closeIC
     * @return
     */
    private boolean closeIc() {
        try {
            return icCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "closeIc: false ==============");
            return false;
        }
    }

    /**
     * open RF
     * @return
     */
    private boolean openRf() {
        try {
            return rfCard.open();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "openRf: false ==============");
            return false;
        }
    }

    /**
     * close RF
     * @return
     */
    private boolean closeRf() {
        try {
            return rfCard.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            AppLog.e(TAG,   "closeRf: false ==============");
            return false;
        }
    }

    @Override
    public void startFindCard(boolean isMag, boolean isIcc, boolean isRf, int outTime,
                              final onReadCardListener onReadCardListener) {
        if (onReadCardListener == null) {
            return;
        }

        this.onReadCardListener = onReadCardListener;

        AppLog.e(TAG, "startFindCard: isMag = " + isMag + " isIcc = " + isIcc + " isRf = " + isRf + " outTime = " + outTime);

        try {
            TopUsdkManage.getInstance().getCheckCard().checkCard(isMag, isIcc, isRf, outTime*1000, new AidlCheckCardListener.Stub() {
                @Override
                public void onFindMagCard(TrackData trackData) throws RemoteException {
                    cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.MAG);
                    cardData.setPan(trackData.getCardno());
                    cardData.setServiceCode(trackData.getServiceCode());
                    cardData.setExpiryDate(trackData.getExpiryDate());
                    cardData.setTrack1(trackData.getFirstTrackData());
                    cardData.setTrack2(trackData.getSecondTrackData());
                    cardData.setTrack3(trackData.getThirdTrackData());
                    setResult(cardData);
                }

                @Override
                public void onSwipeCardFail() throws RemoteException {
                    cardData = new CardData(CardData.EReturnType.OTHER_ERR, CardData.ECardType.MAG);
                    setResult(cardData);
                }

                @Override
                public void onFindICCard() throws RemoteException {
                    cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.IC);
                    setResult(cardData);
                }

                @Override
                public void onFindRFCard() throws RemoteException {
                    Log.v("Jeremy "," ===== onFindRFCard ============ ");
                    cardData = new CardData(CardData.EReturnType.OK, CardData.ECardType.RF);
                    setResult(cardData);
                }

                @Override
                public void onTimeout() throws RemoteException {
                    cardData = new CardData(CardData.EReturnType.TIMEOUT, CardData.ECardType.IC);
                    setResult(cardData);
                }

                @Override
                public void onError(int i) throws RemoteException {
                    Log.v("Jeremy "," ===== onError ============ " + i);

                    if (i == CardData.EReturnType.RF_MULTI_CARD.ordinal()) {
                        onReadCardListener.onNotification(CardData.EReturnType.RF_MULTI_CARD);
                    } else if (i == CardData.EReturnType.TIMEOUT.ordinal()) {
                        onReadCardListener.onNotification(CardData.EReturnType.TIMEOUT);
                    } else {
                        cardData = new CardData(CardData.EReturnType.OTHER_ERR, CardData.ECardType.IC);
                        setResult(cardData);
                    }
                }

                @Override
                public void onCanceled() throws RemoteException {

                }
            });
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancel() {
        try {
            TopUsdkManage.getInstance().getCheckCard().cancelCheckCard();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set card data and notice app
     * @param cardData
     */
    private void setResult(CardData cardData) {
        if (onReadCardListener!= null) {
            onReadCardListener.getReadState(cardData);
        }
        onReadCardListener = null;
    }

    /**
     * close all card readers
     */
    private void CloseAll(){
        closeIc();
        closeMag();
        closeRf();
        AppLog.e(TAG,   "CloseAll ===");
    }

    public interface onReadCardListener {
        /**
         * Success to read card
         * @param cardData
         */
        void getReadState(CardData cardData);

        /**
         * Notice app about return type
         * @param eReturnType
         */
        void onNotification(CardData.EReturnType eReturnType);
    }
}
