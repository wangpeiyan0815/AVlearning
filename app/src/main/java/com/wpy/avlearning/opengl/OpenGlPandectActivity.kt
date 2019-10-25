package com.wpy.avlearning.opengl

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wpy.avlearning.R
import com.wpy.avlearning.opengl.bean.MenuBean
import com.wpy.avlearning.opengl.render.FGLExerciseActivity
import com.wpy.avlearning.opengl.render.FGLViewActivity
import kotlinx.android.synthetic.main.activity_opengl_pandect.*

/**
 *  OpenGl 学习实现功能总览
 */
class OpenGlPandectActivity : AppCompatActivity() {

    private val menuList = ArrayList<MenuBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_pandect)
        menuList.add(MenuBean("DEMO", FGLExerciseActivity::class.java))
        menuList.add(MenuBean("绘制形体", FGLViewActivity::class.java))
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter(menuList)
        rv.adapter = adapter
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val datas = adapter.data as ArrayList<MenuBean>
            startActivity(Intent(this@OpenGlPandectActivity, datas.get(position).clazz))
        }
    }

    class MenuAdapter(var datas: List<MenuBean>) :
        BaseQuickAdapter<MenuBean, BaseViewHolder>(R.layout.item_menu, datas) {

        override fun convert(helper: BaseViewHolder, item: MenuBean) {
            helper.setText(R.id.menu_btn, item.name)
        }

    }
}