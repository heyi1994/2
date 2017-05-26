package com.blue.bluedemo.main

import android.bluetooth.BluetoothDevice

/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.main.
 * Desc:View
 */
interface MainActivityUiInterface {
    /**
     * 添加搜索到的设备
     */
    fun addDevices(device:BluetoothDevice)


    /**
     * short Toast
     */
    fun ShortToast(msg:String)


    /**
     * long Toast
     */
    fun longToast(msg: String)


}