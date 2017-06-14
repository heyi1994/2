package com.blue.bluedemo.adapter

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blue.bluedemo.R

/**
 * Author: Heyi.
 * Date: 2017/5/29.
 * Package:com.blue.bluedemo.adapter.
 * Desc:[me]
 */
class MsgAdaper(var msg:ArrayList<String>,var context:Context): RecyclerView.Adapter<MsgAdaper.MsgHolder>() {
    override fun getItemCount(): Int {
        return msg.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MsgHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.item_msg_other, parent, false)
        return MsgHolder(inflate)
    }

    override fun onBindViewHolder(holder: MsgHolder?, position: Int) {
        if (msg[position].startsWith("[me]")){
            holder?.card_oth?.visibility=View.GONE
            holder?.me_msg?.text=msg[position].substring(4,msg[position].length)
        }else{
            holder?.card_me?.visibility=View.GONE
            holder?.msg?.text=msg[position]
        }
    }


    inner class MsgHolder(var view: View): RecyclerView.ViewHolder(view){
     var msg:TextView?=null
     var me_msg:TextView?=null
     var  card_me:CardView?=null
     var  card_oth:CardView?=null

        init {
            msg=view.findViewById(R.id.msg) as TextView
            me_msg=view.findViewById(R.id.me_msg) as TextView
            card_me=view.findViewById(R.id.card_me) as CardView
            card_oth=view.findViewById(R.id.card_oth) as CardView
        }
    }


}