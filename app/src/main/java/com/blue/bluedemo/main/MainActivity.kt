package com.blue.bluedemo.main

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.OvershootInterpolator
import com.blue.bluedemo.R
import com.blue.bluedemo.adapter.BluetoothDeivceAdapter
import com.blue.bluedemo.base.BaseActivity
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.main.
 * Desc:
 */
class MainActivity :BaseActivity(),MainActivityUiInterface{
    val TAG:String=this.javaClass.simpleName
    private var m_bluetooth_devices:ArrayList<BluetoothDevice>?=null
    private var m_adapter:BluetoothDeivceAdapter?=null

    override fun addDevices(device: BluetoothDevice) {
        m_bluetooth_devices?.add(device)
        m_adapter?.addDevice(device, m_bluetooth_devices?.size!! -1)
    }

    override fun longToast(msg: String) {
        showShortToast(msg)
    }

    override fun ShortToast(msg: String) {
        showShortToast(msg)
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        m_bluetooth_devices=arrayListOf<BluetoothDevice>()
        recycler_view.layoutManager=LinearLayoutManager(this)
        recycler_view.itemAnimator= FadeInRightAnimator()
        recycler_view.itemAnimator.addDuration=1000
        m_adapter = BluetoothDeivceAdapter(m_bluetooth_devices as ArrayList<BluetoothDevice>, this)
        recycler_view.adapter=m_adapter
        MainPresenter(this,this)

    }




}