package com.topwise.manager.card.api;


import com.topwise.manager.card.impl.CardReader;

/**
 * 创建日期：2021/6/9 on 13:40
 * 描述:
 * 作者:wangweicheng
 */
public interface ICardReader {

    /**
     * Start searching card
     *
     * @param isMag support magnetic card or not
     * @param isIcc support contact ic card or not
     * @param isRf  support contactless ic card or not
     * @param outtime 单位秒/Unit second
     * @param onReadCardListener
     */
    void startFindCard(boolean isMag, boolean isIcc, boolean isRf, int outtime, CardReader.onReadCardListener onReadCardListener);

    /**
     * Cancel search card
     */
    void cancel();
}
