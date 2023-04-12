package com.example.qrcodecreate.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton

import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodecreate.MainActivity
import com.example.qrcodecreate.R
import com.example.qrcodecreate.RecyclevView_fs.RecycleviewActivity
import com.example.qrcodecreate.Support
import javax.xml.datatype.DatatypeConstants.DURATION

class SplashActivity: AppCompatActivity() {
    private val gif : ImageButton by lazy {
        findViewById(R.id.splash)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        Handler().postDelayed({
            val intent = Intent(this, RecycleviewActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        },3000)
    }
}