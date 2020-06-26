package com.example.extension

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
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
    this.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream)
    fileOutputStream.close()
    return Uri.parse(file.path)
}

