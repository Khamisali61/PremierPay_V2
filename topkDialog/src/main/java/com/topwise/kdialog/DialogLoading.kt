package com.topwise.kdialog

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.topwise.kdialog.ticktimer.TickTimer
import com.topwise.kdialog.ticktimer.TickTimerListener



/**
 * 作者：wangwc on 2021/5/4 18:18
 */
class DialogLoading :BaseDialog {
    lateinit var loadingView: ImageView
        private set
    lateinit var loadingText: TextView
        private set
    lateinit var titleView: TextView
        private set
    lateinit var contentView: TextView
        private set

    private var title: String = ""



    constructor(context: Context) : super(context, R.style.loading_dialog){
        initView()
    }

    private fun initView(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_loading ,null)
        titleView = dialogView.findViewById(R.id.tv_title) as TextView
        loadingText = dialogView.findViewById(R.id.tv_loading) as TextView

        titleView.setTextIsSelectable(true)
        contentView = dialogView.findViewById(R.id.tv_content) as TextView
        contentView.movementMethod = ScrollingMovementMethod.getInstance()
        contentView.setTextIsSelectable(true)

        loadingView = dialogView.findViewById(R.id.iv_loading) as ImageView
        if (DataTool.isNullString(title)) {
            titleView.visibility = View.GONE
        }
        setContentView(dialogView)

        // 加载动画
        val hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation
        )
        // 使用ImageView显示动画
        loadingView.startAnimation(hyperspaceJumpAnimation)
    }

    fun setImageContent(str: String?) {
        loadingText.text= str
    }
    fun setContent(str: String?) {
        contentView.text = str
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
    fun tickTimerStart(time :Int){
        if (tickTimer != null)
            tickTimer!!.cancel();

        tickTimer = TickTimer(time.toLong(),1)
        tickTimer!!.setTickTimerListener(object : TickTimerListener {
            override fun onFinish() {
                if (myListener != null)
                    myListener!!.onConfirm("")

                dismiss()
            }

            override fun onTick(time: Long) {
                Log.e("DialogLoading","onTick== {$}" +time)
                loadingText.text= time.toString()
            }
        })
        tickTimer!!.start()
    }
}