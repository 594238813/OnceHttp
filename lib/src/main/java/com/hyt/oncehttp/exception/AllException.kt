package com.hyt.oncehttp.exception


class BackMediaTypeException(msg:String) : HttpException(msg)

class ResponseException(msg:String) : HttpException(msg)

class DataException(msg:String) : HttpException(msg)

class HttpTimeOutExcception(msg:String):HttpException(msg)

open class HttpException(msg:String): Exception(msg)
