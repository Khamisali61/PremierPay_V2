package com.topwise.premierpay.daoutils.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 创建日期：2021/3/30 on 15:52
 * 描述: Operator entity
 * 作者:  wangweicheng
 */
@Entity
public class Operator {
    @Id
    private Long id;
    private String operId;
    private String pwd;
    private String name;

    @Generated(hash = 1299474481)
    public Operator(Long id, String operId, String pwd, String name) {
        this.id = id;
        this.operId = operId;
        this.pwd = pwd;
        this.name = name;
    }

    @Generated(hash = 1412551650)
    public Operator() {
    }

    public Operator(String operId, String pwd, String name) {
        this.operId = operId;
        this.pwd = pwd;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperId() {
        return this.operId;
    }

    public void setOperId(String operId) {
        this.operId = operId;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
