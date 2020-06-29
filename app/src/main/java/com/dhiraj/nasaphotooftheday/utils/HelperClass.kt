package com.dhiraj.nasaphotooftheday.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import java.io.InputStream
import java.net.URL
import java.util.regex.Matcher
import java.util.regex.Pattern


object HelperClass {
    fun getVideoId(url: String): String? {
        val videoIdRegex = arrayOf(
            "\\?vi?=([^&]*)",
            "watch\\?.*v=([^&]*)",
            "(?:embed|vi?)/([^/?]*)",
            "^([A-Za-z0-9\\-]*)"
        )
        for (regex in videoIdRegex) {
            val compiledPattern = Pattern.compile(regex)
            val matcher: Matcher = compiledPattern.matcher(linkWithoutDomain(url))
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return null

    }

    private fun linkWithoutDomain(url: String): String {
        val youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        val compiledPattern = Pattern.compile(youTubeUrlRegEx)
        val matcher = compiledPattern.matcher(url)
        return if (matcher.find()) {
            url.replace(matcher.group(), "")
        } else url
    }

    fun getBitmap(context: Context, url: String): Bitmap? {

        val website: URL
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        val height: Int = displayMetrics.heightPixels / 2
        val width: Int = displayMetrics.widthPixels / 2

        return try {
            website = URL(url)
            var inputStream: InputStream = website.openConnection().inputStream

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()
            inputStream = website.openConnection().inputStream
            options.inSampleSize = calculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: Exception) {
            null
        }
    }


    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}