package com.blue.bluedemo.socket.client

import android.app.Activity
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.blue.bluedemo.main.MainActivity
import com.blue.bluedemo.socket.ConnectionStatListener
import com.blue.bluedemo.socket.OnSendMsgListener
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: Heyi.
 * Date: 2017/5/28.
 * Package:com.blue.bluedemo.socket.client.
 * Desc:Client
 */
class ClientSocketService:Service(),OnSendMsgListener {


    val TAG:String=this.javaClass.simpleName
    private var socket:BluetoothSocket?=null
    private var device:BluetoothDevice?=null
    private var m_connStat_listener:ConnectionStatListener?=null
    private var subs:List<Disposable> = ArrayList<Disposable>()
    private var m_out:OutputStream?=null

    override fun onBind(intent: Intent?): IBinder {
        return ClientBinder()
    }

    inner class ClientBinder:Binder(),IClientBinder{

        override fun setConnectionStatListener(listener: ConnectionStatListener) {
            m_connStat_listener=listener
        }

        override fun init(device: BluetoothDevice?, adapter: BluetoothAdapter,activity: MainActivity) {
            activity.setOnSendMsgListener(this@ClientSocketService)
            val uuid=packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA).metaData.getString("uuid")
            socket = device?.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
            val subscribe= Observable.create<BluetoothSocket> { e ->
                adapter.cancelDiscovery()
                try {
                    socket?.connect()
                    e.onNext(socket)
                } catch (ex: Exception) {
                    socket?.close()
                    e.onError(ex)
                }

            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer { t: BluetoothSocket ->
                        managerSocket(t)
                    }, Consumer { t: Throwable ->
                        if(m_connStat_listener!=null) {
                            m_connStat_listener?.connectionFail()
                        }
                    })
            subs.plus(subscribe)

        }

    }

    private fun managerSocket(socket:BluetoothSocket){
         Logger.i("连接成功"+socket.remoteDevice.name)
        m_connStat_listener?.connectionSuccess(socket.remoteDevice.name)
        var m_in:InputStream?=null
        try {
            m_out=socket.outputStream
            m_in=socket.inputStream
        }catch (ex:IOException){
            Logger.i(ex.message)
        }

        val subscribe = Observable.create<String> { e ->
            var buffer = ByteArray(1024)
            while (true) {
                try {
                    val len: Int = m_in?.read(buffer) as Int

                    val come = String(buffer, 0, len)
                    e.onNext(come)
                } catch (ex: IOException) {
                    e.onError(ex)
                    break
                }

            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { t: String ->
                    Logger.i("$t")
                    m_connStat_listener?.obtainMsg(t)
                }
                        , Consumer { t: Throwable -> m_connStat_listener?.disconnection()})
        subs.plus(subscribe)

    }


    override fun onDestroy() {
        for(p in subs){
            p.dispose()
        }
        super.onDestroy()
    }
    override fun sendMsgs(msg:String) {
        Logger.i(msg)
        if (m_out!=null){
            m_out?.write(msg.toByteArray())
        }
    }
}