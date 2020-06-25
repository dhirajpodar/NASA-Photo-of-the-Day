package com.example.nasaphotooftheday.service

import com.example.nasaphotooftheday.model.Response
import io.reactivex.Observable


class Repository(val apiKey: String) {
  private var retrofit = ApiClient.getInstance()
  private var apiService = retrofit.create(ApiService::class.java)

  fun getObservable(): Observable<Response>{
    return  apiService.photoOfTheDay(apiKey)
  }

  fun getObservable(date: String): Observable<Response>{
    return  apiService.photoByDate(apiKey,date)
  }
}
