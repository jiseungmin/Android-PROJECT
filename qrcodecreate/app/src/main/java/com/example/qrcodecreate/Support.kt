package com.example.qrcodecreate
import android.content.Intent
import com.example.qrcodecreate.RecycleView.ListAdapter
import com.example.qrcodecreate.RecycleView.ListViewModel
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodecreate.bootpay.BootpayAactivity


class Support : AppCompatActivity() {
    private lateinit var adapter: ListAdapter
    private val viewModel by lazy { ViewModelProvider(this).get(ListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycleview)

        adapter = ListAdapter(this)

        val recyclerView : RecyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        observerData()
    }


    fun observerData(){
        viewModel.fetchData().observe(this, Observer {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        })
    }

}