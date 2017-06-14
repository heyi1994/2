package com.blue.bluedemo.wiget

import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.blue.bluedemo.R

/**
 * Author: Heyi.
 * Date: 2017/5/28.
 * Package:com.blue.bluedemo.wiget.
 * Desc:自定义Toast
 */
class CustomToast constructor(var activity:AppCompatActivity,var time:Int,var text:String){
    private var m_toast:Toast?=null
    init {
        m_toast=Toast(activity)
        val view = activity.layoutInflater.inflate(R.layout.custom_toast,
                null)
        val textView = view.findViewById(R.id.tv_toast) as TextView
        textView.text=text
        m_toast?.setGravity(Gravity.BOTTOM,0,80)
        m_toast?.duration=time
        m_toast?.view=view
    }

    fun show(){
        m_toast?.show()
    }

}