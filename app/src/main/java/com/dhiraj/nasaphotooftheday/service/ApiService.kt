package com.dhiraj.nasaphotooftheday.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import com.dhiraj.nasaphotooftheday.model.Response as Response

interface ApiService {


  @GET("apod")
  fun photoOfTheDay(@Query("api_key") key: String) : Observable<Response>

  @GET("apod")
  fun photoByDate(@Query("api_key") key: String, @Query("date") date: String) : Observable<Response>
}
