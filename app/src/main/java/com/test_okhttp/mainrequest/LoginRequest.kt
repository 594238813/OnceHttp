package com.test_okhttp.mainrequest

import com.hyt.oncehttp.*
import com.hyt.oncehttp.annotation.HttpMethod
import com.test_okhttp.bean.HttpData
import com.test_okhttp.bean.UserBean

class LoginRequest : OnceRequest(){

    override var api = "user/login"

    override var method  = HttpMethod.POST

    override var contentType = PostContentType.FORM_DATA

    override fun beforeRequest(map: MutableMap<String, String>): MutableMap<String, String> {
        val nMap = map
        nMap["deviceId"] = "设备id"
        return nMap
    }
    
    override fun <T> afterRequest(bean: T): T {
        //这里是 app 自定义拦截数据
        //flow 最后接收到的 是这里的bean
        bean as HttpData<UserBean>
        bean.data?.nickname = "改变昵称了"
        return bean
    }
}