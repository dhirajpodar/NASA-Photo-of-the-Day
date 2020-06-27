package com.example.nasaphotooftheday.splash

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.base.BaseActivity
import com.example.extension.toJsonString
import com.example.nasaphotooftheday.AppConstant
import com.example.nasaphotooftheday.BR
import com.example.nasaphotooftheday.R
import com.example.nasaphotooftheday.databinding.ActivitySplashBinding
import com.example.nasaphotooftheday.main.MainActivity
import com.example.nasaphotooftheday.model.Response
import kotlinx.android.synthetic.main.activity_splash.*
import java.lang.Thread.sleep


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initObserver()
        //initAnimation()
    }

    private fun initAnimation() {
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation)

        iv_logo.animation = topAnimation
        tv_heading.animation = bottomAnimation
    }


    private fun initViewModel() {
        splashViewModel = getViewModel()
        splashViewModel.photoOfTheDay(AppConstant.API_KEY)

    }

    private fun initObserver() {
        getViewModel().responseData.observe(
            this,
            Observer {
                delayBy(2000)
                openMainActivity(it)
            })
        getViewModel().errorMessage.observe(this, Observer { showToast(it) })
        /*getViewModel().isLoading.observe(this, Observer {
            rl_progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })*/
    }

    private fun delayBy(i: Long) {
        object :Runnable{
            override fun run() {
                Handler().postDelayed(this,i)
            }
        }
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
        ViewModelProviders.of(this).get(SplashViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel
}
