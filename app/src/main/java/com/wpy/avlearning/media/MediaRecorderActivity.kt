package com.wpy.avlearning.media

import android.content.Intent
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.wpy.avlearning.R
import kotlinx.android.synthetic.main.activity_media_recorder.*
import java.io.File

class MediaRecorderActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var isRecorder: Boolean = false

    private val mCamera: Camera by lazy {
        Camera.open()
    }

    private val mMediaRecorder by lazy {
        MediaRecorder()
    }

    private val videoRecorderFile: String

    init {
        val fileDir = File("${Environment.getExternalStorageDirectory().absolutePath}/test")
        fileDir.mkdirs()
        videoRecorderFile = "$fileDir/video_record.mp4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_recorder)
        surfaceView.holder.addCallback(this)
        bt_recorder.setOnClickListener {
            if (isRecorder) {
                stopRecorder()
            } else {
                startRecorder()
            }
        }
    }

    private fun startRecorder() {
        mMediaRecorder.apply {
            mCamera.unlock()
            setCamera(mCamera)
            // 设置录制的角度，如果与摄像头不符，会出现视频角度不对的问题
            setOrientationHint(90)
            // 设置录音和录制视频的来源
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            // 还可以设置其他的信息
            // 输出的视频格式
//            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            // 设置编码的格式
//            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            // 设置视频的大小，必须要在设置视频格式之后设置，否则会报错
//            setVideoSize()
            //视频的帧率
//            setVideoFrameRate(25)
            // 设置录制的质量
            setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
            // 设置输出路径
            setOutputFile(videoRecorderFile)
            setPreviewDisplay(surfaceView.holder.surface)
            prepare()
            start()
        }
        isRecorder = true
        bt_recorder.text = "点击停止录制"
    }

    private fun stopRecorder() {
        mMediaRecorder.apply {
            stop()
            release()
        }
        mCamera.lock()
        isRecorder = false
        bt_recorder.text = "点击开始录制"
        startActivity(Intent(this, MediaPlayActivity::class.java))
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        mCamera.setDisplayOrientation(90)
        mCamera.setPreviewDisplay(holder)
        mCamera.startPreview()
        mCamera.autoFocus { success, camera -> }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mCamera.release()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mCamera.stopPreview()
        mCamera.setPreviewDisplay(holder)
        mCamera.startPreview()
        mCamera.autoFocus { success, camera -> }
    }
}