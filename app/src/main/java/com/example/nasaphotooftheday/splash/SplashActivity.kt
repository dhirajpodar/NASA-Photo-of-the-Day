package com.example.nasaphotooftheday.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.base.BaseActivity
import com.example.extension.toJsonString
import com.example.nasaphotooftheday.BR
import com.example.nasaphotooftheday.R
import com.example.nasaphotooftheday.databinding.ActivitySplashBinding
import com.example.nasaphotooftheday.main.MainActivity
import com.example.nasaphotooftheday.model.Response
import kotlinx.android.synthetic.main.activity_splash.rl_progressBar


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

  lateinit var splashViewModel: SplashViewModel
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    initViewModel()
    initObserver()
  }


  private fun initViewModel() {
    splashViewModel = getViewModel()
    splashViewModel.photoOfTheDay(apiKey())

  }

  private fun initObserver() {
    getViewModel().responseData.observe(
      this,
      Observer { openSplashActivity(it) })
    getViewModel().errorMessage.observe(this, Observer { showToast(it) })
    getViewModel().isLoading.observe(this, Observer {
      rl_progressBar.visibility = if (it) View.VISIBLE else View.GONE
    })
  }


  private fun openSplashActivity(responseData: Response) {
    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra("data",responseData.toJsonString())
    startActivity(intent)
    finish()
  }

  private fun apiKey(): String {
    return resources.getString(R.string.API_KEY)
  }

  override fun getLayout(): Int = R.layout.activity_splash

  override fun getContext(): Context = this

  override fun getViewModel(): SplashViewModel =
    ViewModelProviders.of(this).get(SplashViewModel::class.java)

  override fun getBindingVariable(): Int = BR.viewModel
}
