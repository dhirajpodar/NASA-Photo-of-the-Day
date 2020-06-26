package com.example.nasaphotooftheday.service

import com.example.nasaphotooftheday.AppConstant
import com.example.nasaphotooftheday.model.Response
import io.reactivex.Observable


class Repository {

    private var apiService = ApiClient.getAPiService()

    fun getObservable(): Observable<Response> {
        return apiService.photoOfTheDay(AppConstant.API_KEY)
    }

    fun getObservable(date: String): Observable<Response> {
        return apiService.photoByDate(AppConstant.API_KEY, date)
    }
}
