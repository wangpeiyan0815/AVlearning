package com.wpy.avlearning.opengl.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wpy.avlearning.R
import kotlinx.android.synthetic.main.activity_fgl_exercise.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *  入门级
 */
class FGLExerciseActivity : AppCompatActivity() {

    // 加载顶点和片圆着色器
    private val vertexShaderCode =
        "precision mediump float;" +
                "attribute vec4 a_Position;" +
                "void main() {" +
                "    gl_Position = a_Position;" +
                "}"


    private val fragmentShaderCode =
        "precision mediump float;" +
                "void main() {" +
                "    gl_FragColor = vec4(0.0, 0.0, 1.0, 1.0);" +
                "}"

    // 确定绘制坐标点
    private val triangleCoords = floatArrayOf(
        0f, 0.5f, // top
        -0.5f, -0.5f, // bottom left
        0.5f, -0.5f // bottom right
    )

    private lateinit var vertexBuffer: FloatBuffer

    private var mProgramId: Int = 0

    private val COORDS_PER_VERTEX = 2

    private var glSurfaceViewWidth = 0

    private var glSurfaceViewHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fgl_exercise)
        init()
    }

    private fun init() {
        // 设置GL版本
        surfaceView.setEGLContextClientVersion(2)
        // 设置GL 渲染器
        surfaceView.setRenderer(SampleRenderer())
    }

    inner class SampleRenderer : GLSurfaceView.Renderer {

        override fun onDrawFrame(gl: GL10?) {
            // 设置清屏颜色
            GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f)
            // 清屏
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            // 设置视口，这里设置为整个GLSurfaceView区域
            // Set the viewport to the full GLSurfaceView
            GLES20.glViewport(0, 0, glSurfaceViewWidth, glSurfaceViewHeight)
            // 调用draw方法用TRIANGLES的方式执行渲染，顶点数量为3个
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, triangleCoords.size / COORDS_PER_VERTEX)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // 设置GL 窗口
            glSurfaceViewWidth = width
            glSurfaceViewHeight = height
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // 加载，编译 着色器
            val vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            val fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            GLES20.glShaderSource(vertexShader, vertexShaderCode)
            GLES20.glShaderSource(fragmentShader, fragmentShaderCode)
            GLES20.glCompileShader(vertexShader)
            GLES20.glCompileShader(fragmentShader)

            // 创建一个空的GL 程序
            mProgramId = GLES20.glCreateProgram()
            GLES20.glAttachShader(mProgramId, vertexShader)
            GLES20.glAttachShader(mProgramId, fragmentShader)
            // 链接 GL 程序
            GLES20.glLinkProgram(mProgramId)

            // 将三角形顶点数据放入buffer中
            val bb = ByteBuffer.allocateDirect(triangleCoords.size * java.lang.Float.SIZE)
            bb.order(ByteOrder.nativeOrder())
            vertexBuffer = bb.asFloatBuffer()
            vertexBuffer.put(triangleCoords)
            vertexBuffer.position(0)

            // 应用GL程序
            GLES20.glUseProgram(mProgramId)
            // 获取字段a_Position在shader中的位置 成员句柄
            val mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "a_Position")
            // 启动对应设置的参数
            GLES20.glEnableVertexAttribArray(mPositionHandle)
            //  指定a_Position所使用的顶点数据
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        }
    }
}