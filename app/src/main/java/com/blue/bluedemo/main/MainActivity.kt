package com.blue.bluedemo.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.blue.bluedemo.R
import com.blue.bluedemo.adapter.BluetoothDeivceAdapter
import com.blue.bluedemo.base.BaseActivity
import com.blue.bluedemo.detail.DetailFragment
import com.blue.bluedemo.socket.ConnectionStatListener
import com.blue.bluedemo.socket.OnObtainMsgListener
import com.blue.bluedemo.socket.OnSendMsgListener
import com.orhanobut.logger.Logger
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Author: Heyi.
 * Date: 2017/5/24.
 * Package:com.blue.bluedemo.main.
 * Desc:
 */
class MainActivity :BaseActivity(),MainActivityUiInterface,ConnectionStatListener{

    val TAG:String=this.javaClass.simpleName
    private var m_bluetooth_devices:ArrayList<BluetoothDevice>?=null
    private var m_adapter:BluetoothDeivceAdapter?=null
    private var m_presenter:MainPresenter?=null
    private var tag="con"
    private var sendMsgListener : OnSendMsgListener?=null
    private var obtainMsgListener:OnObtainMsgListener?=null

    override fun addDevices(device: BluetoothDevice) {
        m_bluetooth_devices?.add(device)
        m_adapter?.notifyItemChanged(m_bluetooth_devices?.size!! -1)
    }

    override fun scanFinish() {
        showShortToast(getString(R.string.scan_end))
        m_progress_bar.visibility= View.INVISIBLE
    }

    override fun startScan() {
        m_progress_bar.visibility= View.VISIBLE
        showShortToast(getString(R.string.scan_start))
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }
    override fun ShortToast(msg: String) {
        showShortToast(msg)
    }

    override fun init() {
        m_presenter=MainPresenter(this,this)
        m_bluetooth_devices=ArrayList<BluetoothDevice>()
        recycler_view.layoutManager=LinearLayoutManager(this)
        recycler_view.itemAnimator= FadeInRightAnimator()
        recycler_view.itemAnimator.addDuration=1000
        m_adapter = BluetoothDeivceAdapter(m_bluetooth_devices as ArrayList<BluetoothDevice>, this, m_presenter as MainPresenter)
        recycler_view.adapter=m_adapter


    }


    /**
     * 请求位置权限的回调
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==1){
            if(permissions.get(0)==Manifest.permission.ACCESS_COARSE_LOCATION){
                if(grantResults.get(0)==PackageManager.PERMISSION_GRANTED){
                    m_presenter?.getLocationPermission()
                    Log.i(TAG,"已获取位置地址权限")
                }else{
                    showShortToast(getString(R.string.no_addr_permission))
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * 申请打开蓝牙的回调
     * 1、打开蓝牙
     *    用户拒绝返回码是0
     *    用户确定返回码是-1
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG,"$requestCode")
        if (requestCode==1){
          if (resultCode==-1){
              Log.i(TAG,"用户同意打开蓝牙")
              m_presenter?.openBleAndInit()
          }else{
              showShortToast(getString(R.string.no_open_bluetooth))
          }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun connectionSuccess(name:String) {
        showShortToast(getString(R.string.connect_success))
        recycler_view.visibility=View.GONE
        val detailFragment = DetailFragment()
        val bundle = Bundle()
        bundle.putString("name",name)
        detailFragment.arguments=bundle
        startFrag(R.id.m_frame,detailFragment,tag)
    }

    override fun connectionFail() {
        showShortToast(getString(R.string.connect_failed))
    }

    override fun obtainMsg(msg: String) {
       obtainMsgListener?.obtainMsg(msg)
    }

    fun setOnSendMsgListener(listener:OnSendMsgListener){
        sendMsgListener=listener
    }

   fun sendMsg(msg:String){
       if(sendMsgListener!=null){
           Logger.i("可以发送的啊")
           sendMsgListener?.sendMsgs(msg)
       }

       Logger.i(msg)
   }

    fun setOnObtainMsgListener(listener: OnObtainMsgListener){
        obtainMsgListener=listener
    }

    override fun disconnection() {
        showShortToast(getString(R.string.disconnected))
        finish()
    }

    override fun onDestroy() {
        m_presenter?.destoryData()
        m_adapter?.destoryClientService()
        super.onDestroy()

    }
}