package com.topwise.premierpay.pack;

public interface IPacker<T, O> {
    /**
     * 打包接口
     *
     * @param t
     * @return
     */
    public O pack(T t);

    /**
     * 解包接口
     *
     * @param t
     * @return
     */
    public int unpack(T t, O o);
}
