package com.example.postapp.data.remote

import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.remote.CommentsFromAPI
import com.example.postapp.data.datamodels.remote.PostFromAPI
import com.example.postapp.data.datamodels.remote.UserFromAPI
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonPlaceholderApiClient {
    @GET("posts")
    suspend fun getPosts(): List<PostFromAPI>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserFromAPI

    @GET("posts/{id}/comments")
    suspend fun getCommentsByPostId(@Path("id") id: String): List<CommentsFromAPI>
}