package com.wpy.avlearning.opengl.render

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wpy.avlearning.R
import kotlinx.android.synthetic.main.activity_flgview.*

/**
 *  绘制形体
 */
class FGLViewActivity : AppCompatActivity() {

    private val REQ_CHOOSE = 0x0101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flgview)

        switcher_bt.setOnClickListener {
            startActivityForResult(Intent(this@FGLViewActivity, ChooseActivity::class.java), REQ_CHOOSE)
        }
    }

    override fun onResume() {
        super.onResume()
        mGLView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mGLView.onPause()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            mGLView.setShape(data?.getSerializableExtra("name") as Class<out Shape>)
        }
    }
}