package com.example.secretdiary

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import java.security.Key

class MainActivity : AppCompatActivity() {

    private val NumberPicker1:NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.NumberPicker1)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val NumberPicker2:NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.NumberPicker2)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val NumberPicker3:NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.NumberPicker3)
            .apply {
                minValue = 0
                maxValue = 9
            }
    }
    private val OpenButton:AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.OpenButton)
    }
    private val ChangeButton:AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.ChangeButton)
    }
    private var Changepasswordmode = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NumberPicker1
        NumberPicker2
        NumberPicker3

        OpenButton.setOnClickListener {
            if (Changepasswordmode){
                Toast.makeText(this,"비밀번호 변경중입니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)

            val passwordFromUser =
                "${NumberPicker1.value}${NumberPicker2.value}${NumberPicker3.value}"

            if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
                startActivity(Intent(this, DiaryActivity::class.java))

            } else {
                showErrorAlterDialog()
            }
        }


        ChangeButton.setOnClickListener {
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${NumberPicker1.value}${NumberPicker2.value}${NumberPicker3.value}"
            if (Changepasswordmode){
                passwordPreference.edit(true){
                    putString("password",passwordFromUser)
                }
                Changepasswordmode = false
                ChangeButton.setBackgroundColor(Color.BLACK)



            }else{
                if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {

                    Changepasswordmode = true
                    Toast.makeText(this,"변경할 비밀번호를 입력하세요.",Toast.LENGTH_SHORT).show()
                    ChangeButton.setBackgroundColor(Color.RED)

                } else {
                   showErrorAlterDialog()
                }

            }
        }

    }

    private fun showErrorAlterDialog(){
        AlertDialog.Builder(this)
            .setTitle("실패!")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create()
            .show()
    }
}