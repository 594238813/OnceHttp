package com.test_okhttp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test_okhttp.bean.ArticleBean
import com.test_okhttp.bean.HttpData
import com.test_okhttp.bean.PageBean
import com.test_okhttp.bean.RequestLoginBean
import com.test_okhttp.repository.MainRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {

    val repository = MainRepository.instance


    fun getBanner() = repository.getBanner()
        .onStart {
            //这里可以修改 viewmodel状态
            //比如 dialog 关闭显示
        }
        .catch {
            Log.e("catch","${it.message}")
        }
        .onCompletion {
            //关闭dialog
        }


    fun requestLogin(bean: RequestLoginBean) = repository.requestLogin(bean)
        .onStart {

        }
        .catch {
            Log.e("catch","${it.message}")
        }
        .onCompletion {

        }

    fun requestArticle(page:Int)= repository.getArticleList(page)
        .onStart {

        }
        .catch {
            Log.e("catch","${it.message}")
        }
        .onCompletion {

        }


    //livedata 方式
    private var articleList = MutableLiveData<PageBean<ArticleBean>>()
    val articleListLiveData:LiveData<PageBean<ArticleBean>>  = articleList

    fun getArticleListByLiveData(page:Int){
        viewModelScope.launch {
            articleList.value = repository.getArticleList22222(page).value?.data
        }
    }
}
