package com.blue.bluedemo.adapter

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blue.bluedemo.R
import com.blue.bluedemo.main.MainActivity
import com.blue.bluedemo.main.MainPresenter
import com.blue.bluedemo.socket.ConnectionStatListener
import com.blue.bluedemo.socket.client.ClientSocketService
import com.blue.bluedemo.socket.client.IClientBinder

/**
 * Author: Heyi.
 * Date: 2017/5/26.
 * Package:com.blue.bluedemo.adapter.
 * Desc:Adapter
 */
class BluetoothDeivceAdapter constructor(var bluetoothDevices:ArrayList<BluetoothDevice>,var context:MainActivity,var presenter:MainPresenter):RecyclerView.Adapter<BluetoothDeivceAdapter.BluetoothDeviceViewHolder>(){
     val TAG:String=this.javaClass.simpleName
    private var conn:ServiceConnection?=null
    private var binder:IClientBinder?=null

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder?, position: Int) {
        if(bluetoothDevices.get(position).name==null){
            holder?.m_device_name?.text="该设备无名字$position"
        }else {
            holder?.m_device_name?.text = bluetoothDevices.get(position).name
        }
        holder?.m_device_addr?.text=bluetoothDevices.get(position).address

        holder?.m_item?.setOnClickListener {
            if(bluetoothDevices[position].bondState==BluetoothDevice.BOND_BONDED){
                //绑定状态就直接连接
                Log.i(TAG,"连接服务端")
               conn= object :ServiceConnection{
                    override fun onServiceDisconnected(name: ComponentName?) {
                    }

                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        binder=service as IClientBinder
                        binder?.init(bluetoothDevices[position],presenter.getAdapter(),context)
                        binder?.setConnectionStatListener(context)
                    }

                }
                context.bindService(Intent(context,ClientSocketService::class.java),conn,Context.BIND_AUTO_CREATE)
            }else{
                bluetoothDevices[position].createBond()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BluetoothDeviceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_bluetooth_device,parent,false)
        return BluetoothDeviceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bluetoothDevices.size
    }


     inner class BluetoothDeviceViewHolder constructor(var view:View) :RecyclerView.ViewHolder(view){
         var m_device_name:TextView?=null
         var m_device_addr:TextView?=null
         var m_item:LinearLayout?=null

         init {
          m_device_name=view.findViewById(R.id.m_device_name) as TextView
          m_device_addr=view.findViewById(R.id.m_device_addr) as TextView
          m_item = view.findViewById(R.id.m_item) as LinearLayout
         }

    }

    /**
     * 如果客户端存在的话就停止该服务
     */
    fun destoryClientService(){
        if(conn!=null&&binder!=null){
            context.unbindService(conn)
            context.stopService(Intent(context,ClientSocketService::class.java))
        }
    }
}