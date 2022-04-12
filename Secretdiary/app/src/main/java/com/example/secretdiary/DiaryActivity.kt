package com.example.secretdiary

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener

class DiaryActivity: AppCompatActivity() {

    private val diaryEditText:EditText by lazy {
        findViewById<EditText>(R.id.diaryEdittext)
    }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        val detailpreference = getSharedPreferences("diary",Context.MODE_PRIVATE)

        diaryEditText.setText(detailpreference.getString("detail", ""))

        val runnable = Runnable {
            getSharedPreferences("diary",Context.MODE_PRIVATE).edit{
                putString("detail",diaryEditText.text.toString())
            }
        }

        diaryEditText.addTextChangedListener {

                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable,500)

            }
        }


    }

