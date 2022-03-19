package com.hyt.oncehttp.annotation

import androidx.annotation.StringDef
import com.hyt.oncehttp.annotation.PostContentType.Companion.FORM_DATA
import com.hyt.oncehttp.annotation.PostContentType.Companion.JSON_DATA
import com.hyt.oncehttp.annotation.PostContentType.Companion.MULTIPART_FORM_DATA

//指定注解的保留策略，AnnotationRetention.SOURCE表示只保留源码中，编译时删除
@Retention(AnnotationRetention.SOURCE)
@StringDef(value = [MULTIPART_FORM_DATA, FORM_DATA,JSON_DATA ])
annotation class PostContentType {
    companion object {
        const val MULTIPART_FORM_DATA = "multipart/form-data"
        const val FORM_DATA = "application/x-www-form-urlencoded;charset=utf-8"
        const val JSON_DATA = "application/json;charset=utf-8"
    }
}