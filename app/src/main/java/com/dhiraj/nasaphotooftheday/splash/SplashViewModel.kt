package com.dhiraj.nasaphotooftheday.splash

import androidx.lifecycle.MutableLiveData
import com.dhiraj.base.BaseViewModel
import com.dhiraj.nasaphotooftheday.model.Response
import com.dhiraj.nasaphotooftheday.service.Repository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SplashViewModel: BaseViewModel() {
  val responseData by lazy { MutableLiveData<Response>() }
  val errorMessage by lazy { MutableLiveData<String>() }

  fun photoOfTheDay(apiKey: String) {
    isLoading.value = true
    val observable: Observable<Response> = initRetrofit(apiKey)
    val disposable = observable
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({ response ->
        if (response != null) {
          isLoading.value = false
          responseData.value = response
        }
      }, {
        isLoading.value = false
        errorMessage.value = it.localizedMessage
      })
    compositeDisposable.add(disposable)
  }

  fun initRetrofit(apiKey: String) : Observable<Response> {
    return Repository().getObservable()
  }
}
