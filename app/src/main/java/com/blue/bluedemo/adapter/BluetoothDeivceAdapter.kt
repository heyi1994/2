package com.blue.bluedemo.adapter

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blue.bluedemo.R

/**
 * Author: Heyi.
 * Date: 2017/5/26.
 * Package:com.blue.bluedemo.adapter.
 * Desc:Adapter
 */
class BluetoothDeivceAdapter constructor(var bluetoothDevices:ArrayList<BluetoothDevice>,var context:Context):RecyclerView.Adapter<BluetoothDeivceAdapter.BluetoothDeviceViewHolder>(){

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder?, position: Int) {
        holder?.m_device_name?.text=bluetoothDevices.get(position).name
        holder?.m_device_addr?.text=bluetoothDevices.get(position).address
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

         init {
          m_device_name=view.findViewById(R.id.m_device_name) as TextView
          m_device_addr=view.findViewById(R.id.m_device_addr) as TextView
         }

    }

    fun addDevice(device:BluetoothDevice,position: Int){
        bluetoothDevices.add(device)
        notifyItemChanged(position)
    }
}