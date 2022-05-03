package com.example.pushnotification

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private val resultTextView: TextView by lazy { // resultTextView 연결
        findViewById(R.id.resultTextView)
    }

    private val firebaseTokenTextView: TextView by lazy {
        findViewById(R.id.firebaseTokenText)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initfirebase()
        updateResult()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        updateResult(true)
    }

    // cloud msesage 보내는 것은 03 강의에서  알수있음 블로그에 적고 여기는 지우기

    private fun initfirebase() { // firebase 토큰을 가져오는 것을 성공하면 텍스트뷰에 결과를 나타내라.
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseTokenTextView.text = task.result
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱런처") +
                if (isNewIntent) {
                    "(으)로 갱신되었습니다."
                } else {
                    "(으)로 실행했습니다."
                }
    }
}