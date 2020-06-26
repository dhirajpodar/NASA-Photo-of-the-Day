package com.example.nasaphotooftheday.main

import android.app.ActivityOptions
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.base.BaseActivity
import com.example.extension.toJsonObj
import com.example.extension.toUri
import com.example.nasaphotooftheday.AppConstant
import com.example.nasaphotooftheday.BR
import com.example.nasaphotooftheday.R
import com.example.nasaphotooftheday.databinding.ActivityMainBinding
import com.example.nasaphotooftheday.model.Response
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI
import java.net.URL
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var mainViewModel: MainViewModel
    private var date = MutableLiveData<String>()
    val compositeDisposable = CompositeDisposable()
    private var url: String? = null
    private var isImage = false
    private var imageUri: Uri? = null

    override fun getLayout(): Int = R.layout.activity_main

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getContext(): Context = this

    override fun getViewModel(): MainViewModel = ViewModelProviders.of(this).get(
        MainViewModel::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initIntent()
        initViewModel()
        initView()
        initObserver()
    }

    private fun initIntent() {
        var responseInString = intent.getStringExtra("data")!!
        val response = responseInString.toJsonObj(Response::class.java)
        updateUI(response)
    }

    private fun initViewModel() {
        mainViewModel = getViewModel()
    }

    private fun initView() {
        iv_calender.setOnClickListener {
            imageUri?.let { showDatePickerDialog() }
        }
        iv_icon.setOnClickListener {
            openFullScreenActivity()
        }
    }


    private fun openFullScreenActivity() {
        val intent = Intent(this, FullScreenActivity::class.java)
        if (isImage) {
            intent.putExtra("media_type", true)
            intent.setData(imageUri)
        } else {
            intent.putExtra("media_type", false)
            intent.putExtra("url", url)
            intent.setData(Uri.parse(url))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(
                    intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
            }
        } else {
            startActivity(intent)
        }
    }

    private fun initObserver() {
        mainViewModel.responseData.observe(this, Observer { updateUI(it) })
        mainViewModel.errorMessage.observe(this, Observer { showToast(it) })
        mainViewModel.isLoading.observe(this, Observer {
            rl_progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        date.observe(this, Observer {
            loadGif()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                rl_main.background = null
            }
            mainViewModel.photoByDate(it)
        })
    }

    private fun updateUI(response: Response) {
        tv_title.setText(response.title)
        tv_description.setText(response.explanation)
        url = response.url
        response.media_type?.let {
            if (it.equals("image")) {
                isImage = true
                loadGif()
                loadImage()
                iv_icon.setImageDrawable(resources.getDrawable(R.drawable.ic_zoom_24dp))
            } else {
                isImage = false
                iv_icon.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_black_24dp))
            }
        }
    }


    private fun loadGif() {
        Glide.with(this).load(R.drawable.loading).into(iv_image)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePicker = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val m = month + 1
                var dayOfMonthInString = dayOfMonth.toString()
                if (dayOfMonth < 10) dayOfMonthInString = "0" + "$dayOfMonth"
                val monthInString: String

                monthInString = if (m < 10) "0" + "$m" else "$m"

                date.value = "$year-$monthInString-$dayOfMonthInString"


            }, year, month, day
        )
        datePicker.datePicker.maxDate = calendar.timeInMillis
        datePicker.show()

    }

    private fun loadImage() {
        compositeDisposable.add(
            Observable.create(ObservableOnSubscribe<Bitmap> { emitter ->
                val url = URL(this.url)
                val bitmap: Bitmap? =
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                if (bitmap != null) {
                    emitter.onNext(bitmap)
                } else {
                    emitter.onError(Throwable())
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loadBitmapImage(it)
                }, {
                    showDialogBox()
                    it.printStackTrace()
                })
        )
    }

    private fun loadBitmapImage(bitmap: Bitmap?) {
        imageUri = bitmap?.toUri(this)
        iv_image.setImageBitmap(bitmap)
        val drawable = BitmapDrawable(resources, bitmap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rl_main.background = drawable
            rl_main.background.alpha = 127
        }
    }

    private fun showDialogBox() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Could not download image. Do you want to try again?")
            .setPositiveButton(
                "Yes"
            ) { dialog, which ->
                dialog.dismiss()
                loadImage()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .setCancelable(false)
            .create()
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        mainViewModel.onClear()
    }


}
