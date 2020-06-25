package com.example.nasaphotooftheday.main

import androidx.lifecycle.MutableLiveData
import com.example.base.BaseViewModel
import com.example.nasaphotooftheday.model.Response
import com.example.nasaphotooftheday.service.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel : BaseViewModel() {

  val responseData by lazy { MutableLiveData<Response>() }
  val errorMessage by lazy { MutableLiveData<String>() }

  fun photoByDate(apiKey: String, date: String) {
    isLoading.value = true
    val observable: Observable<Response> = initRetrofit(apiKey, date)
    val disposable = observable
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ response ->
        if (response != null) {
          isLoading.value = false
          errorMessage.value = "Success"
          responseData.value = response
        }
      }, {
        isLoading.value = false
        errorMessage.value = it.localizedMessage
      })
    compositeDisposable.add(disposable)
  }

  fun initRetrofit(apiKey: String,date: String) :Observable<Response>{
    return Repository(apiKey).getObservable(date)
  }
}

