package com.test_okhttp.repository

import com.hyt.oncehttp.makeOnceRequestGET
import com.hyt.oncehttp.makeOnceRequestPSOT_JSON
import com.test_okhttp.bean.*
import com.test_okhttp.mainrequest.ArticleListRequest
import com.test_okhttp.mainrequest.BannerRequest
import com.test_okhttp.mainrequest.LoginRequest
import com.test_okhttp.mainrequest.UpFileRequest
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.HashMap

class MainRepository {

    companion object {
        val instance: MainRepository by lazy { MainRepository() }
    }

    //处理get
    fun getBanner() =
        BannerRequest()
            .requestBackFlow<HttpData<List<BannerBean>>>()

    //处理get 连接中有切换分页
    fun getArticleList(page:Int) =
        ArticleListRequest("article/list/${page}/json?page_size=3")
            .requestBackFlow<HttpData<PageBean<ArticleBean>>>()

    //处理post 表单提交
    fun requestLogin(bean: RequestLoginBean) =
        LoginRequest().addParam(bean).requestBackFlow<HttpData<UserBean>>()

    //post json 请求
    fun requestLoginTest(bean: RequestLoginBean) =
        "user/login".makeOnceRequestPSOT_JSON(bean).requestBackFlow<HttpData<UserBean>>()


    //处理get 连接中有切换分页
    suspend fun getArticleListLiveData(page:Int) =
        ArticleListRequest("article/list/${page}/json?page_size=3")
            .requestBackLiveData<HttpData<PageBean<ArticleBean>>>()

    //字符串直接请求
    suspend fun getArticleListWithString(page:Int) =
        "article/list/${page}/json?page_size=3".makeOnceRequestGET(
            //这里可以添加 参数  bean 、map  或者不填
        ).requestBackLiveData<HttpData<PageBean<ArticleBean>>>()

    //上传文件
    fun getUploadFile(file: File): Flow<HttpData<UploadBean>> {
        val map = HashMap<String, String>()
        map["type"] = "3"               //type	String	1.头像、2.美好生活、3.意见反馈
        map["pictype"] = "2"                 //pictype	String	1.视频、2.图片

        return UpFileRequest()
            .addParam(map)
            .addUploadFile("fileName",file).requestBackFlow()
    }


}