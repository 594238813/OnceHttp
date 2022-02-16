package com.hyt.oncehttp

import android.content.Context
import android.net.ConnectivityManager
import com.hyt.oncehttp.exception.HttpException
import com.hyt.oncehttp.exception.HttpTimeOutExcception
import kotlinx.coroutines.CancellableContinuation
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InterruptedIOException
import java.net.UnknownHostException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


suspend fun Call.awaitResponse(): Response {
    return suspendCancellableCoroutine { continuation ->

        continuation.invokeOnCancellation {
           this.cancel()
        }
        enqueue(KtHttpCallBack(continuation))
    }
}

class KtHttpCallBack(val continuation: CancellableContinuation<Response>) :Callback{

    override fun onFailure(call: Call, e: IOException) {

        val networkInfo =
            (KtHttpConfig.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo

        when(e){
            is InterruptedIOException->{
                //超时异常
                throw HttpTimeOutExcception("超时异常 timeout")
            }
            is UnknownHostException->{
                networkInfo?.let {
                    if(it.isConnected) throw HttpException("有网，服务器异常")
                }
                throw HttpException("没网，找不到地址")
            }
            else->{
                continuation.resumeWithException(e)
            }
        }
    }

    override fun onResponse(call: Call, response: Response) {
        continuation.resume(response)
    }

}