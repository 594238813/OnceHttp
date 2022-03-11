package com.hyt.oncehttp

data class HttpBean(
    val contentType: String,
    val duration: String,
    val host: String,
    val method: String,
    val request: HttpRequest,
    val response: HttpResponse
)

data class HttpRequest(
    val body: MutableMap<String, Any>,
    val header: MutableMap<String, String>
)

data class HttpResponse(
    val body: Any?,
    val header:  Map<String, String>
)


