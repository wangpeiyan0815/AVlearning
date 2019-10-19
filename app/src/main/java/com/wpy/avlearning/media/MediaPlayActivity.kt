package com.wpy.avlearning.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.wpy.avlearning.R
import kotlinx.android.synthetic.main.activity_media_play.*
import java.io.File

class MediaPlayActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {
    private val videoRecorderFile: String
    private val mMediaPlay by lazy {
        MediaPlayer()
    }

    init {
        val fileDir = File("${Environment.getExternalStorageDirectory().absolutePath}/test")
        fileDir.mkdirs()
        videoRecorderFile = "$fileDir/video_record.mp4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_play)
        mMediaPlay.apply {
            setDataSource(videoRecorderFile)
            setOnPreparedListener(this@MediaPlayActivity)
            prepare()
            start()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mMediaPlay.setDisplay(surfaceView.holder)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlay.stop()
        mMediaPlay.release()
    }
}