package com.topwise.kdialog

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.topwise.kdialog.DataTool.Companion.isNullString
import com.topwise.kdialog.R.*


/**
 * 作者：wangwc on 2021/5/3 21:26
 */
class DialogEditSureCancel :BaseDialog {
    lateinit var logoView: ImageView
        private set
    lateinit var sureView: TextView
        private set
    lateinit var cancelView: TextView
        private set
    lateinit var editText: EditText
        private set
    lateinit var titleView: TextView
        private set
    private var title: String = ""
    private var logoIcon = -1




    constructor(context: Context?, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context!!, cancelable, cancelListener) {
        initView()
    }

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    constructor(context: Activity?) : super(context!!) {
        initView()
    }

    constructor(context: Context?, alpha: Float, gravity: Int) : super(context, alpha, gravity) {
        initView()
    }

    fun setSure(strSure: String?) {
        sureView.text = strSure
    }

    fun setCancel(strCancel: String?) {
        cancelView.text = strCancel
    }

    fun setInputType(inutType :Int){
        editText!!.inputType = inutType
    }
    fun setMaxlenth(maxLenth :Int){
        editText!!.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLenth))
    }

    private fun initView() {
        val dialogView = LayoutInflater.from(context).inflate(layout.dialog_edittext_sure_false, null)
        logoView = dialogView.findViewById(id.iv_logo) as ImageView
        titleView = dialogView.findViewById(id.tv_title) as TextView
        sureView = dialogView.findViewById(id.tv_sure) as TextView
        cancelView = dialogView.findViewById(id.tv_cancle) as TextView
        editText = dialogView.findViewById(id.editText) as EditText
        if (isNullString(title)) {
            titleView.visibility = View.GONE
        }
        if (logoIcon == -1) {
            logoView.visibility = View.GONE
        }
        setContentView(dialogView)

        sureView.setOnClickListener(View.OnClickListener {
            var toString = editText.text.toString()
            if (myListener != null && !isNullString(toString)){
                myListener!!.onConfirm(toString)
                dismiss()
            }

        })
        cancelView.setOnClickListener(View.OnClickListener {
            if (myListener != null)
                myListener!!.onCancel(-1)

            dismiss()
        })

        showSoftInput(editText);
    }

    fun setLogo(LOGOIcon: Int) {
        logoIcon = LOGOIcon
        if (logoIcon == -1) {
            logoView.visibility = View.GONE
            return
        }
        logoView.visibility = View.VISIBLE
        logoView.setImageResource(logoIcon)
    }

    fun setTitle(titleStr: String) {
        title = titleStr
        if (isNullString(title)) {
            titleView.visibility = View.GONE
            return
        }
        titleView.visibility = View.VISIBLE
        titleView.text = title

    }
    fun setHint(hintStr: String){
        editText.hint = hintStr
    }
    fun setConnent(textStr: String){
        editText.text = Editable.Factory.getInstance().newEditable(textStr)
        editText.setSelection(textStr.length)
    }
}