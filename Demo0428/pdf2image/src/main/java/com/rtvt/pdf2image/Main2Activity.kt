package com.rtvt.pdf2image

import android.Manifest
import android.graphics.*
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.concurrent.Executors
import com.shockwave.pdfium.PdfiumCore
import android.graphics.Bitmap


class Main2Activity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    var page = 0

    val path = Environment.getExternalStorageDirectory().absolutePath + "/DingTalk/abcd.pdf"
    val outPath = Environment.getExternalStorageDirectory().absolutePath + "/outfile/outpdf"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        }


        val fileDescriptor = ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfiumCore = PdfiumCore(this)
        val pdfDocument = pdfiumCore.newDocument(fileDescriptor)
        pdfiumCore.getPageHeight(pdfDocument,0)

        val pageCount = pdfiumCore.getPageCount(pdfDocument)


//        image.setImageBitmap(bitmap)

        val watermarkBitmap = BitmapFactory.decodeStream(assets.open("shuiying.png"))
        val watermarkWidth = watermarkBitmap.width
        val watermarkHeight = watermarkBitmap.height

        btn.setOnClickListener {
            val zStartTime = System.currentTimeMillis()
            for (i in 0 until pageCount) {
                val startTime = System.currentTimeMillis()
//                Log.i(TAG, "start: $i===$startTime")
                pdfiumCore.openPage(pdfDocument, i)

                val width = (pdfiumCore.getPageWidthPoint(pdfDocument, i)*1.4).toInt()
                val height = (pdfiumCore.getPageHeightPoint(pdfDocument, i)*1.4).toInt()
                val bitmap = Bitmap.createBitmap(width, height,
                        Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawColor(Color.WHITE)
                pdfiumCore.renderPageBitmap(pdfDocument, bitmap, i, 0, 0,
                        width, height)
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                val xCount = (Math.ceil(width / watermarkWidth.toDouble())).toInt()
                val yCount = (Math.ceil(height / watermarkHeight.toDouble())).toInt()
                //平布绘制水印
                for (x in 0 until xCount) {
                    for (y in 0 until yCount) {
                        canvas.drawBitmap(watermarkBitmap, x * watermarkWidth.toFloat(), y * watermarkHeight.toFloat(), null)
                    }
                }
                Log.i(TAG, "end: $i===" + (System.currentTimeMillis() - startTime))
                val bufferedOutputStream = BufferedOutputStream(FileOutputStream("$outPath-$i.png"))
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
                bitmap.recycle()
            }
            Log.i(TAG, "end: $===" + (System.currentTimeMillis() - zStartTime))
        }

    }
}
