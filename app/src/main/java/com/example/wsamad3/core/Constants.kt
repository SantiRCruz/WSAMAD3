package com.example.wsamad3.core

import okhttp3.OkHttpClient

object Constants {
    const val URL = "https://wsa2021.mad.hakta.pro/api"
    const val USER = "user"
    val okHttp = OkHttpClient.Builder().build()
}