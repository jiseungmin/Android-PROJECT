package com.example.recorder

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import java.util.jar.Attributes

class RecordButton(
    context: Context,
    attrs: AttributeSet
): AppCompatImageButton(context, attrs) {

     fun updateiconWithstate(state: State){
         when(state){
             State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
             }
             State.ON_RECORDING -> {
                 setImageResource(R.drawable.ic_stop)
             }
             State.AFTER_RECORDING -> {
                 setImageResource(R.drawable.ic_play)
             }
             State.ON_PLAYING -> {
                 setImageResource(R.drawable.ic_stop)
             }
         }
     }
}