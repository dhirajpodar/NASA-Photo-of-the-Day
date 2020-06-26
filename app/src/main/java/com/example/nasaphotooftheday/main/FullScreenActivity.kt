package com.example.nasaphotooftheday.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nasaphotooftheday.AppConstant

import com.example.nasaphotooftheday.R
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import kotlinx.android.synthetic.main.activity_full_screen.*

class FullScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen)

        intent?.let {
            val type = it.getBooleanExtra("media_type", false)
            val uri = it.data
            if (type) {
                iv_fullImage.visibility = View.VISIBLE
                view_fragment.visibility = View.GONE
                uri?.let { iv_fullImage.setImageURI(it) }
            } else {
                val url = it.getStringExtra("url")
                loadVideo(url!!)
            }


        }


    }

    private fun loadVideo(url: String) {
        iv_fullImage.visibility = View.GONE
        view_fragment.visibility = View.VISIBLE
        val youtubePlayerFragment =
            supportFragmentManager.findFragmentById(R.id.yt_fragment) as YouTubePlayerSupportFragment
        youtubePlayerFragment.initialize(
            AppConstant.YOUTUBE_API_KEY,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer?,
                    p2: Boolean
                ) {
                    Log.d("TAG", "Success initlizaiton")
                    if (!p2) {
                        p1?.loadVideo("bhJ0MotUblo")
                    }
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    Log.d("TAG", "Failed initialiaztion")
                }
            })
    }

}
