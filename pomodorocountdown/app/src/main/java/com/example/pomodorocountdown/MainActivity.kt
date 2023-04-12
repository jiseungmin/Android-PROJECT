package com.example.pomodorocountdown

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private val remainMinutesTextview: TextView by lazy {
        findViewById(R.id.remainminutes)
    }
    private val remainsecondTextView: TextView by lazy {
        findViewById(R.id.remainsecondTextView)
    }

    private val SeekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private var currentCountDownTimer: CountDownTimer? = null
    private var soundPool = SoundPool.Builder().build()
    private var tickingSoundID: Int? = null
    private var BellSoundID: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindView()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindView() {
        SeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    SeekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(SeekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(SeekBar: SeekBar?) {
                    SeekBar ?: return
                    if (SeekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }
            }
        )

    }

    private fun createCountDowntimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekbar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekbar(0)
        soundPool.autoPause()
        BellSoundID?.let { sounid ->
            soundPool.play(sounid, 1F, 1F, 0, 0, 1F)
        }
    }

    private fun startCountDown() {

        currentCountDownTimer = createCountDowntimer(SeekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundID?.let { soundid ->
            soundPool.play(soundid, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }


    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMills: Long) {
        val remainSeconds = remainMills / 1000

        remainMinutesTextview.text = "%02d'".format(remainSeconds / 60)
        remainsecondTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekbar(remainMills: Long) {
        SeekBar.progress = (remainMills / 1000 / 60).toInt()
    }

    private fun initSounds() {
        tickingSoundID = soundPool.load(this, R.raw.timer_ticking, 1)
        BellSoundID = soundPool.load(this, R.raw.timer_bell, 1)

    }

}