package com.topwise.premierpay.trans.model;

/**
 * 创建日期：2021/4/6 on 16:29
 * 描述: 交易步骤枚举
 * 作者:  wangweicheng
 */
public enum State {
    CHECK_PWD,      //验证
    ENTER_SCAN,      //
    ENTER_DATA,      //获取流水号
    ENTER_AMOUNT, //输入金额
    ENTER_AMOUNT_2, //再次输入金额
    CHECK_CARD,     //检卡
    CHECK_CARD_2,     //检卡
    ENTER_PIN,      //输密码
    ONLINE,         //联机
    ONLINE_2,       //第二次联机
    EMV_PROC,       //EMV 流程
    TRANS_STATE,    //显示交易状态
    TRANS_DETAIL,   //显示交易详情
    CHECK_CARD_NO,   //确认卡号
    ELEC_SIGN,      //电子签名
    FINGER_PRINT,      //指纹

    CHECK_TURN_SCREEN,   //转界面
    BT_CONNECT ,   //bt connect

    BT_UPLOAD ,   //bt upload
    APP_UPGRADE ,   //App Upgrade
    SHOW_MESSAGE,
    SHOW_CODE,

}
