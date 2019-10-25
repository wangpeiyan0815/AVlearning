package com.wpy.avlearning.opengl.render

import android.opengl.GLES20
import android.view.View
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class FGLRender(view: View) : Shape(view) {

    private lateinit var clazz: Class<out Shape>

    private var mShape: Shape? = null

    public fun setShape(shape: Class<out Shape>) {
        this.clazz = shape
    }

    /**
     * 每一次View的重绘都会调用
     */
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        mShape?.onDrawFrame(gl)
    }

    /**
     *  如果视图的几何形状发生变化（例如，当设备的屏幕方向改变时），则调用此方法
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置视图窗口
        GLES20.glViewport(0, 0, width, height)
        mShape?.onSurfaceChanged(gl,width,height)
    }

    /**
     *  在View的OpenGL环境被创建的时候调用
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // 清屏设置屏幕颜色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        try {
            // 反射获取构造函数
            val constructor = clazz.getDeclaredConstructor(View::class.java)
            constructor.isAccessible = true
            mShape = constructor.newInstance(mView) as Shape
        }catch (e:Exception){
            e.printStackTrace()
            mShape = Triangle(mView)
        }
        mShape?.onSurfaceCreated(gl,config)
    }
}