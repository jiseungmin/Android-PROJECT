package com.example.alarmapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import java.util.*

class MainActivity : AppCompatActivity() {

    private val onOffbutton: Button by lazy {
        findViewById(R.id.OnoffButton)
    }
    private val changeButton: Button by lazy {
        findViewById(R.id.changeButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // step1 뷰를 초기화 하기
        initOnoffButton()
        initChangeButton()

        // step2 데이터를 가져오기
        val model = fetchDateFromSharedPreferences()
        // step3 뷰에 데이터 그려주기
        renderView(model)



    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun initOnoffButton() {
        onOffbutton.setOnClickListener {
            // 데이터를 확인한다.
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener
            val newModel = saveAlarmModel(model.hour, model.minute, model.onoff.not())
            renderView(newModel)
            // 온오프에 따라 작업을 처리한다.
            // 오프 -> 알람을 제거
            // 온 -> 알람을 등록
            if (newModel.onoff) { //켜진 경우 -> 알람을 등록
                val Calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }
                val alarmManager =
                    getSystemService(Context.ALARM_SERVICE) as AlarmManager // 알람기능 가져오기
                val intent = Intent(this, AlarmReciver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_MUTABLE
                )

                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    Calendar.timeInMillis, // 값을 가져옴
                    AlarmManager.INTERVAL_DAY, //하루마다 실행
                    pendingIntent
                )

            } else {// 꺼진 경우
                cancelAlarm()
            }
        }
    }

    private fun initChangeButton() {
        changeButton.setOnClickListener {
            val calender = Calendar.getInstance() //현재시간을 가져온다.
            //timepickDialog 띄워줘서 시간을 설정을 하도록 하고 그시간을 가져와서
            TimePickerDialog(this, { picker, hour, minute ->
                // 데이터를 저장한다.
                val model = saveAlarmModel(hour, minute, false)
                // 뷰를 업데이트한다.
                renderView(model)
                // 기존에 있던 알람을 삭제한다.
                cancelAlarm()
            }, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show()
        }

    }

    private fun saveAlarmModel(hour: Int, minute: Int, onoff: Boolean): AlarmDisplayModel {
        val model = AlarmDisplayModel(hour = hour, minute = minute, onoff = onoff) //데이터 생성
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) { // with 함수를 이용해  데이터 집어넣기
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onoff)
            commit()
        }

        return model
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun fetchDateFromSharedPreferences(): AlarmDisplayModel {
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val timeDBValue = sharedPreferences.getString(ALARM_KEY, "9:30") ?: "9:30"
        val onoffDBValue = sharedPreferences.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")

        val alarmModel = AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onoff = onoffDBValue
        )
        // 보정예외처리
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReciver::class.java),
            PendingIntent.FLAG_MUTABLE
        )

        if ((pendingIntent == null) and alarmModel.onoff) { // 알람꺼져있는, 데이터는 켜져있는 경우
            alarmModel.onoff = false
        } else if ((pendingIntent != null) and alarmModel.onoff.not()) { // 알람이켜져있는, 데이터가 꺼져있는 경우
            pendingIntent.cancel() // 알람을 취소함
        }
        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel) { // 텍스트뷰에 표시
        findViewById<TextView>(R.id.ampmTextView).apply {
            text = model.ampmText
        }
        findViewById<TextView>(R.id.clockTextview).apply {
            text = model.timeText
        }
        findViewById<Button>(R.id.OnoffButton).apply {
            text = model.onofftext
            tag = model
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ALARM_REQUEST_CODE,
            Intent(this, AlarmReciver::class.java),
            PendingIntent.FLAG_MUTABLE
        )
        pendingIntent.cancel()
    }

    companion object { // 상수
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }
}