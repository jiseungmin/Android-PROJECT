package com.example.qrcodecreate.RecycleView

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodecreate.R
import com.example.qrcodecreate.RecyclevView_fs.User
import com.example.qrcodecreate.bootpay.BootpayAactivity

class ListAdapter(private val context: Context): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var userList = mutableListOf<User>()


    fun setListData(data: MutableList<User>) {
        userList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = userList[position]
//        holder.bank.text = user.bank
//        holder.account.text = user.account_number.toString()
        holder.myButton.setOnClickListener { v ->
            val intent = Intent(v.context, BootpayAactivity::class.java)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bank: TextView = itemView.findViewById(R.id.bank)
        val account: TextView = itemView.findViewById(R.id.account)
        val myButton = itemView.findViewById<Button>(R.id.supportbutton)
    }



}

