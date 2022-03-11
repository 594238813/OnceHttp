package com.test_okhttp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.test_okhttp.bean.*
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

        //get
        lifecycleScope.launchWhenStarted {
            //测试 1
            mainViewModel.getBanner()
                .collect{
                    Log.e("code", "${it.errorCode}")
                    it.data?.forEach {
                        Log.e("bean","${it.title}")
                    }
                }

            //测试2  测试 bean中 包含 list 对象   的传参方式
//            val st = "这是图片路径"
//            val list = mutableListOf(Files(st))
//            val bean = SuggectSubmitBean(
//                suggestType = "TYPE06",
//                description = "123",
//                contactNumber = "17625908757",
//                filesList = list
//            )
//            mainViewModel.sendJsonSubList(bean).collect{
//                Log.e("code", "${it.message}")
//            }

            //测试3 POST   请求 登录
            val bean = RequestLoginBean("wanandroid账号","密码")
            mainViewModel.requestLogin(bean)
                .collect{
                    Log.e("code", "${it.errorCode}-${it.errorMsg}")
                    Log.e("nickname","${it.data?.nickname}")
                }
        }

        //测试4  返回 Livedata
        mainViewModel.getArticleListByLiveData(1)
        mainViewModel.articleListLiveData.observe(this){
            Log.e("livedata.size", "${it.size}")
        }

        findViewById<TextView>(R.id.btn_onceRequest).setOnClickListener {
            lifecycleScope.launchWhenStarted {
                //测试5 处理get中 有分页
                val bean = mainViewModel.requestArticle(1).first()
                Log.e("code", "${bean.errorCode}")
                Log.e("size", "${bean.data?.size}")


                //测试6   由于内部方法是 suspend  需要在 协程中 且  是IO线程上
//                val bean2 = withTimeoutOrNull(3000){
//                    withContext(Dispatchers.IO){
//                        "article/list/1/json?page_size=3".makeOnceRequestGET().requestBackBean<HttpData<PageBean<ArticleBean>>>()
//                    }
//                }
//                Log.e("code", "${bean2?.data?.size}")
            }
        }

        findViewById<TextView>(R.id.btn_upload).setOnClickListener {
            //测试7  上传图片
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