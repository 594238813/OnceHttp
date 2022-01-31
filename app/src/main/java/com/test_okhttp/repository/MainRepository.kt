package com.test_okhttp.repository

import com.test_okhttp.bean.*
import com.test_okhttp.mainrequest.ArticleListRequest
import com.test_okhttp.mainrequest.BannerRequest
import com.test_okhttp.mainrequest.LoginRequest

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


    //处理get 连接中有切换分页22222
    suspend fun getArticleList22222(page:Int) =
        ArticleListRequest("article/list/${page}/json?page_size=3")
            .requestBackLiveData<HttpData<PageBean<ArticleBean>>>()
}