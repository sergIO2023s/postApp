package com.example.postapp.postapp.utils

import android.provider.Contacts.Intents.UI

sealed class UIResponse<T>(
    val type: TYPEDATA,
    val data: T? = null,
    val message: String? = null
) {
    data class Success<T>(val dataObject: T) : UIResponse<T>(TYPEDATA.SUCCESS, dataObject, null)
    data class Error<T>(val msg: String) : UIResponse<T>(TYPEDATA.ERROR, null, msg)
    class Loading<T>() : UIResponse<T>(TYPEDATA.LOADING, null, null)
}

fun <T> UIResponse.Error<T>.asResponse(): UIResponse<T>  { return this as UIResponse<T> }

fun <T> UIResponse.Success<T>.asResponse(): UIResponse<T>  { return this as UIResponse<T> }

enum class TYPEDATA {
    ERROR,SUCCESS,LOADING
}