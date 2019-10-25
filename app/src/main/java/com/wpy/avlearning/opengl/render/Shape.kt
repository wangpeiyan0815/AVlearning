package com.wpy.avlearning.opengl.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.View

/**
 *实现了GLSurfaceView.Renderer 类才是真正算是开始能够在应用中使用OpenGL ES。这个类控制着与它关联的GLSurfaceView 绘制的内容。
 * 在renderer 里面有三个方法能够被Android系统调用，以便知道在GLSurfaceView绘制什么以及如何绘制
 */
abstract class Shape(var mView: View) : GLSurfaceView.Renderer {

    /**
     * // 加载、编译vertex shader和fragment shader
     */
    fun loadShader(type: Int, shaderCode: String): Int {
        //根据type创建顶点着色器或者片元着色器
        val shader = GLES20.glCreateShader(type)
        //将资源加入到着色器中，并编译
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

}