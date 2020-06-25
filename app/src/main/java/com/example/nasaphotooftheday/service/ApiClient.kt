package com.example.nasaphotooftheday.service

import io.reactivex.android.plugins.RxAndroidPlugins
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
  companion object{
    private val BASE_URL: String = "https://api.nasa.gov/planetary/"
    private var retrofit: Retrofit? = null
    fun getInstance(): Retrofit{
      retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
      return retrofit as Retrofit
    }
  }
}
