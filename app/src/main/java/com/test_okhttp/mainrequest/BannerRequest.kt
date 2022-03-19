package com.test_okhttp.mainrequest

import android.util.Base64
import com.hyt.oncehttp.*
import com.hyt.oncehttp.annotation.HttpMethod
import com.hyt.oncehttp.annotation.PostContentType

open class BannerRequest : OnceRequest() {

    override val api = "banner/json"

    override var method = HttpMethod.GET

    override var contentType = PostContentType.JSON_DATA


    override fun onceHeader(header:MutableMap<String,String>): MutableMap<String, String> {
        val map = mutableMapOf<String,String>()

        //为这一个请求添加header, 或者移除公共的header , header 不能有中文 否则会报错 使用base64一下
        map["token"] = Base64.encodeToString("abc123123".toByteArray(), Base64.NO_WRAP)
        return map
    }



}

