package com.example.nasaphotooftheday.utils

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
        linkWithoutDomain(url)?.let {
            for (regex in videoIdRegex) {
                val compiledPattern = Pattern.compile(regex)
                val matcher: Matcher = compiledPattern.matcher(it)
                if (matcher.find()) {
                    return matcher.group(1)
                }
            }
        }
        return null

    }
    private fun linkWithoutDomain(url: String): String{
        val youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((youtube.com)|(youtu.be))/";
        val compiledPattern = Pattern.compile(youTubeUrlRegEx)
        val matcher = compiledPattern.matcher(url)
        return if (matcher.find()) {
            url.replace(matcher.group(), "")
        } else url
    }
}