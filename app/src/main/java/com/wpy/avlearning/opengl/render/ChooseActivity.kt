package com.wpy.avlearning.opengl.render

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wpy.avlearning.R
import com.wpy.avlearning.opengl.bean.MenuBean
import kotlinx.android.synthetic.main.activity_choose.*

class ChooseActivity : AppCompatActivity() {

    private val menuList = ArrayList<MenuBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        menuList.add(MenuBean("三角形", Triangle::class.java))
        menuList.add(MenuBean("正三角形", TriangleWithCamera::class.java))
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = MenuAdapter(menuList)
        rv.adapter = adapter
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val datas = adapter.data as ArrayList<MenuBean>
            val intent = Intent()
            intent.putExtra("name", datas[position].clazz)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    class MenuAdapter(datas: List<MenuBean>) :
        BaseQuickAdapter<MenuBean, BaseViewHolder>(R.layout.item_choose, datas) {

        override fun convert(helper: BaseViewHolder, item: MenuBean) {
            helper.setText(R.id.name_tv, item.name)
        }

    }
}