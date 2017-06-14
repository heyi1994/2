package com.blue.bluedemo.base

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blue.bluedemo.wiget.CustomToast

/**
 * Author: Heyi.
 * Date: 2017/5/29.
 * Package:com.blue.bluedemo.base.
 * Desc:
 */
abstract class BaseFragment :Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = View.inflate(activity,getLayoutId(),null)
        init(view)
        return view
    }

    abstract fun getLayoutId():Int

    /**
     * show short toast
     */
    protected fun showShortToast(msg:String){
        CustomToast(activity as AppCompatActivity, Toast.LENGTH_SHORT,msg).show()
    }

    /**
     * show long toast
     */
    protected fun showLongToast(msg:String){
        CustomToast(activity as AppCompatActivity, Toast.LENGTH_LONG,msg).show()
    }

    abstract fun init(view:View)

}