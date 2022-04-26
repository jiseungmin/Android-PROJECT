package com.example.pictureframe

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoFlameActivity : AppCompatActivity() {

    private val photolist = mutableListOf<Uri>()

    private val backgroundphotoview: ImageView by lazy {
        findViewById(R.id.backgroundphotoimageview)
    }

    private val photoimageview: ImageView by lazy {
        findViewById(R.id.photoimageview)
    }

    private var currentPosition = 0

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activitiy_photoframe)
        getphotoimageIntent()
    }

    private fun getphotoimageIntent() {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photolist.add(Uri.parse(it))
            }
        }
    }

    private fun starttiemr() {
        timer = timer(period = 5 * 1000) {
            runOnUiThread {

                val current = currentPosition
                val next = if (photolist.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundphotoview.setImageURI(photolist[current])

                photoimageview.alpha = 0f
                photoimageview.setImageURI(photolist[next])
                photoimageview.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
                Log.d("photofram", "5초가 지나감")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        starttiemr()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

}