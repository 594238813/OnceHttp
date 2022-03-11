package com.test_okhttp.repository

import com.hyt.oncehttp.makeOnceRequestGET
import com.hyt.oncehttp.makeOnceRequestPSOT_JSON
import com.test_okhttp.bean.*
import com.test_okhttp.mainrequest.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.HashMap

class MainRepository {

    companion object {
        val instance: MainRepository by lazy { MainRepository() }
    }

    //处理get  返回Flow
    fun getBanner() =
        BannerRequest().requestBackFlow<HttpData<List<BannerBean>>>()

    //处理get 连接中有切换分页   返回Flow
    fun getArticleList(page:Int) =
        ArticleListRequest("article/list/${page}/json?page_size=3").addParam()
            .requestBackFlow<HttpData<PageBean<ArticleBean>>>()

    //处理post 表单提交  返回Flow
    fun requestLogin(bean: RequestLoginBean) =
        LoginRequest().addParam(
            bean,
            //测试数据
//            mutableMapOf("key" to "abc","key2" to 123,"key3" to false,"key4" to 1.2f),
//            mutableMapOf("files" to mutableListOf(false,1,"abc"))

        ).requestBackFlow<HttpData<UserBean>>()

    //post json 请求  返回Flow
    fun requestLoginTest(bean: RequestLoginBean) =
        "user/login".makeOnceRequestPSOT_JSON(bean).requestBackFlow<HttpData<UserBean>>()


    //处理get 连接中有切换分页  返回 LiveData
    suspend fun getArticleListLiveData(page:Int) =
        ArticleListRequest("article/list/${page}/json?page_size=3")
            .requestBackLiveData<HttpData<PageBean<ArticleBean>>>()


    //字符串直接请求 返回LiveData
    suspend fun getArticleListWithString(page:Int) =
        "article/list/${page}/json?page_size=3".makeOnceRequestGET(
            //这里可以添加 参数  bean 、map  或者不填
        ).requestBackLiveData<HttpData<PageBean<ArticleBean>>>()

    //上传文件
    fun getUploadFile(file: File): Flow<HttpData<UploadBean>> {

        val map = HashMap<String, String>()

        return UpFileRequest()
            .addParam(map)
            .addUploadFile("fileName",file).requestBackFlow()
    }


    //测试方法  bean 可有 list 方式
    fun sendJsonSubList(bean: SuggectSubmitBean) =
        FeedBackRequest().addParam(bean).requestBackFlow<HttpData<String?>>()


}