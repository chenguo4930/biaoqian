package com.shenrui.label.biaoqian.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shenrui.label.biaoqian.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_open.setOnClickListener {
            startActivity(Intent(this, BiaoQianActivity::class.java))
        }
        btn_input.setOnClickListener {
            toast("暂未开通")
        }
    }

}
