package com.blue.bluedemo.socket

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.blue.bluedemo.main.MainActivity

/**
 * Author: Heyi.
 * Date: 2017/5/28.
 * Package:com.blue.bluedemo.socket.
 * Desc:
 */
interface IBinder {
    /**
     * 初始化数据
     * 服务端不需要参数device
     */
    fun init(device: BluetoothDevice?,adapter: BluetoothAdapter,activity:MainActivity)


    /**
     * 设置监听器
     */
    fun setConnectionStatListener(listener: ConnectionStatListener)


}