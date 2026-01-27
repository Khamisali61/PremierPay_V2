package com.topwise.premierpay.setting.activity;

/**
 * 创建日期：2021/4/2 on 16:46
 * 描述:
 * 作者:  wangweicheng
 */
public class selectBean {
    private int index;
    private String title;
    private boolean select;

    public selectBean() {

    }

    public selectBean(String title, boolean select) {
        this.title = title;
        this.select = select;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "selectBean{" +
                "index=" + index +
                ", title='" + title + '\'' +
                ", select=" + select +
                '}';
    }
}
