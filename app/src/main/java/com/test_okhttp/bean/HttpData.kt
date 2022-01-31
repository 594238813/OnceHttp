package com.test_okhttp.bean

data class HttpData<T>(
    val errorCode:Int?,
    val errorMsg:String?,
    val data:T?
)