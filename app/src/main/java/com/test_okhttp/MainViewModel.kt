package com.test_okhttp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test_okhttp.bean.*
import com.test_okhttp.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

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

    fun requestUploadFile(file: File)= repository.getUploadFile(file)
        .onStart {
            Log.e("onStart","图片上传中")
        }
        .catch {
            Log.e("catch","${it.message}")
        }
        .onCompletion {
            Log.e("onCompletion","图片上传 完成")
        }


    //livedata 方式
    private var articleList = MutableLiveData<PageBean<ArticleBean>>()
    val articleListLiveData:LiveData<PageBean<ArticleBean>>  = articleList

    fun getArticleListByLiveData(page:Int){
        viewModelScope.launch(Dispatchers.IO)  {
            articleList.postValue( repository.getArticleListLiveData(page).value?.data)
        }
    }


    fun sendJsonSubList(bean: SuggectSubmitBean) =
        repository.sendJsonSubList(bean)
            .onStart {

            }.catch {
                Log.e("catch","${it.message}")
            }.onCompletion {

            }


    val processLiveData = MutableLiveData(0)
    val dialogFlag = MutableLiveData(false)

    fun downloadFile(url:String, file: File)
        = repository.downloadFile(url,file){
            processLiveData.value = it
        }.onStart {
            processLiveData.value = 0
            dialogFlag.value = true
        }.catch {
            Log.e("catch","${it.message}")
            dialogFlag.value = false
        }.onCompletion {
            dialogFlag.value = false
        }



}
