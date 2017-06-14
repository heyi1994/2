package com.blue.bluedemo.main

import android.bluetooth.BluetoothDevice

/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.main.
 * Desc:View Interface
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
     * 开始扫描
     */
    fun startScan()


    /**
     * 扫描完成
     */
    fun  scanFinish()


}