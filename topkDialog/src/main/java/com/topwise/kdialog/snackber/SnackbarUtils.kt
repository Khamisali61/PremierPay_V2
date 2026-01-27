package com.topwise.kdialog.snackber

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.topwise.kdialog.R


/**
 * 作者：wangwc on 2021/5/4 11:36
 */
open class SnackbarUtils {


    companion object{
        //设置Snackbar背景颜色
        private var sColorInfo: Long = 0xFF299EE3
        private var sColorConfirm: Long = 0xFF4CB04E
        private var sColorWarning: Long = 0xFFFEC005
        private var sColorDanger: Long = 0xFFF44336


        fun  showShort(view: View, message: String): Snackbar {
            return Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        }
        fun showLong(view: View, message: String):Snackbar{

            return Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        }
        @SuppressLint("ResourceAsColor")
        fun showShortTop(view: View, message: String):Snackbar{
            return Snackbar.make(view, message, Snackbar.LENGTH_LONG).setActionTextColor(R.color.green_xiaomi)
        }


    }
}