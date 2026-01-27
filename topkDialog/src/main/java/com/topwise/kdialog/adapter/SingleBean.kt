package com.topwise.kdialog.adapter

/**
 * 作者：wangwc on 2021/5/5 16:41
 */
class SingleBean {
    var isSelect :Boolean?= false
    var content :String?=null

    constructor(isSelect: Boolean?, content: String?) {
        this.isSelect = isSelect
        this.content = content
    }
    constructor(content: String?) {
        this.isSelect = false
        this.content = content
    }
}