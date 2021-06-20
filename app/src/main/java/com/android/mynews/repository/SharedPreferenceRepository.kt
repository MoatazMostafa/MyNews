package com.android.mynews.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.android.mynews.models.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferenceRepository(application: Application) {

    var preference: SharedPreferences = application.getSharedPreferences("", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveArticleList(processList: ArrayList<Article>?) {
        preference.edit().putString("ArticleList", gson.toJson(processList)).apply()
    }

    fun getArticleList():  ArrayList<Article>?{
        val jsonPreferences = preference.getString("ArticleList", null) ?: return null
        val type = object : TypeToken< List<Article>?>() {}.type
        return gson.fromJson<ArrayList<Article>?>(jsonPreferences, type)
    }

    fun saveLastSync(lastSync: String) {
        preference.edit().putString("LastSync", gson.toJson(lastSync)).apply()
    }

    fun getLastSync(): String {
        val jsonPreferences = preference.getString("LastSync", null) ?: return ""
        val type = object : TypeToken<String>() {}.type
        return gson.fromJson(jsonPreferences, type)
    }

    fun saveCountry(country: Int) {
        preference.edit().putString("Country", gson.toJson(country)).apply()
    }

    fun getCountry(): Int {
        val jsonPreferences = preference.getString("Country", null) ?: return 0
        val type = object : TypeToken<Int>() {}.type
        return gson.fromJson(jsonPreferences, type)
    }
}