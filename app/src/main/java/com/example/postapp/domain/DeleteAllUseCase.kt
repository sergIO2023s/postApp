package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.domain.interfaces.IPostsRepository
import kotlinx.coroutines.flow.Flow

class DeleteAllUseCase(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(): Flow<Boolean> {
        return postsRepository.deleteAllPosts()
    }
}