package com.topwise.kdialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import com.topwise.kdialog.ticktimer.TickTimer
import com.topwise.kdialog.ticktimer.TickTimerListener
import java.lang.Exception


/**
 * 作者：wangwc on 2021/5/3 15:40
 */
open class BaseDialog : Dialog{

    protected lateinit var mContext: Context
    var layoutParams: WindowManager.LayoutParams? = null

    var myListener: IkeyListener?= null
    var tickTimer: TickTimer?=null

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        initView(context)
    }

    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {
        initView(context)
    }

    constructor(context: Context) : super(context) {
        initView(context)
    }

    private fun initView(context: Context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(R.drawable.transparent_bg)
        mContext = context
        val window = this.window
        layoutParams = window!!.attributes
        layoutParams?.alpha = 1f
        window.attributes = layoutParams
        if (layoutParams != null) {
            layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams?.gravity = Gravity.CENTER
        }

        setCancelable(false)
    }

    /**
     * @param context 实体
     * @param alpha   透明度 0.0f--1f(不透明)
     * @param gravity 方向(Gravity.BOTTOM,Gravity.TOP,Gravity.LEFT,Gravity.RIGHT)
     */
    constructor(context: Context?, alpha: Float, gravity: Int) : super(context!!) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(R.drawable.transparent_bg)
        mContext = context
        val window = this.window
        layoutParams = window!!.attributes
        layoutParams?.alpha = 1f
        window.attributes = layoutParams
        if (layoutParams != null) {
            layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams?.gravity = gravity
        }
    }


    /**
     * 隐藏头部导航栏状态栏
     */
    fun skipTools() {
        window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 设置全屏显示
     */
    fun setFullScreen() {
        val window = window!!
        window.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        window.attributes = lp
    }

    /**
     * 设置宽度match_parent
     */
    fun setFullScreenWidth() {
        val window = window!!
        window.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.FILL_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
    }

    /**
     * 设置高度为match_parent
     */
    fun setFullScreenHeight() {
        val window = window!!
        window.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.FILL_PARENT
        window.attributes = lp
    }

    fun setOnWhole() {
        window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
    }

    fun setMyLietener(listener: IkeyListener){
        myListener = listener;
    }
    fun tickTimerStop(){
        if (tickTimer != null){
            tickTimer!!.cancel()
            tickTimer = null
        }

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun dismiss() {
        tickTimerStop()
        println("BaseDialog dismiss: ")
        if ( this is Dialog) {
            println("BaseDialog : this is Dialog")
            if (this?.window?.decorView?.isAttachedToWindow == false) {
                println("BaseDialog !isAttachedToWindow: ")
                this.window?.decorView?.postDelayed({
                    try {
                        if (this.isShowing) {
                            println("BaseDialog !isAttachedToWindow  isShowing: ")
                            super.dismiss()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 200)
                return
            }
        }
        super.dismiss()
    }
    fun showSoftInput(editText: EditText ) {
        editText.requestFocus()
        editText.postDelayed({
            val inputManager =
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.showSoftInput(editText, 0)
        }, 200)
    }
}