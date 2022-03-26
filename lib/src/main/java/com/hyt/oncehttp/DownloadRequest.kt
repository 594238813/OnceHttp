package com.hyt.oncehttp

import android.util.Log
import com.hyt.oncehttp.annotation.HttpMethod
import com.hyt.oncehttp.exception.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.system.measureTimeMillis

class DownloadRequest(val url:String,val file: File) : OnceRequest() {

    override var host = url

    override val api = ""

    override var method = HttpMethod.GET

    fun requestDownload(onProcess: (process:Int) -> Unit): Flow<File> {
        return flow {
            val parentFile = file.parentFile
            //判断父目录 是否存在 ，不存在 就创建
            if (parentFile != null) {
                if (!parentFile.exists()) {
                    parentFile.mkdirs()
                }
            }

            var response: Response
            val duration = measureTimeMillis {
                //执行返回
                response = makeRequest()
            }

            val body = response.body ?: throw ResponseException("response 为空")

            var mDownloadByte = 0L
            var readLength: Int
            val bytes = ByteArray(4096)
            val inputStream = body.byteStream()
            val fileOutputStream = FileOutputStream(file)
            try {
                while (inputStream.read(bytes).also { readLength = it } != -1) {
                    mDownloadByte += readLength.toLong()
                    fileOutputStream.write(bytes, 0, readLength)

                    withContext(Dispatchers.Main){
                        onProcess( (mDownloadByte.toDouble() / body.contentLength() * 100).toInt() )
                    }
                }
                inputStream.close()
                fileOutputStream.close()

            }catch (ex:Exception){
                ex.printStackTrace()
            }

            emit(file)

        }.flowOn(Dispatchers.IO)
    }




}