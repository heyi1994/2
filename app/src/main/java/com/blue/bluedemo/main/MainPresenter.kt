package com.blue.bluedemo.main

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.blue.bluedemo.R
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.main.
 * Desc:Presenter of MainActivity
 */
class MainPresenter constructor(val view:MainActivityUiInterface,val context: Activity) {


    val TAG:String=this.javaClass.simpleName
    //必须大于0
    private val REQUEST_ENABLE_BT:Int=1
    private var m_bluetooth_adapter:BluetoothAdapter?=null


    

    init {
        checkBlueToothLE()
    }

    /**
     * 确定BLE可用性
     */
    private fun checkBlueToothLE() {
     if(!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
         view.ShortToast(context.getString(R.string.not_support_ble))
     }else{
         initBlueTooth()
     }
    }

    /**
     * 初始化蓝牙
     */
    private fun initBlueTooth() {
        val m_bluetooth_manager= context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        m_bluetooth_adapter= m_bluetooth_manager.adapter
        if(!m_bluetooth_adapter?.isEnabled!!){
            val intent= Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivityForResult(intent,REQUEST_ENABLE_BT)
        }

        val enabler = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        enabler.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600) //3600为蓝牙设备可见时间
        context.startActivityForResult(enabler, 1)


        val m_intentFilter= IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(BlueToothRecever(),m_intentFilter)

        val m_intentFilter2= IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(BlueToothRecever(),m_intentFilter2)

        val m_intentFilter3= IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        context.registerReceiver(BlueToothRecever(),m_intentFilter3)

        m_bluetooth_adapter?.startDiscovery()
    }


    /**
     * 扫描LE设备
     */
    inner class BlueToothRecever : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val m_action= intent?.action
            if(m_action.equals(BluetoothDevice.ACTION_FOUND)){

                val m_device = intent?.
                        getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

              if(m_device?.bondState!=BluetoothDevice.BOND_BONDED){
                  Log.i(TAG,"扫描到设备")
                  view.addDevices(m_device as BluetoothDevice)
              }
            }else if(m_action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                if (context != null) {
                    view.ShortToast(context.getString(R.string.scan_end))
                }
                  Log.i(TAG,"扫描完成")
            }else if(m_action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                if (context != null) {
                    view.ShortToast(context.getString(R.string.scan_start))
                }
                Log.i(TAG,"扫描开始")
            }

        }
    }

}

