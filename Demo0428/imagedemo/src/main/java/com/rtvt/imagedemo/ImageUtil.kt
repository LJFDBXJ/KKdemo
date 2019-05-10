package com.rtvt.imagedemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by rtvt-03 on 2019/5/7.
 */
object ImageUtil {

    private val okHttpClient by lazy {
        val builder = OkHttpClient.Builder()
                .connectTimeout(120L, TimeUnit.SECONDS)
                .readTimeout(120L, TimeUnit.SECONDS)
                .writeTimeout(120L, TimeUnit.SECONDS)
                .build()
        builder
    }
    private const val IMAGE_SIZE = 32768//微信分享图片大小限制

    fun getShareThumbnail(url: String, getBitmap: Function1<Bitmap, Unit>) {
        okHttpClient.newCall(
                Request.Builder()
                        .url(url)
                        .get()
                        .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        val bytes = body.bytes()
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        options.inSampleSize = calculateInSampleSize(options, 200, 200)
                        options.inJustDecodeBounds = false
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                        var quality = 100
                        if (bitmap != null) {
                            val output = ByteArrayOutputStream()
                            val cropBitmap = cropBitmap(bitmap)
                            cropBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
                            while (output.toByteArray().size > IMAGE_SIZE && quality > 10) {
                                output.reset()
                                cropBitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
                                quality -= 10
                            }
                            getBitmap(cropBitmap)
                            if (bitmap.isRecycled) {
                                bitmap.recycle()
                            }
                            if (cropBitmap.isRecycled) {
                                cropBitmap.recycle()
                            }
                            output.close()
                        }
                    }

                }
            }

        })
    }


    //获取采样（压缩比）
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val outWidth = options.outWidth
        val outHeight = options.outHeight
        var inSampleSize = 1
        if (outHeight > reqHeight || outHeight > reqWidth) {
            while ((outHeight / inSampleSize) >= reqHeight || (outHeight / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize
    }

    fun cropBitmap(bitmap: Bitmap): Bitmap {//从中间截取一个正方形
        val w = bitmap.width// 得到图片的宽，高
        val h = bitmap.height;
        val cropWidth = if (w >= h) h else w;// 裁切后所取的正方形区域边长

        return Bitmap.createBitmap(
                bitmap, (bitmap.width - cropWidth) / 2,
                (bitmap.height - cropWidth) / 2, cropWidth, cropWidth
        )
    }

}