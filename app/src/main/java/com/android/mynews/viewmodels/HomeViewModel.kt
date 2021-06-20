package com.android.mynews.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.mynews.models.Article
import com.android.mynews.models.NewsBody
import com.android.mynews.repository.NetworkRepository
import com.android.mynews.repository.SharedPreferenceRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(application: Application) :
    AndroidViewModel(application) {
    var networkRepository = NetworkRepository()
    var preference = SharedPreferenceRepository(application)
    var articleList = ArrayList<Article>()
    var originalList = ArrayList<Article>()
    var requestStatus = MutableLiveData<String?>()
    var selectedCountry=0

    fun getNews() {
        var country=""
        country = if(selectedCountry==0)// determined which country news will be received
            "us"
        else
            "eg"
        val disposable = networkRepository.getNews(country)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ //When data received
                    articleList=(it.articles as ArrayList<Article>)
                    originalList=(it.articles as ArrayList<Article>)
                    requestStatus.value=it.status
                }, { // server or unknown Error
                    requestStatus.value="Failed"
                }
            )
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime():String{
        val sdf = SimpleDateFormat("dd/M hh:mm a")
        return sdf.format(Date())
    }

    fun isNetworkAvailable(context: Context): Boolean { // Return if there internet connection or not
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = conManager.activeNetwork
        return activeNetworkInfo != null
    }
}