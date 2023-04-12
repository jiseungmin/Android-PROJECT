package com.example.qrcodecreate.RecyclevView_fs

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodecreate.R
import com.example.qrcodecreate.bootpay.BootpayAactivity

class MyAdapter(private val userlist: ArrayList<User>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val user : User = userlist[position]
        holder.ment.text = user.ment
        holder.shop_name.text = user.shop_name
//        holder.account_number.text = user.account_number.toString()
        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, BootpayAactivity::class.java)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val ment : TextView = itemView.findViewById(R.id.mnet)
        val shop_name : TextView = itemView.findViewById(R.id.shop_name)
//        val account_number : TextView = itemView.findViewById(R.id.account_number)

    }
}