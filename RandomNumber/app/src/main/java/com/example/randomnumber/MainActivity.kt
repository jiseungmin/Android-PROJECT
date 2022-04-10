package com.example.randomnumber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val clearButton : Button by lazy {
        findViewById(R.id.clearButton)
    }
    private val addButton : Button by lazy {
        findViewById(R.id.addButton)
    }
    private val runButton : Button by lazy {
        findViewById(R.id.RunButton)
    }
    private val NumberPicker : NumberPicker by lazy {
        findViewById(R.id.NumberPicker)
    }
    private val NumberTextviewlist : List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.Textview1),
            findViewById<TextView>(R.id.Textview2),
            findViewById<TextView>(R.id.Textview3),
            findViewById<TextView>(R.id.Textview4),
            findViewById<TextView>(R.id.Textview5),
            findViewById<TextView>(R.id.Textview6)
        )
    }

    private var didrun = false
    private val PickNumberset = hashSetOf<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NumberPicker.minValue = 1
        NumberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()

    }

    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()
            didrun = true
            list.forEachIndexed{ index, number ->
                val textView = NumberTextviewlist[index]
                textView.text = number.toString()
                textView.isVisible = true

              setnumberBackground(number, textView)
            }
        }
    }


    private fun initAddButton(){
        addButton.setOnClickListener {
            if(didrun){
                Toast.makeText(this,"초기화 후 사용해주세요.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(PickNumberset.size >= 5){
                Toast.makeText(this,"번호는 5개 까지만 선택가능합니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(PickNumberset.contains(NumberPicker.value)){
                Toast.makeText(this,"이미 선택한 번호입니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = NumberTextviewlist[PickNumberset.size]
            textView.isVisible = true
            textView.text = NumberPicker.value.toString()

            setnumberBackground(NumberPicker.value, textView)


            PickNumberset.add(NumberPicker.value)

        }
    }
    private fun setnumberBackground(number:Int, textView: TextView){
        when(number){
            in 1..10 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yello)
            in 11..20 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }


    private fun initClearButton() {
        clearButton.setOnClickListener {
            PickNumberset.clear()
            NumberTextviewlist.forEach{
                it.isVisible = false
            }
            didrun = false
        }
    }


    private fun getRandomNumber():List<Int>{
        val numberlist = mutableListOf<Int>().apply {
            for (i in 1..45){
                if(PickNumberset.contains(i)){
                    continue
                }
                this.add(i)
            }
        }
        numberlist.shuffle()
        val newlist = PickNumberset.toList() + numberlist.subList(0,6-PickNumberset.size)
        return newlist.sorted()
    }
}