package com.test_okhttp.mainrequest

import com.hyt.oncehttp.OnceRequest
import com.hyt.oncehttp.PostContentType
import com.hyt.oncehttp.annotation.HttpMethod

class UpFileRequest:OnceRequest() {

    override var host = ""

    override var api = ""

    override var method  = HttpMethod.POST

    override var contentType = PostContentType.MULTIPART_FORM_DATA




}