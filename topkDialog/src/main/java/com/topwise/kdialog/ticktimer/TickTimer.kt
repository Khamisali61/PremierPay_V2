package com.topwise.kdialog.ticktimer

import android.os.CountDownTimer

/**
 * 作者：wangwc on 2021/5/4 20:42
 */
class TickTimer : CountDownTimer {
    constructor(millisInFuture: Long, countDownInterval: Long) : super(millisInFuture* 1000, countDownInterval* 1000)

    var listener: TickTimerListener?=null
    override fun onTick(millisUntilFinished: Long) {
        if (listener != null)
            listener!!.onTick(millisUntilFinished/ 1000)
    }

    override fun onFinish() {
        if (listener != null)
            listener!!.onFinish()
    }
    fun setTickTimerListener(ilistener: TickTimerListener){
        listener = ilistener;
    }
}