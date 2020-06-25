package com.example.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
  val compositeDisposable = CompositeDisposable()

  val isLoading: MutableLiveData<Boolean> = MutableLiveData()

  val message: MutableLiveData<Any> = MutableLiveData()

  fun getIsLoading() : MutableLiveData<Boolean> = isLoading


  fun onClear(){
   super.onCleared()
   this.compositeDisposable.clear()
 }
}
