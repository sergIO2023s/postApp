package com.example.postapp.data.datamodels.remote

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class PostFromAPI(
    val id: String?,
    @SerializedName("userId")
    val userId: String?,
    val title: String?,
    val body: String?,
    val isFavorite: Boolean?
)