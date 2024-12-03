package com.android.news1application.model.repository

import com.android.news1application.api.RetrofitInstance
import com.android.news1application.db.NewsDatabase
import com.android.news1application.model.Article
import com.android.news1application.model.NewsResponse
import retrofit2.Response

class NewsRepository(private val db: NewsDatabase) {

    suspend fun getHeadlines(countryCode: String, pageNumber: Int): Response<NewsResponse> {
       return RetrofitInstance.api.getHeadlines(countryCode, pageNumber)
    }

    suspend fun searchInstance(searchQuery: String, pageNumber: Int): Response<NewsResponse>  {
        return RetrofitInstance.api.searchNews(searchQuery, pageNumber)
    }

    suspend fun upsert(article: Article) = db.getArticlesDAO().upsert(article)

    fun getFavoriteNews() = db.getArticlesDAO().getAllArticles()

    suspend fun deleteArticles(article: Article) = db.getArticlesDAO().deleteArticle(article)
}