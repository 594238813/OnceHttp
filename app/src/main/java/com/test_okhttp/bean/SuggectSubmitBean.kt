package com.test_okhttp.bean

data class SuggectSubmitBean(
    val contactNumber: String,
    val description: String,
    val filesList: List<Files>,
    val suggestType: String,
    val password:String = "aaa"
)

data class Files(
    val filePath: String
)