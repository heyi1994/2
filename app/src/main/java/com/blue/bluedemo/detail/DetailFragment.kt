package com.blue.bluedemo.detail

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.blue.bluedemo.R
import com.blue.bluedemo.adapter.MsgAdaper
import com.blue.bluedemo.base.BaseFragment
import com.blue.bluedemo.main.MainActivity
import com.blue.bluedemo.socket.OnObtainMsgListener
import com.orhanobut.logger.Logger
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_detail.*

/**
 * Author: Heyi.
 * Date: 2017/5/29.
 * Package:com.blue.bluedemo.detail.
 * Desc:
 */
class DetailFragment:BaseFragment(),OnObtainMsgListener{

    private var btn_send:Button?=null
    private var ed_msg:EditText?=null
    private var msgs:ArrayList<String> =ArrayList<String>()
    private var recycle:RecyclerView?=null
    private val me="[me]"

    private var msg_adapter:MsgAdaper?=null
    private var mainActivity:MainActivity?=null

    override fun init(view : View) {
        val string = getString(R.string.connected)
        val textView = view.findViewById(R.id.m_title) as TextView
        Logger.i(string)
        textView.text=String.format(string,arguments.getString("name"))
       ed_msg=view.findViewById(R.id.ed_msg) as EditText
       btn_send=view.findViewById(R.id.btn_send) as Button
       recycle=view.findViewById(R.id.recycler) as RecyclerView
        recycle?.layoutManager= LinearLayoutManager(activity)
        recycle?.itemAnimator= FadeInRightAnimator()
        recycle?.itemAnimator?.addDuration=1000
        msg_adapter = MsgAdaper(msgs, getActivity())
        recycle?.adapter=msg_adapter


        ed_msg?.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(ed_msg?.text.toString().trim())){
                    btn_send?.isClickable=true
                }else{
                    btn_send?.isClickable=false
                }
            }
        })

        btn_send?.setOnClickListener {
           val info=me+ed_msg?.text.toString().trim()
            msgs.add(info)
            msg_adapter?.notifyItemChanged(msgs.size-1)
            mainActivity?.sendMsg(ed_msg?.text.toString().trim())
        }


    }

    override fun obtainMsg(msg: String) {
        msgs.add(msg)
        msg_adapter?.notifyItemChanged(msgs.size-1)
    }

    override fun getLayoutId(): Int {
        return R.layout.frag_detail
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = activity as MainActivity
        mainActivity?.setOnObtainMsgListener(this)
    }
}