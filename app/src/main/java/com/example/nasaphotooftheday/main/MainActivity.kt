package com.example.nasaphotooftheday.main

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.base.BaseActivity
import com.example.extension.toJsonObj
import com.example.nasaphotooftheday.BR
import com.example.nasaphotooftheday.R
import com.example.nasaphotooftheday.databinding.ActivityMainBinding
import com.example.nasaphotooftheday.model.Response
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

  private lateinit var mainViewModel: MainViewModel
  private var date = MutableLiveData<String>()
  val compositeDisposable = CompositeDisposable()
  private var url: String? = null

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
    var responseInString = intent.getStringExtra("data")
    val response = responseInString.toJsonObj(Response::class.java)
    updateUI(response)
  }

  private fun initViewModel() {
    mainViewModel = getViewModel()
  }

  private fun initView() {
    iv_calender.setOnClickListener { showDatePickerDialog() }
  }

  private fun initObserver() {
    getViewModel().responseData.observe(this, Observer { updateUI(it) })
    getViewModel().errorMessage.observe(this, Observer { showToast(it) })
    getViewModel().isLoading.observe(this, Observer {
      rl_progressBar.visibility = if (it) View.VISIBLE else View.GONE
    })

    date.observe(this, Observer {
      iv_image.setImageBitmap(null)
      mainViewModel.photoByDate(apiKey(), it)
    })
  }

  private fun updateUI(response: Response) {
    tv_title.setText(response.title)
    tv_description.setText(response.explanation)
    url = response.url
    loadImage()

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
        val bitmap : Bitmap? = BitmapFactory.decodeStream(url.openConnection().getInputStream())
        if(bitmap != null){
          emitter.onNext(bitmap)
        }else{
          emitter.onError(Throwable())
        }
      }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          iv_image.visibility = View.VISIBLE
          iv_image.setImageBitmap(it)
        }, {
          showDialogBox()
          it.printStackTrace()
        })
    )
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

  private fun apiKey(): String {
    return resources.getString(R.string.API_KEY)
  }


  override fun onDestroy() {
    super.onDestroy()
    compositeDisposable.clear()
    mainViewModel.onClear()
  }


}
