package com.dhiraj.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel


abstract class BaseActivity<T:ViewDataBinding,V : ViewModel> :AppCompatActivity() {

  private lateinit var viewDataBinding: ViewDataBinding
  private var viewModel: V? = null

  abstract fun getLayout(): Int

  abstract fun getContext(): Context

  abstract fun getViewModel(): V

  abstract fun getBindingVariable() : Int

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initViewModel()
    performDataBinding()
  }

  private fun performDataBinding() {
    viewDataBinding = DataBindingUtil.setContentView(this,getLayout())
  }

  private fun initViewModel() {
    this.viewModel = if(viewModel == null) getViewModel() else this.viewModel

  }

  fun showToast(message: String){
    Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show()
  }

  fun <U> openActivity(mClass: Class<U>){
    startActivity(Intent(getContext(),mClass))
  }
}
