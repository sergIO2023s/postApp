package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User
import com.example.postapp.domain.interfaces.IPostsRepository
import com.example.postapp.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPostCommentsUseCase(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(postId: String): Flow<Response<List<Comment>>> {
        return postsRepository.getCommentsByPostId(postId)
    }
}