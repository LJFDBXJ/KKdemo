package com.rtvt.umdemo2

import android.app.Application
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

/**
 * Created by rtvt-03 on 2019/5/7.
 */
class App : Application() {


    override fun onCreate() {
        super.onCreate()

        UMConfigure.init(this, "5cd178303fc19506e8000551", null, UMConfigure.DEVICE_TYPE_PHONE, null)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)
        // 打开统计SDK调试模式
        UMConfigure.setLogEnabled(true)
    }
}