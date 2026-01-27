package com.topwise.kdialog

/**
 * 作者：wangwc on 2021/5/3 16:23
 */
interface IkeyListener {
    fun onConfirm(text:String)
    fun onCancel(res:Int)
}