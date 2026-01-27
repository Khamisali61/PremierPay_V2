package com.topwise.kdialog

import android.animation.Animator
import android.animation.ValueAnimator

/**
 * 作者：wangwc on 2021/5/3 16:14
 */
class DataTool {
    companion object{
        /**
         * 判断字符串是否为空 为空即true
         *
         * @param str 字符串
         * @return
         */
        fun isNullString(str: String?): Boolean {
            return str == null || str.length == 0 || "null" == str
        }
        fun isStarted(animator: ValueAnimator?): Boolean {
            return animator != null && animator.isStarted
        }
        fun start(animator: Animator?) {
            if (animator != null && !animator.isStarted) {
                animator.start()
            }
        }
        fun stop(animator: Animator?) {
            if (animator != null && !animator.isRunning) {
                animator.end()
            }
        }

        fun isRunning(animator: ValueAnimator?): Boolean {
            return animator != null && animator.isRunning
        }

    }
}