package com.topwise.kdialog

import android.content.Context
import android.content.DialogInterface
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.topwise.kdialog.DataTool.Companion.isNullString
import com.topwise.kdialog.ticktimer.TickTimer
import com.topwise.kdialog.ticktimer.TickTimerListener


/**
 * 作者：wangwc on 2021/5/3 16:00
 */
class DialogSure : BaseDialog {
    lateinit var logoView: ImageView
        private set
    lateinit var titleView: TextView
        private set
    lateinit var contentView: TextView
        private set
    lateinit var sureView: TextView
        private set
    private var title: String = ""
    private var logoIcon = -1

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

    fun setSureListener(listener: View.OnClickListener?) {
        sureView.setOnClickListener(listener)
    }

    fun setSure(content: String?) {
        sureView.text = content
    }

    fun setContent(str: String?) {
        contentView.text = str
    }

    private fun initView() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sure, null)
        sureView = dialogView.findViewById(R.id.tv_sure) as TextView
        titleView = dialogView.findViewById(R.id.tv_title) as TextView
        titleView.setTextIsSelectable(true)
        contentView = dialogView.findViewById(R.id.tv_content) as TextView
        contentView.movementMethod = ScrollingMovementMethod.getInstance()
        contentView.setTextIsSelectable(true)
        logoView = dialogView.findViewById(R.id.iv_logo) as ImageView
        if (isNullString(title)) {
            titleView.visibility = View.GONE
        }
        if (logoIcon == -1) {
            logoView.visibility = View.GONE
        }
        setContentView(dialogView)

        sureView.setOnClickListener(View.OnClickListener {
            if (myListener != null)
                myListener!!.onConfirm("")


            tickTimerStop()
            dismiss()
        })
    }
    fun setSucessLogo(){
        logoView.visibility = View.VISIBLE
        logoView.setImageResource(R.drawable.scuess)
    }
    fun setFailLogo(){
        logoView.visibility = View.VISIBLE
        logoView.setImageResource(R.drawable.fail)
    }
    fun setLogo(resId: Int) {
        logoIcon = resId
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

    fun tickTimerStart(time :Int){
        if (time <= 0) return

        if (tickTimer != null)
            tickTimer!!.cancel();

        tickTimer = TickTimer(time.toLong(),1)
        tickTimer!!.setTickTimerListener(object : TickTimerListener {
            override fun onFinish() {
                if (myListener != null)
                    myListener!!.onCancel(-1)

                dismiss()
            }

            override fun onTick(time: Long) {
                Log.e("DialogSure","onTick== {$}" +time)
            }
        })
        tickTimer!!.start()
    }
}