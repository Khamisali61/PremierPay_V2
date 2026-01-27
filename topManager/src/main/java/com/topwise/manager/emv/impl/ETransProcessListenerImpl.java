package com.topwise.manager.emv.impl;

import com.topwise.cloudpos.aidl.emv.level2.Combination;
import com.topwise.cloudpos.aidl.emv.level2.EmvCandidateItem;
import com.topwise.cloudpos.aidl.emv.level2.EmvCapk;
import com.topwise.manager.emv.api.ITransProcessListener;
import com.topwise.manager.emv.entity.EmvAidParam;
import com.topwise.manager.emv.entity.EmvOnlineResp;
import com.topwise.manager.emv.entity.EmvPinEnter;
import com.topwise.manager.emv.enums.EKernelType;
import com.topwise.manager.emv.enums.EPinType;

import java.util.List;

/**
 * 创建日期：2021/6/10 on 16:42
 * 描述:
 * 作者:wangweicheng
 */
public class ETransProcessListenerImpl implements ITransProcessListener {

    @Override
    public int onReqAppAidSelect(String[] aids) {
        return 0;
    }

    @Override
    public void onUpToAppEmvCandidateItem(EmvCandidateItem emvCandidateItem) {

    }

    @Override
    public void onUpToAppKernelType(EKernelType eKernelType) {

    }

    @Override
    public boolean onReqFinalAidSelect() {
        return true;
    }

    @Override
    public boolean onConfirmCardInfo(String cardNo) {
        return false;
    }

    @Override
    public EmvPinEnter onReqGetPinProc(EPinType pinType, int leftTimes) {
        return new EmvPinEnter();
    }

    @Override
    public boolean onDisplayPinVerifyStatus(final int PinTryCounter) {
        return false;
    }


    @Override
    public boolean onReqUserAuthProc(int certype, String certnumber) {
        return false;
    }

    @Override
    public EmvOnlineResp onReqOnlineProc()  {
        return new EmvOnlineResp();
    }


    @Override
    public boolean onSecCheckCardProc() {
        return false;
    }

    @Override
    public List<Combination> onLoadCombinationParam() {
        return null;
    }

    @Override
    public EmvAidParam onFindCurAidParamProc(String sAid) {
        return null;
    }

    @Override
    public void onRemoveCardProc() {

    }

    @Override
    public EmvCapk onFindIssCapkParamProc(String sAid, byte bCapkIndex) {
        return null;
    }
}
