package com.blue.bluedemo.base

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.blue.bluedemo.R

/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.
 * Desc:The base of all activity
 */
 abstract class BaseActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersiveStatus()
        setContentView(getLayoutId())
        init()
    }

    /**
     * 初始化数据
     */
    protected abstract fun init()

    /**
     * 沉浸式状态栏
     */
    protected fun immersiveStatus() {
        if (Build.VERSION.SDK_INT>=21){
            /*window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)*/
            window.statusBarColor=resources.getColor(R.color.blue)
        }
    }

    /**
     * 初始化子类的布局文件
     */
    protected abstract fun getLayoutId():Int


    /**
     * show short toast
     */
    protected fun showShortToast(msg:String){
        Toast.makeText(this,msg,
                Toast.LENGTH_SHORT).show()
    }

    /**
     * show long toast
     */
    protected fun showLongToast(msg:String){
        Toast.makeText(this,msg,
                Toast.LENGTH_LONG).show()
    }

}