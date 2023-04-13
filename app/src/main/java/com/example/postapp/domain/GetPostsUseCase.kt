package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.domain.interfaces.IPostsRepository
import com.example.postapp.domain.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GetPostsUseCase(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(): Flow<Response<List<Post>>> {
        return postsRepository.getPosts()
    }
}