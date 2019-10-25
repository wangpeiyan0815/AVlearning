package com.wpy.avlearning.opengl.render

import android.opengl.GLES20
import android.opengl.Matrix
import android.view.View
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 *  正三角形绘制
 *  参考链接：
 *  https://www.jianshu.com/p/7e0471100605
 *  https://blog.csdn.net/junzia/article/details/52817978
 */
class TriangleWithCamera(view: View) : Shape(view) {

    // 定义顶点与片元着色器
    private val vertexShaderCode =
        "attribute vec4 vPosition;" +
                "uniform mat4 vMatrix;" +
                "void main() {" +
                "  gl_Position = vMatrix*vPosition;" +
                "}";

    private val fragmentShaderCode =
        "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}"

    private val mVertexBuffer: FloatBuffer

    var triangleCoords = floatArrayOf(
        0.5f, 0.5f, 0.0f, // top
        -0.5f, -0.5f, 0.0f, // bottom left
        0.5f, -0.5f, 0.0f  // bottom right
    )


    //设置颜色，依次为红绿蓝和透明通道
    internal var color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    val COORDS_PER_VERTEX = 3
    val mProgramId: Int

    val vertexStride = COORDS_PER_VERTEX * 4
    //顶点个数
    val vertexCount = triangleCoords.size / COORDS_PER_VERTEX

    private val mViewMatrix = FloatArray(16)
    private val mProjectMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)


    init {
        // 处理加载数据
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        mVertexBuffer = bb.asFloatBuffer()
        mVertexBuffer.put(triangleCoords)
        mVertexBuffer.position(0)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 创建GL 程序
        mProgramId = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgramId, vertexShader)
        GLES20.glAttachShader(mProgramId, fragmentShader)
        GLES20.glLinkProgram(mProgramId)
    }

    override fun onDrawFrame(p0: GL10?) {
        // 使用GL 程序
        GLES20.glUseProgram(mProgramId)
        // 获取变换矩阵句柄
        val mMatrixHandler = GLES20.glGetUniformLocation(mProgramId, "vMatrix")
        // 指定Matrix值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        // 获取顶点句柄
        val mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        // 放入三角形数据
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES20.GL_FLOAT,
            false,
            vertexStride,
            mVertexBuffer
        )
        // 获取片元句柄
        val mColorHandle = GLES20.glGetUniformLocation(mProgramId, "vColor")
        // 设置绘制三角形颜色
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        val ratio = width.toFloat() / height
        //设置透视投影
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        //设置相机位置
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {

    }

}