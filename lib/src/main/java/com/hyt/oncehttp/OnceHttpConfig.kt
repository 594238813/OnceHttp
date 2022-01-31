package com.hyt.oncehttp

import android.app.Application
import android.util.Log
import com.github.simonpercic.oklog.core.Logger
import com.github.simonpercic.oklog3.OkLogInterceptor
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class KtHttpConfig {

    companion object {
        lateinit var context:Application
        val instance by lazy {
            KtHttpConfig()
        }
    }

    var okHttpClient = OkHttpClient.Builder()
        .addInterceptor(OkLogInterceptor.builder()
            .withAllLogData()
            .setLogger(object:Logger{

                //这里使用 Log.e() 部分手机不支持 Log.i()
                override fun d(tag: String?, message: String?) {
                    Log.e(tag,message!!)
                }

                override fun w(tag: String?, message: String?) {
                    Log.e(tag,message!!)
                }

                override fun e(tag: String?, message: String?, throwable: Throwable?) {
                    Log.e(tag,message!!)
                }
            }).build())
        .eventListenerFactory { OnceRequest.PrintingEventListener() }
        .build()


    var host = ""
        set(value){
            require(value!=""){ "host 为空" }
            require(value.toHttpUrlOrNull()!=null){ "host不是url" }
            field = value
        }

    val header = mutableMapOf<String,String>()

    val mapData = mutableMapOf<String,String>()

}

fun ktHttpConfig(context:Application,block: KtHttpConfig.() -> Unit) = KtHttpConfig.instance.apply {
    KtHttpConfig.context = context
    block(this)
}.okHttpClient.newBuilder()

