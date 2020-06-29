package com.dhiraj.nasaphotooftheday.splash

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dhiraj.base.BaseActivity
import com.dhiraj.extension.toJsonString
import com.dhiraj.nasaphotooftheday.utils.AppConstant
import com.dhiraj.nasaphotooftheday.BR
import com.dhiraj.nasaphotooftheday.R
import com.dhiraj.nasaphotooftheday.databinding.ActivitySplashBinding
import com.dhiraj.nasaphotooftheday.main.MainActivity
import com.dhiraj.nasaphotooftheday.model.Response


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    private lateinit var splashViewModel: SplashViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initObserver()
    }


    private fun initViewModel() {
        splashViewModel = getViewModel()
        splashViewModel.photoOfTheDay(AppConstant.API_KEY)

    }

    private fun initObserver() {
        getViewModel().responseData.observe(
            this,
            Observer {
                openMainActivity(it)
            })
        getViewModel().errorMessage.observe(this, Observer { showToast(it) })

    }


    private fun openMainActivity(responseData: Response) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("data", responseData.toJsonString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
        } else {
            startActivity(intent)
        }
        finish()
    }


    override fun getLayout(): Int = R.layout.activity_splash

    override fun getContext(): Context = this

    override fun getViewModel(): SplashViewModel =
        ViewModelProvider(this).get(SplashViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel
}
