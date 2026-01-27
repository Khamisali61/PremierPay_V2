package com.topwise.premierpay.trans.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class TestParam implements Serializable, Cloneable {
    static final long serialVersionUID = 42L;

    @Id
    private Long id;
     // Communication Type   0:wifi , 1：mobile 2：random
    private int commType  ;
    private int totalNum  =1000;
    // unit second
    private int intervalTime =3 ;
    private int delayTime = 0;
    // interval time mode  0:fixed interval time  , 1：random time
    private int intervalMode;

    private int eachTransTime =12;


    @Generated(hash = 1665786676)
    public TestParam(Long id, int commType, int totalNum, int intervalTime,
            int delayTime, int intervalMode, int eachTransTime) {
        this.id = id;
        this.commType = commType;
        this.totalNum = totalNum;
        this.intervalTime = intervalTime;
        this.delayTime = delayTime;
        this.intervalMode = intervalMode;
        this.eachTransTime = eachTransTime;
    }

    @Generated(hash = 1574695816)
    public TestParam() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCommType() {
        return commType;
    }

    public void setCommType(int commType) {
        this.commType = commType;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public int getIntervalMode() {
        return intervalMode;
    }

    public void setIntervalMode(int intervalMode) {
        this.intervalMode = intervalMode;
    }

    public int getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(int delayTime) {
        this.delayTime = delayTime;
    }


    public int getEachTransTime() {
        return eachTransTime;
    }

    public void setEachTransTime(int eachTransTime) {
        this.eachTransTime = eachTransTime;
    }
}
