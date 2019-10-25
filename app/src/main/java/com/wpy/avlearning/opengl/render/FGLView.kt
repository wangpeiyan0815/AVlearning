package com.wpy.avlearning.opengl.render

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import java.lang.Exception

class FGLView : GLSurfaceView {

    private lateinit var renderer: FGLRender

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun init() {
        // 设置 OpenGl 版本
        setEGLContextClientVersion(2)
        // 设置渲染器
        renderer = FGLRender(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun setShape(clazz: Class<out Shape>) {
        try {
            renderer.setShape(clazz)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}