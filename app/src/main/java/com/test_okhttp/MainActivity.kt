package com.test_okhttp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.test_okhttp.bean.RequestLoginBean


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        //flow
        lifecycleScope.launchWhenStarted {
            mainViewModel.getBanner()
                .collect{
                    Log.e("code", "${it.errorCode}")
                    it.data?.forEach {
                        Log.e("bean","${it.title}")
                    }
                }
        }

        lifecycleScope.launchWhenStarted {
            val bean = RequestLoginBean("账号","密码")
            mainViewModel.requestLogin(bean)
                .collect{
                    Log.e("code", "${it.errorCode}-${it.errorMsg}")
                    Log.e("nickname","${it.data?.nickname}")
                }
        }

        lifecycleScope.launchWhenStarted {
            mainViewModel.requestArticle(1)
                .collect{
                    Log.e("code", "${it.errorCode}")
                    Log.e("size", "${it.data?.size}")
                }
        }


        //livedata
        mainViewModel.getArticleListByLiveData(1)
        mainViewModel.articleListLiveData.observe(this){
            Log.e("livedata.size", "${it.size}")
        }

    }


}