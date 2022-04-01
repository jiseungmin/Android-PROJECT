package com.example.bmiproject

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.pow

class ResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val height = intent.getIntExtra("height",0)
        val weight = intent.getIntExtra("weight",0)

        Log.d("ResultActivity", "height : $height, weight : $weight")

        val BMI = weight/ (height / 100.0).pow(2.0)
        val resultText = when {
            BMI >= 35.0 -> "고도 비만"
            BMI >= 30.0 -> "중정도 비만"
            BMI >= 25.0 -> "경도 비만"
            BMI >= 23.0 -> "과체중"
            BMI >= 18.5 -> "정상 체중"
            else -> "저체중"
        }

        val resultvalueTextview = findViewById<TextView>(R.id.BMIresultTextView)
        val resultStringTextview = findViewById<TextView>(R.id.resultTextView)

        resultvalueTextview.text =BMI.toString()
        resultStringTextview.text = resultText

    }

}