package com.topwise.premierpay.trans.receipt;

/**
 * 创建日期：2021/4/2 on 14:07
 * 描述:
 * 作者:  wangweicheng
 */
public interface PrintListener {
    public static final int CONTINUE = 0;
    public static final int CANCEL = 1;
    /**
     * 打印提示信息
     *
     * @param title
     * @param message
     */
    public void onShowMessage(String title, String message);

    /**
     * 打印机异常确认
     *
     * @param title
     * @param message
     * @return {@link PrintListener#CONTINUE}/{@link PrintListener#CANCEL}
     */
    public int onConfirm(String title, String message);

    public int onConfirmNext(String title, String message);
    public void onEnd();

}
