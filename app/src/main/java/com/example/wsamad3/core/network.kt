package com.example.wsamad3.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun networkInfo(context: Context):Boolean{
    val conn = context.getSystemService(ConnectivityManager::class.java)
    val network = conn.activeNetwork ?: return false
    val capabilities = conn.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}