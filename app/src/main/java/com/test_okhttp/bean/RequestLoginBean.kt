package com.test_okhttp.bean

class RequestLoginBean(
    val username:String,
    val password:String,
    val loginList:MutableMap<String,String> = mutableMapOf(Pair("aaa","aaa"))
    )