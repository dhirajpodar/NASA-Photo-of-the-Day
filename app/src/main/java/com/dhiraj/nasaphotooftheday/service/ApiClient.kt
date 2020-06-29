package com.dhiraj.nasaphotooftheday.service

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val BASE_URL: String = "https://api.nasa.gov/planetary/"
    private var retrofit: Retrofit? = null
    private fun getInstance(): Retrofit {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return retrofit as Retrofit
    }

    fun getAPiService(): ApiService {
        return getInstance().create(ApiService::class.java)
    }
}
