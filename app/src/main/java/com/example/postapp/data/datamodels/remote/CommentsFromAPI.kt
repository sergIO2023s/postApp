package com.example.postapp.data.datamodels.remote

import com.google.gson.annotations.SerializedName

data class CommentsFromAPI(
    val id: String?,
    val name: String?,
    val body: String?,
    @SerializedName("postId")
    val postId: String?
)