package com.example.feedflow.repositories

import com.example.feedflow.remote.NewsApiService
import javax.inject.Inject

class NewsRepository @Inject constructor(var apiService: NewsApiService) {


   suspend fun getNews(page: Int) = apiService.getNews(page = 1, pageSize = 10)

}