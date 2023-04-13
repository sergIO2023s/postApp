package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User
import com.example.postapp.domain.interfaces.IPostsRepository
import com.example.postapp.domain.utils.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class GetPostDetailsUseCase(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(userId: String): Flow<Response<User>> {
         return postsRepository.getUserById(userId)
    }


    /*suspend fun execute(postId: String): Flow<Pair<Post, User>> {
         return postsRepository.getPostById(postId)
            .flatMapMerge { post ->
                postsRepository.getUserById(post.data!!.userId)
                    .map { user ->
                        post.data!! to user.data!!
                    }
            }
    }*/

}