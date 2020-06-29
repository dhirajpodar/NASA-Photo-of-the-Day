package com.dhiraj.nasaphotooftheday.service

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import com.dhiraj.nasaphotooftheday.model.Response as Response

interface ApiService {
  /* https://api.nasa.gov/planetary/apod?api_key=sBioDQXl66F5S2Dh0vCeZvwJhy9mzahtcA0t0qqx */
  /* https://api.nasa.gov/planetary/apod?api_key=sBioDQXl66F5S2Dh0vCeZvwJhy9mzahtcA0t0qqx&date=2020-02-12 */

  @GET("apod")
  fun photoOfTheDay(@Query("api_key") key: String) : Observable<Response>

  @GET("apod")
  fun photoByDate(@Query("api_key") key: String, @Query("date") date: String) : Observable<Response>
}
