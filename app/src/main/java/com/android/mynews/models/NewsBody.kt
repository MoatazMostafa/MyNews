package com.android.mynews.models


import com.google.gson.annotations.SerializedName

data class NewsBody(
    @SerializedName("articles")
    var articles: List<Article>?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("totalResults")
    val totalResults: Int?
)