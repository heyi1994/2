package com.blue.bluedemo.socket

/**
 * Author: Heyi.
 * Date: 2017/5/29.
 * Package:com.blue.bluedemo.socket.
 * Desc:连接状态监听
 */
interface ConnectionStatListener {
    /**
     * Socket连接成功
     * @param name 远端设备的名字
     */
    fun connectionSuccess(name:String)

    /**
     * Socket连接失败
     */
    fun connectionFail()


    /**
     * 收到另外一台设备的数据
     * 可以在服务里转换，可以是图片、音频等字节数据
     *  我只做了个简单的聊天而已
     */
    fun obtainMsg(msg:String)

    /**
     * 断开连接
     */
    fun disconnection()



}