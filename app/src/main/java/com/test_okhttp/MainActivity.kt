package com.test_okhttp

import android.Manifest
import android.R.attr
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.test_okhttp.bean.RequestLoginBean
import com.wildma.pictureselector.PictureBean
import com.wildma.pictureselector.PictureSelector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var mainViewModel:MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

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

        //post 请求 登录
        lifecycleScope.launchWhenStarted {
            val bean = RequestLoginBean("账号","密码")
            mainViewModel.requestLogin(bean)
                .collect{
                    Log.e("code", "${it.errorCode}-${it.errorMsg}")
                    Log.e("nickname","${it.data?.nickname}")
                }
        }


        //返回 Livedata
        mainViewModel.getArticleListByLiveData(1)
        mainViewModel.articleListLiveData.observe(this){
            Log.e("livedata.size", "${it.size}")
        }

        findViewById<TextView>(R.id.btn_onceRequest).setOnClickListener {
            lifecycleScope.launchWhenStarted {
                //处理get 连接中有切换分页
                val bean = mainViewModel.requestArticle(1).first()
                Log.e("code", "${bean.errorCode}")
                Log.e("size", "${bean.data?.size}")
            }
        }

        findViewById<TextView>(R.id.btn_upload).setOnClickListener {
            //上传图片
            PictureSelector
                .create(this, PictureSelector.SELECT_REQUEST_CODE)
                .selectPicture(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            PictureSelector.SELECT_REQUEST_CODE->{
                val pictureBean: PictureBean? = data?.getParcelableExtra(PictureSelector.PICTURE_RESULT)

                //获取图片绝对路径后  上传图片
                lifecycleScope.launch {
                    val file = File(pictureBean?.path.toString())
                    mainViewModel.requestUploadFile(file)
                        .collect{
                            Log.e("code", "${it.data?.url}")
                        }
                }
            }
        }
    }



}