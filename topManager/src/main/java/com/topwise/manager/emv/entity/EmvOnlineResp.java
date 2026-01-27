package com.topwise.manager.emv.entity;

import com.topwise.manager.emv.enums.EOnlineResult;
import com.topwise.toptool.impl.TopTool;

/**
 * 创建日期：2021/6/11 on 14:39
 * 描述:
 * 作者:wangweicheng
 */
public class EmvOnlineResp {
    private EOnlineResult eOnlineResult = EOnlineResult.ONLINE_ABORT; //online Result
    private boolean existAuthCode;
    byte[] authCode; //89 Authorisation Code
    private boolean existAuthRespCode;
    byte[] authRespCode; //8A Authorisation Response Code
    private boolean existIssAuthData;
    byte[] issueAuthData; //91 Issuer Authentication Data
    private boolean existIssScr71;
    byte[] issueScript71; //71 Issuer Script
    private boolean existIssScr72;
    byte[] issueScript72; //72 Issuer Script

    public EOnlineResult geteOnlineResult() {
        return eOnlineResult;
    }

    public void seteOnlineResult(EOnlineResult eOnlineResult) {
        this.eOnlineResult = eOnlineResult;
    }

    public boolean isExistAuthCode() {
        return existAuthCode;
    }

    public void setExistAuthCode(boolean existAuthCode) {
        this.existAuthCode = existAuthCode;
    }

    public boolean isExistAuthRespCode() {
        return existAuthRespCode;
    }

    public void setExistAuthRespCode(boolean existAuthRespCode) {
        this.existAuthRespCode = existAuthRespCode;
    }

    public boolean isExistIssAuthData() {
        return existIssAuthData;
    }

    public void setExistIssAuthData(boolean existIssAuthData) {
        this.existIssAuthData = existIssAuthData;
    }

    public boolean isExistIssScr71() {
        return existIssScr71;
    }

    public void setExistIssScr71(boolean existIssScr71) {
        this.existIssScr71 = existIssScr71;
    }

    public boolean isExistIssScr72() {
        return existIssScr72;
    }

    public void setExistIssScr72(boolean existIssScr72) {
        this.existIssScr72 = existIssScr72;
    }

    public byte[] getAuthCode() {
        return authCode;
    }

    public void setAuthCode(byte[] authCode) {
        this.authCode = authCode;
    }

    public byte[] getAuthRespCode() {
        return authRespCode;
    }

    public void setAuthRespCode(byte[] authRespCode) {
        this.authRespCode = authRespCode;
    }

    public byte[] getIssueAuthData() {
        return issueAuthData;
    }

    public void setIssueAuthData(byte[] issueAuthData) {
        this.issueAuthData = issueAuthData;
    }

    public byte[] getIssueScript71() {
        return issueScript71;
    }

    public void setIssueScript71(byte[] issueScript71) {
        this.issueScript71 = issueScript71;
    }

    public byte[] getIssueScript72() {
        return issueScript72;
    }

    public void setIssueScript72(byte[] issueScript72) {
        this.issueScript72 = issueScript72;
    }

    private String toSrc(byte [] bcd) {
        if (bcd == null ){
            return null;
        }
        return TopTool.getInstance().getConvert().bcdToStr(bcd);
    }

    @Override
    public String toString() {
        return "EmvOnlineResp{" +
                "eOnlineResult=" + eOnlineResult +
                ", existAuthCode=" + existAuthCode +
                ", authCode=" + toSrc(authCode) +
                ", existAuthRespCode=" + existAuthRespCode +
                ", authRespCode=" + toSrc(authRespCode) +
                ", existIssAuthData=" + existIssAuthData +
                ", issueAuthData=" + toSrc(issueAuthData) +
                ", existIssScr71=" + existIssScr71 +
                ", issueScript71=" + toSrc(issueScript71) +
                ", existIssScr72=" + existIssScr72 +
                ", issueScript72=" + toSrc(issueScript72) +
                '}';
    }
}
