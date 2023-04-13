package com.example.postapp.domain.utils

sealed class Response<T>(
    val type: TYPEDATA,
    val data: T? = null,
    val errorMessage: String? = null
) {
    data class Success<T>(val dataObject: T) : Response<T>(TYPEDATA.SUCCESS, dataObject, null)
    data class Error<T>(val msg: String) : Response<T>(TYPEDATA.ERROR, null, msg)
}

fun <T> Response.Error<T>.asResponse(): Response<T>  { return this as Response<T> }

fun <T> Response.Success<T>.asResponse(): Response<T>  { return this as Response<T> }

enum class TYPEDATA {
    ERROR,SUCCESS,LOADING
}