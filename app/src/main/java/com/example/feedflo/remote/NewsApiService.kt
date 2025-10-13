package com.example.feedflow.remote

import com.example.feedflow.remote.response.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    suspend fun getNews(
        @Query("domains") domains: String = "techcrunch.com,thenextweb.com",
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("apiKey") apiKey: String = "97f4265ddb374df5980deceb2fde0863"
    ): NewsResponse
}