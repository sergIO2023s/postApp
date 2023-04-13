package com.example.postapp.domain

import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.domain.interfaces.IError
import com.example.postapp.domain.interfaces.IPostsRepository
import com.example.postapp.domain.interfaces.IResponse
import com.example.postapp.domain.interfaces.ISuccess
import com.example.postapp.domain.utils.Response
import com.example.postapp.domain.utils.TYPEDATA
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class GetLatestPostUseCase(
    private val postsRepository: IPostsRepository
) {
    suspend fun execute(): Flow<Response<Unit>> {
        return postsRepository.fetchRemotePosts()
    }
}