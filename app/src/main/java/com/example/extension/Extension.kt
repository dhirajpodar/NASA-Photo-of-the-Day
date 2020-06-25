package com.example.extension

import com.example.nasaphotooftheday.model.Response
import com.google.gson.Gson

fun Any.toJsonString(): String{
  return Gson().toJson(this)
}

fun <T> String.toJsonObj(mClass: Class<T>): T{
  return Gson().fromJson(this,mClass)
}
