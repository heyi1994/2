package com.blue.bluedemo.socket.server

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.blue.bluedemo.R
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

/**
 * Author: Heyi.
 * Date: 2017/5/28.
 * Package:com.blue.bluedemo.socket.
 * Desc:
 */
class ServerSocketService : Service(),OnSendMsgListener{

    val TAG:String=this.javaClass.simpleName
    private var m_adapter: BluetoothAdapter?=null
    private var m_server_socket:BluetoothServerSocket?=null
    private var m_connStat_listener:ConnectionStatListener?=null
    private var subs:List<Disposable> = ArrayList<Disposable>()
    private var m_out: OutputStream?=null
    override fun onBind(intent: Intent?): IBinder {
        return ServerBinder()
    }

    inner class ServerBinder: Binder(), IServerBinder {
        override fun setConnectionStatListener(listener: ConnectionStatListener) {
           m_connStat_listener=listener
        }


        override fun init(device: BluetoothDevice?,adapter: BluetoothAdapter,activity:MainActivity) {
            m_adapter=adapter
            initData()
            activity.setOnSendMsgListener(this@ServerSocketService)
        }

     }

    private fun initData(){
        val uuid = packageManager.getApplicationInfo(packageName,
                PackageManager.GET_META_DATA).metaData.getString("uuid")

        m_server_socket= m_adapter?.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), UUID.fromString(uuid))
        Log.i(TAG,"hehhe")
        val subscribe = Observable.create<BluetoothSocket> {
            e ->
            var socket: BluetoothSocket? = null
            while (true) {
                Logger.i(TAG, "尝试连接")
                try {
                    socket = m_server_socket?.accept()
                } catch (ex: Exception) {
                    e.onError(ex)
                }
                if (socket != null) {
                    Logger.i("已有设备连接成功")
                    e.onNext(socket)
                    m_server_socket?.close()
                    break
                }
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { t: BluetoothSocket ->
                    managerSocket(t)
                }, Consumer { t: Throwable ->
                    Log.i(TAG, t.message)
                    m_connStat_listener?.connectionFail()
                })
        subs.plus(subscribe)

    }


    /**
     * 服务端接受到Socket进行管理
     */
    private fun managerSocket(socket:BluetoothSocket){
        m_connStat_listener?.connectionSuccess(socket.remoteDevice.name)
        Logger.i(socket.remoteDevice.name)

        var m_in: InputStream?=null
        try {
            m_out=socket.outputStream
            m_in=socket.inputStream
            Logger.i("输入输出流获取完成了")
        }catch (ex: IOException){
            Logger.i(ex.message)
        }

        val subscribe = Observable.create<String> { e ->
            var buffer = ByteArray(1024)
            while (true) {
                try {
                    val len: Int = m_in?.read(buffer) as Int
                    Log.i(TAG,"$len")
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
                    Logger.i("收到信息"+t)
                    m_connStat_listener?.obtainMsg(t)
                }
                        , Consumer { t: Throwable ->
                    m_connStat_listener?.disconnection()
                })
        subs.plus(subscribe)
    }
    override fun sendMsgs(msg: String) {
            Logger.i("可以发送")

        val subscribe = Observable.create<Unit> {
            e ->
            try {
                Logger.i(msg.toByteArray().toString())
                if (m_out == null) {
                    Logger.i("草，怎么为空")
                }
                m_out?.write(msg.toByteArray())
            } catch (ex: Exception) {
                e.onError(ex)
            }

        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        Consumer { t -> Logger.i("hehe") },
                        Consumer { t: Throwable -> Logger.i(t.message) })
        subs.plus(subscribe)

    }
    override fun onDestroy() {
        for (p in subs){
            p.dispose()
        }
        super.onDestroy()
    }


}