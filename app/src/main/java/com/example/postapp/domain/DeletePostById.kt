package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.domain.interfaces.IPostsRepository
import kotlinx.coroutines.flow.Flow

class DeletePostById(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(idPost: String): Flow<Boolean> {
        return postsRepository.deletePostById(idPost)
    }
}