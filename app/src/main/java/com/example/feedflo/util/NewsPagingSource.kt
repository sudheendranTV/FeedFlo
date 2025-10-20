package com.example.feedflo.util

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.feedflow.remote.NewsApiService
import com.example.feedflow.remote.response.Article
import javax.inject.Inject

class NewsPagingSource @Inject constructor(
    private val api: NewsApiService
) : PagingSource<Int, Article>() {
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 1
            val response = api.getNews(page = page)
            
            LoadResult.Page(
                data = response.articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }
}
