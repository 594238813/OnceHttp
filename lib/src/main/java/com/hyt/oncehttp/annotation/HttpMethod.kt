package com.hyt.oncehttp.annotation

import androidx.annotation.StringDef
import com.hyt.oncehttp.annotation.HttpMethod.Companion.GET
import com.hyt.oncehttp.annotation.HttpMethod.Companion.POST

//指定注解的保留策略，AnnotationRetention.SOURCE表示只保留源码中，编译时删除
@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [POST, GET ])
annotation class HttpMethod {
    companion object {
        const val POST  = "POST"
        const val GET  = "GET"
    }
}

