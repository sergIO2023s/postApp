package com.example.postapp.domain.interfaces

import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User
import com.example.postapp.domain.utils.Response
import kotlinx.coroutines.flow.Flow

interface IPostsRepository {

    suspend fun getPosts(): Flow<Response<List<Post>>>

    suspend fun getPostById(id: String): Flow<Response<Post>>

    suspend fun getUserById(userId: String): Flow<Response<User>>

    suspend fun getCommentsByPostId(postId: String): Flow<Response<List<Comment>>>

    suspend fun setFavorite(postId: String, isFavorite: Boolean): Flow<Boolean>

    suspend fun deleteAllPosts(): Flow<Boolean>

    suspend fun deletePostById(idPost: String): Flow<Boolean>

    suspend fun fetchRemotePosts(): Flow<Response<Unit>>
}