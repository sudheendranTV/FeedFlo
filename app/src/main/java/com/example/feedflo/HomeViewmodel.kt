package com.example.feedflo

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedflow.remote.response.NewsResponse
import com.example.feedflow.repositories.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewmodel @Inject constructor(private val newsRepository: NewsRepository) : ViewModel() {

    var _newsResponse = mutableStateOf<NewsResponse?>(null)

    init {
        viewModelScope.launch {
            getNews()
        }
    }

    fun getNews() {
        viewModelScope.launch (Dispatchers.IO) {
            try {
                val news = newsRepository.getNews(1)
                _newsResponse.value = news
            } catch (e: Exception) {
                  Log.e("HomeViewmodel", "getNews: ${e.message}")
            }

        }

    }
}