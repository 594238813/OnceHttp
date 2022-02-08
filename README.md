# OnceHttp
一次简单的请求  
对于使用者来说，重点关注入参、出参  
出参：Response.body  
入参：keyvalue的map或者是bean  
一个请求：包括host、api、header、body  
### 使用
使用前配置
```
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
请求继承 OnceRequest() 就好，api、header、body可以自由更改
```
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
```
直接使用 可以看我的demo
```
  LoginRequest().addParam(bean).requestBackFlow<HttpData<UserBean>>()
```
有3中返回数据的方式
```
requestBackLiveData(): LiveData<T> 
requestBackFlow(): Flow<T>
requestBackBean():T
```
也为一次请求 增加了 两个 数据的 前后拦截方式
```
//请求之前 修改 请求的数据
open fun beforeRequest(map:MutableMap<String,String>) = mapData
//请求之后 修改 返回的数据
open fun <T> afterRequest(bean:T) : T = bean

```
2022-2-8  
增加内容  
使用字符串直接请求  
```
  "article/list/${page}/json?page_size=3".makeOnceRequestGET(
            //这里可以添加 参数  bean 、map  或者不填
        ).requestBackLiveData<HttpData<PageBean<ArticleBean>>>()
```
可以直接使用GET请求，也可以`makeOnceRequestPSOT_JSON` post json方式，也可以 `makeOnceRequestPSOT_FORM` post form 方式


内容较多，不是很完善，持续更新中
