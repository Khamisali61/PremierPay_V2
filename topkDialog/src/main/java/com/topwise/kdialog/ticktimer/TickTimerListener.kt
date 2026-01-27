package com.topwise.kdialog.ticktimer

/**
 * 作者：wangwc on 2021/5/4 20:42
 */
interface TickTimerListener {
    fun onFinish();
    fun onTick(time:Long);
}