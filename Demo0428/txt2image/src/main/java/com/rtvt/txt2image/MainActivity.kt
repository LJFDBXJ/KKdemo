package com.rtvt.txt2image

import android.Manifest
import android.graphics.*
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    val path = Environment.getExternalStorageDirectory().absolutePath + "/DingTalk/abc.txt"
    val outPath = Environment.getExternalStorageDirectory().absolutePath + "/outImage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }


        var startTime = 0L
        btn.setOnClickListener {
            startTime = System.currentTimeMillis()
            str2Image(startTime)


        }

    }


    private fun txt2Image() {
        try {


        } catch (e: Exception) {
        }


    }


    private fun str2Image(startTime: Long) {
        val bufferedReader = BufferedReader(FileReader(path))
        var readLine = bufferedReader.readLine()
        val stringBuilder = StringBuilder()
        while (readLine != null) {
            stringBuilder.append(readLine)
            stringBuilder.append("\n")
            if (stringBuilder.length > 500) break
            readLine = bufferedReader.readLine()
        }
        bufferedReader.close()
        val textPaint = TextPaint()
        textPaint.textSize = 60f
        textPaint.isAntiAlias = true
        textPaint.color = Color.BLACK
        val watermarkBitmap = BitmapFactory.decodeStream(assets.open("shuiying.png"))
        val watermarkWidth = watermarkBitmap.width
        val watermarkHeight = watermarkBitmap.height
        for (i in 0 until stringBuilder.length step 500) {
            object : Thread() {
                override fun run() {
                    val end = if (i + 500 > stringBuilder.length) stringBuilder.length else i + 500
                    val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        StaticLayout.Builder.obtain(stringBuilder, i, end, textPaint, 1200)
                                .setLineSpacing(1.5f, 1.5f)
                                .build()
                    } else {
                        StaticLayout(stringBuilder, i, end, textPaint, 1200, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0f, true)
                    }
                    val bitmap = Bitmap.createBitmap(staticLayout.width + 10, staticLayout.height + 10, Bitmap.Config.ARGB_8888)
                    bitmap.setHasAlpha(true)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    //绘制文字
                    staticLayout.draw(canvas)

//                    canvas.translate(10f, 10f)
                    val xCount = (Math.ceil(staticLayout.width / watermarkWidth.toDouble())).toInt()
                    val yCount = (Math.ceil(staticLayout.height / watermarkHeight.toDouble())).toInt()
                    //平布绘制水印
                    for (x in 0 until xCount) {
                        for (y in 0 until yCount) {
                            canvas.drawBitmap(watermarkBitmap, x * watermarkWidth.toFloat(), y * watermarkHeight.toFloat(), null)
                        }
                    }
//                    watermarkBitmap.recycle()

                    //导出图片
//                    val bufferedOutputStream = BufferedOutputStream(FileOutputStream("$outPath-${i / 500}.png"))
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bufferedOutputStream)

//                    val bufferedOutputStream = BufferedOutputStream(FileOutputStream("$outPath-${i / 500}.png"))
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                    val byteArrayInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())
                    val byte = ByteArray(1024)
                    val fileOutputStream = FileOutputStream("$outPath-${i / 500}.png")
                    var read = byteArrayInputStream.read(byte)
                    while (read != -1) {
                        fileOutputStream.write(byte)
                        read = byteArrayInputStream.read(byte)
                    }

//                    val bufferedOutputStream = BufferedOutputStream(fileOutputStream)

//                    bufferedOutputStream.flush()
//                    bufferedOutputStream.close()
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    byteArrayInputStream.close()

                    bitmap.recycle()
                }

            }.start()
        }
    }
}
