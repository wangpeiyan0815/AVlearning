package com.wpy.avlearning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wpy.avlearning.audio.AudioRecordActivity
import com.wpy.avlearning.media.MediaRecorderActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start_audio_record.setOnClickListener(onClickListener)
        start__mediaRecorder.setOnClickListener(onClickListener)
    }

    private val onClickListener:View.OnClickListener = View.OnClickListener {
        when(it.id){
            R.id.start_audio_record ->{
                startIntent(AudioRecordActivity::class.java)
            }
            R.id.start__mediaRecorder ->{
                startIntent(MediaRecorderActivity::class.java)
            }
        }
    }

    private fun startIntent(clzss:Class<*>){
        startActivity(Intent(this, clzss))
    }
}
