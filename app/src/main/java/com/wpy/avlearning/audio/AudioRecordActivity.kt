package com.wpy.avlearning.audio

import android.media.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.wpy.avlearning.R
import com.wpy.avlearning.audio.GlobalConfig.*
import kotlinx.android.synthetic.main.activity_audio_record.*
import java.io.*


/**
 *  1. 构造一个AudioRecord对象，其中需要的最小录音缓存buffer大小可以通过getMinBufferSize方法得到。
 *  如果buffer容量过小，将导致对象构造的失败。
 *
 *  2. 初始化一个buffer，该buffer大于等于AudioRecord对象用于写声音数据的buffer大小。
 *  3. 开始录音
 *  4. 创建一个数据流，一边从AudioRecord中读取声音数据到初始化的buffer，一边将buffer中数据导入数据流。
 *  5. 关闭数据流
 *  6. 停止录音
 *
 *  注意权限检查， 读写权限  录音权限  AudioRecord 录制pcm 格式不能直接播放
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class AudioRecordActivity : AppCompatActivity() {

    private val TAG = "AudioRecordActivity"
    private var mAudioRecord: AudioRecord? = null
    private var mAudioTrack: AudioTrack? = null
    private var mRecordBufSize: Int = 0
    private lateinit var fileInputStream: FileInputStream

    private var isRecording: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)
        start_record.setOnClickListener(onClickListener)
        stop_record.setOnClickListener(onClickListener)
        play_record.setOnClickListener(onClickListener)
        stop_play.setOnClickListener(onClickListener)
        convert.setOnClickListener(onClickListener)
    }

    private val onClickListener: View.OnClickListener = View.OnClickListener {
        when (it.id) {
            R.id.start_record -> startRecord()
            R.id.stop_record -> stopRecord()
            R.id.play_record -> playInModeStream()
            R.id.stop_play -> stopPlay()
            R.id.convert -> convert()
        }
    }

    private fun convert() {
        val pcmToWac = PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        val pcmFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        val wavFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav")
        if (!wavFile.mkdirs()) {
            Log.e(TAG, "wavFile Directory not created")
        }
        if (wavFile.exists()) {
            wavFile.delete()
        }
        pcmToWac.pcmToWav(pcmFile.absolutePath, wavFile.absolutePath)
    }

    /**
     * audioSource  录音源
     * sampleRateInHz  采样率
     * channelConfig   音频通道
     * audioFormat 音频数据返回的格式
     * bufferSizeInBytes  音频数据的缓冲区
     */
    private fun startRecord() {
        //最小录音缓存buffer
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
            CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize
        )
        val data = ByteArray(minBufferSize)
        // 创建文件
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        if (!file.mkdirs()) {
            Log.e("TAG", "Directory not created")
        }
        // 检查文件是否存在,存在删掉
        if (file.exists()) {
            file.delete()
        }
        // 开始录音
        mAudioRecord?.startRecording()
        isRecording = true
        // pcm数据无法直接播放，保存为WAV格式。
        Thread{
            // 创建数据流
            var os: FileOutputStream? = null
            try {
                os = FileOutputStream(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            if (os != null) {
                while (isRecording) {
                    val read = mAudioRecord?.read(data, 0, minBufferSize)
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
//                            os.write(read!!) 惨痛的教训bug 找了半个小时
                            os.write(data)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                try {
                    os.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun stopRecord() {
        isRecording = false
        mAudioRecord?.let {
            it.stop()
            it.release()
        }
    }

    /**
     * 播放，使用stream模式
     *
     * SAMPLE_RATE_INHZ 对应pcm音频的采样率
     * channelConfig 对应pcm音频的声道
     * AUDIO_FORMAT 对应pcm音频的格式
     */
    private fun playInModeStream() {
        val channelConfig = AudioFormat.CHANNEL_OUT_MONO
        val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE_INHZ, channelConfig, AUDIO_FORMAT)
        mAudioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(SAMPLE_RATE_INHZ)
                .setEncoding(AUDIO_FORMAT)
                .setChannelMask(channelConfig)
                .build(),
            minBufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        mAudioTrack?.play()
        // 创建文件getExternalFilesDir
        val file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        try {
            fileInputStream = FileInputStream(file)
            Thread {
                try {
                    val tempBuffer = ByteArray(minBufferSize)
                    while (fileInputStream.available() > 0) {
                        val readCount = fileInputStream.read(tempBuffer)
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                            readCount == AudioTrack.ERROR_BAD_VALUE
                        ) {
                            continue
                        }
                        if (readCount != 0 && readCount != -1) {
                            mAudioTrack?.write(tempBuffer, 0, readCount)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun stopPlay() {
        mAudioTrack?.let {
            mAudioTrack?.stop()
            mAudioTrack?.release()
        }
    }
}