package com.hyt.oncehttp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hyt.oncehttp.PostContentType.Companion.FORM_DATA
import com.hyt.oncehttp.PostContentType.Companion.JSON_DATA
import com.hyt.oncehttp.PostContentType.Companion.MULTIPART_FORM_DATA
import com.hyt.oncehttp.annotation.HttpMethod
import com.hyt.oncehttp.annotation.HttpMethod.Companion.GET
import com.hyt.oncehttp.annotation.HttpMethod.Companion.POST
import com.hyt.oncehttp.exception.BackMediaTypeException
import com.hyt.oncehttp.exception.DataException
import com.hyt.oncehttp.exception.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.lang.NullPointerException

abstract class OnceRequest{

    abstract val api  :String

    var client = KtHttpConfig.instance.okHttpClient

    open var host = KtHttpConfig.instance.host

    private var header = mutableMapOf<String,String>()

    @HttpMethod
    open var method = GET

    @PostContentType
    open var contentType = FORM_DATA

    //所有参数
    //参数用的是 追加 不清除
    //所有参数的都在map里
    private var mapData = mutableMapOf<String,String>()

    init {
        //获取全局 头数据  和  公共参数
        header += KtHttpConfig.instance.header
        mapData += KtHttpConfig.instance.mapData
    }

    //可以重写修改或增加 header
    open fun onceHeader(header:MutableMap<String,String>): MutableMap<String, String> {
        return header
    }

    //构造request 并执行
    suspend fun makeRequest(): Response {
        //构造一个request 一个request 包含 url、header，公共参数
        val requestBuilder = Request.Builder()
        //合并 公共的header 和 单独一条请求的hader
        header += onceHeader(header)
        requestBuilder.headers(header.toHeaders())
        //合并 公共的参数 和 单独一条的参数
        mapData += beforeRequest(mapData)
        //选择get 还是post
        when(method){
            GET->{
                val getUrl = makeGetUrl("${host}${api}")
                requestBuilder.url(getUrl)
            }
            POST->{
                requestBuilder.url("${host}${api}")
                //post
                makePostData(contentType,requestBuilder)
            }
        }

        //执行
        return client.newCall(requestBuilder.build()).awaitResponse()
    }

    private fun makeGetUrl(url: String): String {
        var newUrl = url
        newUrl += if(url.indexOf("?")<-1) "?" else "&"
        mapData.forEach { (k, v) ->
            newUrl+= "${k}=${v}&"
        }
        //去掉最后一个参数
        return newUrl.dropLast(1)
    }

    //构造post 参数
    private fun makePostData(@PostContentType contentType: String, requestBuilder: Request.Builder) {
        //根据 Content-Type 设置参数
        when(contentType){
            MULTIPART_FORM_DATA->{
                //上传文件  构建上传文件

                //转换 除 文件的 额外参数
                val formBody = FormBody.Builder()
                mapData.forEach {
                    formBody.addEncoded(it.key,it.value)
                }

                val requestBody = MultipartBody.Builder().apply {
                    setType(MultipartBody.FORM)
                    //添加 文件 部分
                    addFormDataPart(name,file.name,file.asRequestBody())
                    //添加 额外参数
                    addPart(formBody.build())
                }

                requestBuilder.post(requestBody.build())
            }
            FORM_DATA->{
                //构建表单参数
                val formBody = FormBody.Builder()
                mapData.forEach {
                    formBody.addEncoded(it.key,it.value)
                }
                requestBuilder.post(formBody.build())
            }
            JSON_DATA->{
                //构建json参数
                val data = Gson().toJson(mapData)
                requestBuilder.post(
                    data.toRequestBody(JSON_DATA.toMediaType())
                )
            }
        }
    }

    private var name = ""
    private lateinit var file: File

    //添加上传文件
    fun addUploadFile(name:String,file: File):OnceRequest{
        this.name = name
        this.file = file
        return this
    }

    //请求返回-flow
    inline fun <reified T> requestBackFlow(): Flow<T> {
        return flow<T> {
            //flow发射
            emit(requestBackBean())
        }.flowOn(Dispatchers.IO)
    }

    suspend inline fun <reified T> requestBackLiveData(): LiveData<T> {
        return MutableLiveData(requestBackBean<T>())
    }

    //请求返回-bean
    suspend inline fun <reified T> requestBackBean() : T{
        //执行返回
        val response = makeRequest()

        //判断是不是200..299
        if(!response.isSuccessful){
            throw ResponseException("服务器异常-${response.code}-${response.message}")
        }
        //简单判断 application/json 类型  排除、图片、视频、音频
        //只允许通过json
        if(response.body?.contentType().toString().indexOf("application/json")==-1){
            throw BackMediaTypeException("返回类型只允许application/json")
        }

        try{
            val json = response.body?.charStream()
            val resultBean = Gson().fromJson<T>(json, object : TypeToken<T>(){}.type )

            //afterRequest拦截 认为  数据是否正常 或者修改数据
            //先拦截 在发射
            return afterRequest(resultBean)
        }catch (ex:Exception){
            ex.printStackTrace()
            throw DataException("数据解析异常")
        }finally {
            response.close()
        }
    }

    //请求之前 修改 请求的数据
    open fun beforeRequest(map:MutableMap<String,String>) = mapData

    //请求之后 修改 返回的数据
    open fun <T> afterRequest(bean:T) : T = bean

    //添加参数  bean->map
    //这里要注意 如果传入bean，表单提交。如果bean有 list map等不会被提交
    fun addParam(bean: Any):OnceRequest {
        val fields = bean.javaClass.declaredFields
        //这里使用了反射，获取对象的 变量名 和 值， GSON内部用的也是反射
        fields.forEach { field->
            field.isAccessible = true
            val value = field.get(bean)
            val key = field.name

            //判断基本类型
            if(field.javaClass.isPrimitive || value is String){
                mapData[key] = value.toString()
            }
        }
        return this
    }

    //添加参数 map->map
    fun addParam(map: Map<String,String>):OnceRequest {
        mapData += map
        return this
    }

    fun cleanParam():OnceRequest{
        mapData.clear()
        return this
    }

    //事件监听 这里可以做很多事情
    class PrintingEventListener : EventListener() {

        var start = 0L
        var end = 0L

        private fun printEvent(name: String) {
//            Log.e("printEvent", name)
        }

        override fun callStart(call: Call) {
            super.callStart(call)
            start = System.currentTimeMillis()
        }

        override fun callEnd(call: Call) {
            super.callEnd(call)
            end = System.currentTimeMillis()

            printEvent("${call.request().url} -useTime:${end-start}")
        }
    }
}


//使用扩展方法 快速请求

//根据字符串 直接请求
//get 方式
fun String.makeOnceRequestGET(bean: Any?=null) = object: OnceRequest() {
    override val api: String =  this@makeOnceRequestGET
}.apply {
    if (bean != null) {
        addParam(bean)
    }
}

fun String.makeOnceRequestGET(map: Map<String,String>) = object: OnceRequest() {
    override val api: String = this@makeOnceRequestGET
}.addParam(map)


//post json 方式
fun String.makeOnceRequestPSOT_JSON(bean: Any?=null) = object: OnceRequest() {
    override val api: String =  this@makeOnceRequestPSOT_JSON
    override var method = POST
    override var contentType = JSON_DATA
}.apply {
    if (bean != null) {
        addParam(bean)
    }
}
fun String.makeOnceRequestPSOT_JSON(map: Map<String,String>) = object: OnceRequest() {
    override val api: String =  this@makeOnceRequestPSOT_JSON
    override var method = POST
    override var contentType = JSON_DATA
}.addParam(map)

//post form 方式
fun String.makeOnceRequestPSOT_FORM(bean: Any?=null) = object: OnceRequest() {
    override val api: String =  this@makeOnceRequestPSOT_FORM
    override var method = POST
    override var contentType = FORM_DATA
}.apply {
    if (bean != null) {
        addParam(bean)
    }
}

fun String.makeOnceRequestPSOT_FORM(map: Map<String,String>) = object: OnceRequest() {
    override val api: String =  this@makeOnceRequestPSOT_FORM
    override var method = POST
    override var contentType = FORM_DATA
}.addParam(map)
