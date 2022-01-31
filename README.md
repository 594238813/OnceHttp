# OnceHttp
一次简单的请求  
对于使用者来说，重点关注入参、出参  
出参：无非Response.body  
入参：map或者是bean  
一个请求：无非包括host、api、header、body  
###使用
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


