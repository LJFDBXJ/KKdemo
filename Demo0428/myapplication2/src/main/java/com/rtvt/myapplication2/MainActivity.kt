package com.rtvt.myapplication2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import kotlin.text.Typography.tm

class MainActivity : AppCompatActivity() {


    private var isStart = false
    private var isExit = false
    private var isPause = true

    private var time = 0

    private val thread = object : Thread() {
        override fun run() {
            while (!isExit) {
                if (isPause) {
                    runOnUiThread {
                        time++
                        text.text = time.toString()
                    }
                    try {
                        Thread.sleep(1000)
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn.setOnClickListener {
            if (!isStart) {
                isStart = true
                btn.text = "结束"
                thread.start()
            } else {
                isExit = true
                isStart = false
                btn.text = "开始"

            }
        }
        btn2.setOnClickListener {
            if (isPause) {
                isPause = false
                btn2.text = "继续"
            } else {
                isPause = true
                btn2.text = "暂停"
            }
        }
    }
}
