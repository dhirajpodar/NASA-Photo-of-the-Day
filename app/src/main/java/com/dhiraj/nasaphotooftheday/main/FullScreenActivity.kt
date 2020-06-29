package com.dhiraj.nasaphotooftheday.main


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dhiraj.nasaphotooftheday.R
import com.dhiraj.nasaphotooftheday.utils.AppConstant
import com.dhiraj.nasaphotooftheday.utils.HelperClass
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

            if (isImage) {
                showImageView()
                val uri = it.data

                uri?.let {
                    iv_fullImage.setImageURI(it)
                }

            } else {
                val url = it.getStringExtra("url")
                videoId = url?.let { HelperClass.getVideoId(it) }
                videoId?.let { loadVideo() }

            }
        }
    }

    private fun showImageView() {
        iv_fullImage.visibility = View.VISIBLE
        view_fragment.visibility = View.GONE
    }

    private fun hideImageView() {
        iv_fullImage.visibility = View.GONE
        view_fragment.visibility = View.VISIBLE
    }

    private fun loadVideo() {
        hideImageView()
        val youtubePlayerFragment =
            supportFragmentManager.findFragmentById(R.id.yt_fragment) as YouTubePlayerSupportFragment
        youtubePlayerFragment.initialize(AppConstant.YOUTUBE_API_KEY, this)
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer?,
        p2: Boolean
    ) {
        youTubePlayer?.let {
            it.setShowFullscreenButton(false)
            if (!p2) {
                videoId?.let { youTubePlayer.loadVideo(it) }
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
    ) {
    }

}
