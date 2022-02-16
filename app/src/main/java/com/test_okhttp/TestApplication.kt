package com.test_okhttp

import android.app.Application
import com.hyt.oncehttp.ktHttpConfig


class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        ktHttpConfig(this) {
            host = "https://www.wanandroid.com/"
            //公共的 header参数
            header["appVersion"] = "1.0.0"
            //公共的 body参数
            //mapData["appVersion"] = "1.0.0"

            //可以传入一个client
            //okHttpClient = OkHttpClient.Builder().build()
        }




    }
}

