package com.android.mynews.repository.services

import com.android.mynews.models.NewsBody
import com.android.mynews.repository.Utils.BASE_URL
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ClientService {

    companion object {
        fun getClient(): ClientService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(HttpClient.getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(ClientService::class.java)
        }
    }
    @GET("top-headlines")
    fun getNews(
            @Query("apiKey") apiKey:String,
            @Query("country") country:String
    ): Observable<NewsBody>
}