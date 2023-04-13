package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.domain.interfaces.IPostsRepository
import kotlinx.coroutines.flow.Flow

class SetFavoriteUseCase(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(postId: String, isFavorite: Boolean): Flow<Boolean> {
        return postsRepository.setFavorite(postId, isFavorite)
    }
}