package com.example.postapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.domain.*
import com.example.postapp.domain.utils.Response
import com.example.postapp.postapp.utils.UIResponse
import com.example.postapp.postapp.utils.asResponse
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class UIListSectionState(
    val postsList: List<Post>,
)

data class UIState(
    val isShowingError: Boolean,
    val errorMessage: String,
    val isLoading: Boolean
)

class PostListViewModel(
   private val getPostsUseCase: GetPostsUseCase,
   private val setFavoriteUseCase: SetFavoriteUseCase,
   private val deleteAllUseCase: DeleteAllUseCase,
   private val getLatestPostUseCase: GetLatestPostUseCase,
   private val deletePostByIdUseCase: DeletePostById
): ViewModel() {

    val liveDataListSectionState = MutableLiveData<UIListSectionState>()
    val liveDataUiState = MutableLiveData<UIState>()
    var currentListSectionState: UIListSectionState = UIListSectionState(
        postsList = emptyList()
    )
    var currentState: UIState = UIState(
        isShowingError = false,
        errorMessage = "",
        isLoading = false
    )


    fun getPosts() {
        viewModelScope.launch {
            getPostsUseCase
                .execute()
                .map { resultPostList ->
                    if (resultPostList !is Response.Error) {
                        UIResponse.Success(resultPostList.data!!).asResponse()
                    } else {
                        UIResponse.Error(resultPostList.msg)
                    }
                }.onStart { emit(UIResponse.Loading()) }
                .catch { e -> emit(UIResponse.Error(e.message?: "UNKNOWN")) }
                .collect { resultPostList ->
                    when (resultPostList) {
                        is UIResponse.Loading<*> -> {
                            liveDataUiState.postValue(
                                currentState.copy(
                                    isLoading = true,
                                    errorMessage = "yy"
                                )
                            )
                        }
                        is UIResponse.Success<*> -> {
                            liveDataListSectionState.postValue(
                                currentListSectionState.copy(
                                    postsList = resultPostList.data!!,
                                )
                            )
                            liveDataUiState.postValue(
                                currentState.copy(
                                    isShowingError = false,
                                    isLoading = false,
                                    errorMessage = "g"
                                )
                            )
                        }
                        is UIResponse.Error<*> -> {
                            val newState = currentState
                            .copy(
                                isShowingError = true,
                                errorMessage = resultPostList.msg,
                                isLoading = false
                            )
                            currentState = newState
                            liveDataUiState.postValue(newState)
                        }
                    }
                }
        }
    }

    fun setFavorite(postId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            val resultPostList = setFavoriteUseCase
                .execute(postId, isFavorite)
                .collect { resultPostList ->
                    //liveData.postValue(resultPostList)
                }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            deleteAllUseCase
                .execute()
                .collect { resultPostList ->
                    //liveData.postValue(resultPostList)
                }
        }
    }

    fun deletePostByAId(postId: String) {
        viewModelScope.launch {
            deletePostByIdUseCase
                .execute(postId)
                .collect { resultPostList ->
                    //liveData.postValue(resultPostList)
                }
        }
    }

    fun getLatestPost() {
        viewModelScope.launch {
            getLatestPostUseCase
                .execute()
                .collect { resultPostList ->
                    when (resultPostList) {
                        is Response.Error -> {
                            val newstate = currentState
                                .copy(
                                    isShowingError = true,
                                    errorMessage = "fallo carga"
                                )
                            currentState = newstate
                            liveDataUiState.postValue(newstate)
                        }
                        else -> {}
                    }
                }
        }
    }

    fun warningWasShown() {
        liveDataUiState.postValue(
            currentState.copy(
                isShowingError = false
            )
        )
    }
}