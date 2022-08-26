package com.example.wsamad3.data

import android.hardware.usb.UsbEndpoint
import android.net.Uri
import com.example.wsamad3.core.Constants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*

fun signIn(email: String, password: String): RequestBody {
    val json = JSONObject().apply {
        put("login", email)
        put("password", password)
    }
    return json.toString().toRequestBody("application/json".toMediaType())
}

fun get(endpoint: String): Request {
    return Request.Builder().url("${Constants.URL}/$endpoint").get().build()
}

fun post(endpoint: String, body: RequestBody): Request {
    return Request.Builder().url("${Constants.URL}/$endpoint").post(body).build()
}

fun image(uri: Uri):RequestBody{
    val uuid = UUID.randomUUID().toString()
    return MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file","$uuid.jpg",File(uri.path!!).asRequestBody()).build()
}

fun post2(endpoint: String, body: RequestBody): Request {
    return Request.Builder().url("$endpoint").post(body)
        .addHeader("X-RapidAPI-Key", "2fb4c3de22mshfdab5359f5975a1p186131jsn555916173999")
        .addHeader("X-RapidAPI-Host","cloudlabs-image-object-detection.p.rapidapi.com").build()
}

fun daysSymptom(body: List<Int>): RequestBody {
    return body.toString().toRequestBody("application/json".toMediaType())
}