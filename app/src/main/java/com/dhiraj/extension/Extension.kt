package com.dhiraj.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream

fun Any.toJsonString(): String {
    return Gson().toJson(this)
}

fun <T> String.toJsonObj(mClass: Class<T>): T {
    return Gson().fromJson(this, mClass)
}


fun Bitmap.toUri(context: Context): Uri {
    val file = File(context.getDir("temp", Context.MODE_PRIVATE), "image")
    val fileOutputStream = FileOutputStream(file)
    this.compress(Bitmap.CompressFormat.JPEG, 40, fileOutputStream)
    fileOutputStream.close()
    return Uri.parse(file.path)
}






