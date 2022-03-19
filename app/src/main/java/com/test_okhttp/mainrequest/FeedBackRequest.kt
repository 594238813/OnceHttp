package com.test_okhttp.mainrequest

import com.hyt.oncehttp.OnceRequest
import com.hyt.oncehttp.annotation.PostContentType.Companion.JSON_DATA
import com.hyt.oncehttp.annotation.HttpMethod

class FeedBackRequest :OnceRequest() {


    override val api = ""

    override var method = HttpMethod.POST

    override var contentType = JSON_DATA


    override fun onceHeader(header: MutableMap<String, String>): MutableMap<String, String> {

        return header
    }

}