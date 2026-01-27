package com.topwise.kdialog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.topwise.kdialog.R
import java.util.*


/**
 * 作者：wangwc on 2021/5/4 22:17
 */
class SingleChoiceAdapter :BaseAdapter {
    var listData:List<SingleBean> ?=null
    var mContext:Context?=null

    constructor(listData: List<SingleBean>?, context: Context?) : super() {
        this.listData = listData
        this.mContext = context
    }


    override fun getCount(): Int {
       return listData!!.size
    }

    override fun getItem(position: Int): Any {
        return listData!!.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        var tv_content: TextView? = null
        var cb_check: CheckBox? = null
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview, null)
        }
        cb_check = convertView!!.findViewById(R.id.cb_check) as CheckBox?
        tv_content = convertView!!.findViewById(R.id.tv_content) as TextView?


        var get = listData!!.get(position)
        tv_content!!.text = get.content
//
        if (  get.isSelect == true ){
            cb_check!!.isChecked =true
        }else{
            cb_check!!.isChecked =false
        }
        return convertView!!;
    }


    fun setOtherFalse(position: Int){
        for (i in 0..listData!!.size -1){
            if (position == i){
                listData!!.get(i).isSelect = true
            }else{
                listData!!.get(i).isSelect = false
            }
        }
        notifyDataSetChanged()
    }
    fun getPosiTion():String{
        for (i in 0..listData!!.size -1){
            if (listData!!.get(i).isSelect == true){
                 return i.toString();
            }
        }
        return ""
    }
    fun getPosiTions(): Array<Boolean> {
        var arrays = BooleanArray(listData!!.size)
        for (i in 0..listData!!.size -1){
            if (listData!!.get(i).isSelect == true){
                arrays[i] = true
            }else{
                arrays[i] = false
            }
        }
        return arrays.toTypedArray()
    }
    fun setSelect(position: Int){
        for (i in 0..listData!!.size -1){
            if (position == i){
                listData!!.get(i).isSelect = true
            }
        }
        notifyDataSetChanged()
    }
}


