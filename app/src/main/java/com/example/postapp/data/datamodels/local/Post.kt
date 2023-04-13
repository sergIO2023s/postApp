package com.example.postapp.data.datamodels.local

import androidx.activity.result.contract.ActivityResultContracts
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.postapp.data.datamodels.remote.CommentsFromAPI
import com.google.gson.annotations.SerializedName


@Entity(tableName = "posts", indices = arrayOf(
    Index(value = ["id"],
    unique = true)
))
data class Post(
    @PrimaryKey
    val id: String,
    @ColumnInfo
    @SerializedName("userId")
    val userId: String,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val body: String,
    @ColumnInfo
    var isFavorite: Boolean
)