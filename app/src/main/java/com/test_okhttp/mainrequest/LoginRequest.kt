package com.test_okhttp.mainrequest

import com.hyt.oncehttp.*
import com.hyt.oncehttp.annotation.HttpMethod
import com.test_okhttp.bean.HttpData
import com.test_okhttp.bean.UserBean

class LoginRequest : OnceRequest(){

    override var host = "https://www.wanandroid.com/"

    override var api = "user/login"

    override var method  = HttpMethod.POST

    override var contentType = PostContentType.FORM_DATA

    override fun beforeRequest(map: MutableMap<String, Any>): MutableMap<String, Any> {
        //这里可以追加参数 或者 加密签名修改参数
        val nMap = map
        nMap["sing"] = "sing签名"
        return nMap
    }

    override fun <T> afterRequest(bean: T): T {
        //这里是 app 自定义拦截数据
        //flow 最后接收到的 是这里的bean
        bean as HttpData<*>
        if(bean.data is UserBean){
            bean.data.nickname = "改变昵称了"
        }
        return bean
    }
}