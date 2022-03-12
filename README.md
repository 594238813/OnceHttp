# OnceHttp
一次简单的请求  ，基于Okhttp、Kotlin、Coroutine。为了AAC架构的Repository层获取网络数据更方便。

对于使用者来说，重点关注 入参、出参

入参：key-value的map 或者 是一个Bean对象

出参：Response.body 的 json

## 使用
### 1、引入
```kotlin
maven { url 'https://jitpack.io' }
```
app模块 build.gradle

```kotlin
implementation "com.squareup.okhttp3:okhttp:4.9.0"
implementation "com.github.594238813:OnceHttp:$lastVersion"
```
快速使用

1、使用字符串或继承 构建一个请求

2、请求 requestBackFlow

### 2、构建
构建一个请求类 `LoginRequest` 继承 `OnceRequest` , 重写 host、api、method、contentType

```kotlin
class LoginRequest : OnceRequest(){
    override var host = "https://www.wanandroid.com/"
    override var api = "user/login"
    override var method  = HttpMethod.POST
    override var contentType = PostContentType.FORM_DATA
}
```
或者不重写，默认 GET

### 3、请求
开始请求

```kotlin
LoginRequest().addParam(bean).requestBackFlow<HttpData<UserBean>>()
```
请求后 返回 Flow、LiveData或Bean

```kotlin
requestBackFlow(): Flow<T> 
requestBackLiveData(): LiveData<T>
requestBackBean() : T
```
## 4、可选
1. 追加参数 使用 `addParam` 追加参数 Bean或者Map

```kotlin
LoginRequest().addParam(bean).requestBackFlow<HttpData<UserBean>>()

fun addParam(vararg args: Any) 
```
这里使用了不定参数，合并所有对象。
2. 在Application里，配置全局公共参数

```kotlin
ktHttpConfig(this) {
    host = "https://www.wanandroid.com/"
    //公共的 header参数
    header["appVersion"] = "1.0.0"
    //公共的 body参数
    //mapData["appVersion"] = "1.0.0"
    //可以传入一个client
    //okHttpClient = OkHttpClient.Builder().build()
}
```
3. 参数拦截，一次请求可以在 请求前  和 请求后 拦截数据，由于可以合并参数，map的value是Any类型

```kotlin
class LoginRequest : OnceRequest(){
    override fun beforeRequest(map: MutableMap<String, Any>): MutableMap<String, Any> {
        //这里可以追加参数 或者 加密签名修改参数
        val nMap = map
        return nMap
    }

    override fun <T> afterRequest(bean: T): T {
        //这里是 app 自定义拦截数据 可以进行解密操作
        bean as HttpData<*>
        return bean
    }
}
```
### 5、其他
上传文件，使用`addUploadFile`  添加上传的文件

```kotlin
UpFileRequest().addParam(map).addUploadFile("fileName",file).requestBackFlow()
```
更快捷的OnceRequest

#### 字符串直接构造get请求
```kotlin
"article/list/${page}/json?page_size=3".makeOnceRequestGET(
            //这里可以添加 参数  bean 、map  或者不填
        ).requestBackLiveData<HttpData<PageBean<ArticleBean>>>()
```
或者 `makeOnceRequestPSOT_JSON`

```kotlin
"article/list/${page}/json?page_size=3".makeOnceRequestPSOT_JSON()
    .requestBackLiveData<HttpData<PageBean<ArticleBean>>>()
```
GET请求 可以

```kotlin
makeOnceRequestGET( bean 或 map )
```
POST请求 可以

```kotlin
"".makeOnceRequestPSOT_JSON( bean 或 map )
"".makeOnceRequestPSOT_FORM( bean 或 map )
```
