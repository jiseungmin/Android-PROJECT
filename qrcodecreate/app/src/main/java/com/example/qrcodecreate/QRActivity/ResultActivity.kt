package com.example.qrcodecreate.QRActivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodecreate.MainActivity
import com.example.qrcodecreate.R

class ResultActivity : AppCompatActivity() {

    private val resultview: TextView by lazy {
        findViewById(R.id.ResultView)
    }

    private val backbutton: Button by lazy {
        findViewById(R.id.backbutton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result)

        backbutton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}