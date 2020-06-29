package com.dhiraj.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {
    val compositeDisposable = CompositeDisposable()

    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    val message: MutableLiveData<String> = MutableLiveData()

    fun onClear() {
        super.onCleared()
        this.compositeDisposable.clear()
    }
}
