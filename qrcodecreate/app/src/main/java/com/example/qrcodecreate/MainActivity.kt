package com.example.qrcodecreate

import com.example.qrcodecreate.QRActivity.ResultActivity
import com.example.qrcodecreate.RecyclevView_fs.User
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val qrbutton: Button by lazy {
        findViewById(R.id.QRScan)
    }
//    private val web_view: WebView by lazy {
//        findViewById(R.id.web_view)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Qrbutton()
    }


    private fun Qrbutton() {
        qrbutton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("아동급식 QR코드를 스캔하여 주세요.")
            integrator.initiateScan() // scan
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "보여드리겠습니다." + result.contents, Toast.LENGTH_LONG).show()
//                web_view.settings.javaScriptEnabled = true
//                web_view.webViewClient = WebViewClient()
//                web_view.loadUrl(result.contents)
                val userInfo = JSONObject(result.contents)
                val userbank = userInfo.getString("bank")
                val useraccount = userInfo.getString("account")

                val database =
                    Firebase.database("https://qrapp-2f5a6-default-rtdb.asia-southeast1.firebasedatabase.app/")
                val myRef = database.getReference("User")
                val userid = myRef.push().key!!
                val user = User("$userbank", "$useraccount")
                myRef.child(userid).setValue(user)
                notification("$userbank", "$useraccount")

                val intent = Intent(this, ResultActivity::class.java)
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun notification(add1:String, add2:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel_name = "n"
            val channel_id = "n"
            val descriptionText = ""
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channel_id, channel_name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val intent = Intent(this, Support::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

            val builder = NotificationCompat.Builder(this, channel_id)
                .setContentTitle("아동 후원")
                .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText("$add1  $add2")

            with(NotificationManagerCompat.from(this)) {
                notify(999, builder.build())

            }

        }
    }
}