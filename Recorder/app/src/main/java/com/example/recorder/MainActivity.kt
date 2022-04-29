package com.example.recorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private val SoundvisualizerView: SoundvisualizeView by lazy {
        findViewById(R.id.visualizerView)
    }
    private val Recordtimetextview: CountIpView by lazy {
        findViewById(R.id.Recordtimetextview)
    }
    private val recordButton: RecordButton by lazy {
        findViewById(R.id.recordButton)
    }
    private val resetButton: Button by lazy {
        findViewById(R.id.resetButton)
    }
    private val requirPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value

            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recordButton.updateiconWithstate(value)
        }
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestaudioPermissions()
        initViews()
        bindview()
        initvariables()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissions = requestCode == REQUEST_RECORD_AUDIO_PERMISSIONS &&
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED

        if (!audioRecordPermissions) {
            finish()
        }

    }

    private fun bindview() {
        SoundvisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
        resetButton.setOnClickListener {
            stopplay()
            SoundvisualizerView.clearVisualization()
            Recordtimetextview.clearCounttime()
            state = State.BEFORE_RECORDING
        }
        recordButton.setOnClickListener {
            when (state) {
                State.ON_RECORDING -> {
                    stopRecoding()
                }
                State.BEFORE_RECORDING -> {
                    startRecoding()
                }
                State.AFTER_RECORDING -> {
                    startplaying()
                }
                State.ON_PLAYING -> {
                    stopplay()
                }
            }
        }
    }

    private fun initvariables() {
        state = State.BEFORE_RECORDING
    }

    private fun startRecoding() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        SoundvisualizerView.startVisualizing(false)
        Recordtimetextview.startCountup()
        state = State.ON_RECORDING
    }

    private fun startplaying() {
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()
            }
        player?.setOnCompletionListener {
            stopplay()
            state = State.AFTER_RECORDING
        }
        player?.start()
        SoundvisualizerView.startVisualizing(true)
        Recordtimetextview.startCountup()
        state = State.ON_PLAYING
    }

    private fun stopplay() {
        player?.release()
        player = null
        SoundvisualizerView.stopVistualizing()
        Recordtimetextview.stopCountup()
        state = State.AFTER_RECORDING
    }

    private fun stopRecoding() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        SoundvisualizerView.stopVistualizing()
        Recordtimetextview.stopCountup()
        state = State.AFTER_RECORDING
    }

    private fun requestaudioPermissions() {
        requestPermissions(requirPermissions, REQUEST_RECORD_AUDIO_PERMISSIONS)
    }

    private fun initViews() {
        recordButton.updateiconWithstate(state)
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSIONS = 201
    }
}