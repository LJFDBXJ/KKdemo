package com.rtvt.imagedemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {

            ImageUtil.getShareThumbnail("http://misccdn.wanxiangapp.com/Cover/Cartoon/20190430523139df.jpg") {
                runOnUiThread {
                    imageView.setImageBitmap(it)
                }
            }
        }
    }
}
