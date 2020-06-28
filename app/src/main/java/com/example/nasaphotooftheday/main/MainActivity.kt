package com.example.nasaphotooftheday.main

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
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.base.BaseActivity
import com.example.extension.toJsonObj
import com.example.extension.toUri
import com.example.nasaphotooftheday.utils.AppConstant
import com.example.nasaphotooftheday.BR
import com.example.nasaphotooftheday.utils.HelperClass
import com.example.nasaphotooftheday.R
import com.example.nasaphotooftheday.databinding.ActivityMainBinding
import com.example.nasaphotooftheday.model.Response
import com.example.nasaphotooftheday.splash.SplashActivity
import com.example.nasaphotooftheday.utils.MediaType
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var mainViewModel: MainViewModel
    private var date = MutableLiveData<String>()
    private val compositeDisposable = CompositeDisposable()
    private var url: String? = null
    private var isImage = false
    private var containsImage = false
    private var imageUri: Uri? = null

    override fun getLayout(): Int = R.layout.activity_main

    override fun getBindingVariable(): Int = BR.viewModel

    override fun getContext(): Context = this

    override fun getViewModel(): MainViewModel = ViewModelProvider(this).get(
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
        val responseInString = intent.getStringExtra("data")!!
        val response = responseInString.toJsonObj(Response::class.java)
        updateUI(response)
    }

    private fun initViewModel() {
        mainViewModel = getViewModel()
    }

    private fun initView() {
        iv_calender.setOnClickListener {
            if (containsImage) showDatePickerDialog()
        }
        iv_icon.setOnClickListener {
            if (containsImage) openFullScreenActivity()
        }
    }


    private fun openFullScreenActivity() {
        val intent = Intent(this, FullScreenActivity::class.java)
        if (isImage) {
            intent.putExtra("media_type", true)
            intent.data = imageUri
        } else {
            intent.putExtra("media_type", false)
            intent.putExtra("url", url)
            intent.data = Uri.parse(url)
        }
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            iv_image,
            ViewCompat.getTransitionName(iv_image)!!
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(intent, options.toBundle())
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
            containsImage = false
            mainViewModel.photoByDate(it)
        })
    }

    private fun updateUI(response: Response) {
        tv_title.text = response.title
        tv_description.text = response.explanation
        loadGif()
        response.media_type?.let {
            if (it == MediaType.IMAGE.value) {
                url = response.hdurl
                updateUIForImage()
            } else if (it == MediaType.VIDEO.value) {
                url = response.url
                updateUIForVideo(url!!)

            }
        }
    }

    private fun updateUIForVideo(url: String) {
        isImage = false
        val videoId = HelperClass.getVideoId(url)
        val thumbnailUrl =
            AppConstant.IMG_URL_PREFEX + videoId + AppConstant.IMG_URL_SUFFEX
        loadImage(thumbnailUrl)
        iv_icon.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_play_arrow_black_24dp
            )
        )
    }

    private fun updateUIForImage() {
        isImage = true
        loadImage(url!!)
        iv_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_zoom_24dp))
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
            R.style.DialogTheme,
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

    private fun loadImage(urlInString: String) {
        compositeDisposable.add(
            Observable.create(ObservableOnSubscribe<Bitmap> { emitter ->
                val url = URL(urlInString)
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
        imageUri = if (isImage) bitmap?.toUri(this) else null
        iv_image.setImageBitmap(bitmap)
        val drawable = BitmapDrawable(resources, bitmap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rl_main.background = drawable
            rl_main.background.alpha = 127
        }
        containsImage = true
    }

    private fun showDialogBox() = AlertDialog.Builder(this)
        .setTitle(getString(R.string.title_error))
        .setMessage(getString(R.string.msg_dialog))
        .setPositiveButton(
            getString(R.string.title_yes)
        ) { dialog, which ->
            dialog.dismiss()
            loadImage(url!!)
        }
        .setNegativeButton(getString(R.string.title_no)) { dialog, _ -> dialog.dismiss() }
        .setCancelable(false)
        .create()
        .show()


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        mainViewModel.onClear()
    }


}
