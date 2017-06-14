package com.blue.bluedemo.main

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.blue.bluedemo.R
import com.blue.bluedemo.socket.server.IServerBinder
import com.blue.bluedemo.socket.server.ServerSocketService
import java.io.Serializable
import kotlin.jvm.javaClass

/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.main.
 * Desc:Presenter of MainActivity
 */
class MainPresenter constructor(val view:MainActivityUiInterface,val context: MainActivity):Serializable {


    val TAG:String=this.javaClass.simpleName
    //必须大于0
    private val REQUEST_ENABLE_BT:Int=1
    private val REQUEST_DISCOVERABLE:Int=2
    private var m_bluetooth_adapter:BluetoothAdapter?=null
    private var m_service_connection:ServiceConnection?=null
    private var m_server_binder:IServerBinder?=null
    private var receviers:ArrayList<BlueToothRecever> = ArrayList()
    

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
         checkPermissions()
     }
    }

    /**
     * 动态申请位置信息权限
     */
    private fun checkPermissions(){
              if(ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                  ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)
              }else{
                  checkBluetoothStat()
              }
      }

    /**
     * 得到了位置信息权限
     */
    fun getLocationPermission(){
        checkBluetoothStat()
     }


    /**
     * 检测蓝牙是否打开
     */
    private fun checkBluetoothStat(){
        m_bluetooth_adapter= BluetoothAdapter.getDefaultAdapter()
        if(!m_bluetooth_adapter?.isEnabled!!){
            val intent= Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            context.startActivityForResult(intent,REQUEST_ENABLE_BT)
        }else{
            initBlueTooth()
        }
    }

    /**
     * 用户同意打开蓝牙,初始化数据
     */
    fun openBleAndInit(){
        initBlueTooth()
    }


    /**
     * 初始化蓝牙  开启服务端
     */
    private fun initBlueTooth() {
        m_service_connection=object :ServiceConnection{
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.i(TAG,"服务已连接")
                m_server_binder= service as IServerBinder
                m_server_binder?.init(null, m_bluetooth_adapter as BluetoothAdapter,context)
                m_server_binder?.setConnectionStatListener(context)
            }
        }

        context.bindService(Intent(context,ServerSocketService::class.java),m_service_connection,Context.BIND_AUTO_CREATE)
        val enabler = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        enabler.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600) //3600为蓝牙设备可见时间
        context.startActivityForResult(enabler, REQUEST_DISCOVERABLE)

        val blueToothRecever3 = BlueToothRecever()
        val m_intentFilter= IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(blueToothRecever3,m_intentFilter)
         receviers.plus(blueToothRecever3)
        val blueToothRecever2= BlueToothRecever()
        val m_intentFilter2= IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(blueToothRecever2,m_intentFilter2)
        receviers.plus(blueToothRecever2)

        val blueToothRecever1 = BlueToothRecever()
        val m_intentFilter3= IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        context.registerReceiver(blueToothRecever1,m_intentFilter3)
          receviers.plus(blueToothRecever1)
        val  blueToothRecever = BlueToothRecever()
        val m_intentFilter4= IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        receviers.plus(blueToothRecever)
        context.registerReceiver(blueToothRecever,m_intentFilter4)

        m_bluetooth_adapter?.startDiscovery()
    }


    /**
     * 扫描LE设备
     */
    inner class BlueToothRecever : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val m_action= intent?.action
            val state= intent?.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)

            val m_device = intent?.
                    getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            if(m_action.equals(BluetoothDevice.ACTION_FOUND)){
                  Log.i(TAG,"扫描到设备")
                  view.addDevices(m_device as BluetoothDevice)
            }else if(m_action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                if (context != null) {
                    view.scanFinish()
                }
                  Log.i(TAG,"扫描完成")
            }else if(m_action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                if (context != null) {
                    view.startScan()
                }
                Log.i(TAG,"扫描开始")
            }

            //配对的广播
            if(state==BluetoothDevice.BOND_BONDING){
                Log.i(TAG,"配对中")
                view.ShortToast(m_device?.name+":"+context?.getString(R.string.bluetooth_binding) as String)
            }else if (state==BluetoothDevice.BOND_BONDED){
                Log.i(TAG,"配对成功")
                view.ShortToast(m_device?.name+":"+context?.getString(R.string.bluetooth_binded) as String)
            }else if(state==BluetoothDevice.BOND_NONE){
                view.ShortToast(m_device?.name+":"+context?.getString(R.string.bluetooth_none) as String)
                Log.i(TAG,"解除绑定")
            }

        }
    }
    fun getAdapter():BluetoothAdapter{
        return m_bluetooth_adapter as BluetoothAdapter
    }


    /**
     * 销毁数据 关闭服务和广播  防止内存泄露
     */
    fun destoryData(){
        context.unbindService(m_service_connection)
        context.stopService(Intent(context,ServerSocketService::class.java))
        for (p in receviers){
            context.unregisterReceiver(p)
        }

    }


}

