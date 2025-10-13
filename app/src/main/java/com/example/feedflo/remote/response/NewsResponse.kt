package com.example.feedflow.remote.response

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)