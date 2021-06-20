package com.android.mynews.repository

import com.android.mynews.models.NewsBody
import com.android.mynews.repository.Utils.API_KEY
import com.android.mynews.repository.services.ClientService
import io.reactivex.Observable

class NetworkRepository () {
    var clientService = ClientService.getClient()

    fun getNews(country:String): Observable<NewsBody> {
        return clientService.getNews(API_KEY, country)
    }
}