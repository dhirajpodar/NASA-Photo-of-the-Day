package com.example.nasaphotooftheday.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nasaphotooftheday.utils.AppConstant
import com.example.nasaphotooftheday.utils.HelperClass

import com.example.nasaphotooftheday.R
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener {
    private var videoId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)
        initIntent()
    }

    private fun initIntent() {
        intent?.let {
            val isImage = it.getBooleanExtra("media_type", false)
            val uri = it.data
            if (isImage) {
                iv_fullImage.visibility = View.VISIBLE
                view_fragment.visibility = View.GONE
                uri?.let { iv_fullImage.setImageURI(it) }
            } else {
                val url = it.getStringExtra("url")
                videoId = url?.let { HelperClass.getVideoId(it)  }
                videoId?.let { loadVideo() }

            }
        }
    }

    private fun loadVideo() {
        iv_fullImage.visibility = View.GONE
        view_fragment.visibility = View.VISIBLE
        val youtubePlayerFragment =
            supportFragmentManager.findFragmentById(R.id.yt_fragment) as YouTubePlayerSupportFragment
        youtubePlayerFragment.initialize(AppConstant.YOUTUBE_API_KEY, this)
    }

    override fun onInitializationSuccess(
        p0: YouTubePlayer.Provider?,
        p1: YouTubePlayer?,
        p2: Boolean
    ) {
        Log.d("TAG", "Success initlizaiton")
//        p1!!.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
        p1?.let {
            it.setShowFullscreenButton(false)
            if (!p2) {
                videoId?.let { p1.loadVideo(it) }
            }


            it.setPlayerStateChangeListener(object :
                YouTubePlayer.PlayerStateChangeListener {
                override fun onAdStarted() {}
                override fun onLoading() {}
                override fun onVideoStarted() {}
                override fun onLoaded(p0: String?) {}
                override fun onError(p0: YouTubePlayer.ErrorReason?) {}
                override fun onVideoEnded() {
                    finish()
                }
            })
        }


    }

    override fun onInitializationFailure(
        p0: YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {}

}
