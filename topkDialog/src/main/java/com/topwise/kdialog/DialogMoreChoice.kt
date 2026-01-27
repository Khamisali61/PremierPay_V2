package com.topwise.kdialog

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.topwise.kdialog.adapter.SingleBean
import com.topwise.kdialog.adapter.SingleChoiceAdapter
import java.util.*

/**
 * 创建日期：2021/5/6 on 14:16
 * 描述:
 * 作者:wangweicheng
 */
class DialogMoreChoice :BaseDialog {
    lateinit var titleView: TextView
        private set
    lateinit var listView: ListView
        private set
    lateinit var sureView: TextView
        private set
    private var title: String = ""

    private var singleChoiceAdapter: SingleChoiceAdapter?=null

    private var listData:List<SingleBean>?=null
    var selectListener: ISelectListener?= null

    constructor(context: Context?, themeResId: Int) : super(context!!, themeResId) {
        initView()
    }

    constructor(context: Context?, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context!!, cancelable, cancelListener) {
        initView()
    }

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, alpha: Float, gravity: Int) : super(context, alpha, gravity) {
        initView()
    }
    private fun initView() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_single_choice, null)
        sureView = dialogView.findViewById(R.id.tv_sure) as TextView
        titleView = dialogView.findViewById(R.id.tv_title) as TextView
        titleView.setTextIsSelectable(true)
        if (DataTool.isNullString(title)) {
            titleView.visibility = View.GONE
        }
        listView = dialogView.findViewById(R.id.lv_listview) as ListView

        setContentView(dialogView)

        sureView.setOnClickListener(View.OnClickListener {
            if (selectListener != null)
                selectListener!!.onConfirm(singleChoiceAdapter!!.getPosiTions())

            dismiss()
        })

        listView.onItemClickListener = (object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (singleChoiceAdapter != null){
                    singleChoiceAdapter!!.setSelect(position)
                }
            }

        })

        setCancelable(true)

    }
    fun setTitle(titleStr: String) {
        title = titleStr
        if (DataTool.isNullString(title)) {
            titleView.visibility = View.GONE
            return
        }
        titleView.visibility = View.VISIBLE
        titleView.text = title
    }
    fun setListdata(listdata :List<SingleBean>){
        listData = listdata;
        if (listView != null){
            singleChoiceAdapter = SingleChoiceAdapter(listData,mContext)
            listView.adapter = singleChoiceAdapter
        }
    }

    fun setSelectLietener(slistener: ISelectListener){
        selectListener = slistener;
    }
}