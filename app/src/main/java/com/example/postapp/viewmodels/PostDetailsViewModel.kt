package com.example.postapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postapp.data.repositories.UNKNOWN_ERROR
import com.example.postapp.domain.GetPostCommentsUseCase
import com.example.postapp.domain.GetPostDetailsUseCase
import com.example.postapp.domain.utils.Response
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch



const val LOADING_TEXT = "Loading..."

class PostDetailsViewModel(
    private val getPostDetailsUseCase: GetPostDetailsUseCase,
    private val getPostCommentsUseCase: GetPostCommentsUseCase
): ViewModel() {

    companion object {
        data class UIPostSectionState(
            val postDetails : UIPostDetails,
            val userDetails : UIUserDetails,
            val isShowingError: Boolean,
            val errorMessage : String,
            val isLoading : Boolean
        )
        data class UIPostDetails(
            val postId: String = "",
            val userId: String = LOADING_TEXT,
            val title: String = LOADING_TEXT,
            val body: String = LOADING_TEXT,
            var isFavorite: Boolean = false
        )
        data class UIUserDetails(
            val name: String =  LOADING_TEXT,
            val email: String = LOADING_TEXT
        )
        data class UIComment(
            val name: String,
            val body: String
        )
        data class UICommentsSectionState(
            val comments: List<UIComment>,
            val isShowingError: Boolean,
            val errorMessage : String,
            val isLoading : Boolean
        )
    }

    val liveDataPostSection = MutableLiveData<UIPostSectionState>()
    val liveDataCommentsSection = MutableLiveData<UICommentsSectionState>()

    var currentUIPostSectionState =
        UIPostSectionState(
            postDetails = UIPostDetails(),
            userDetails = UIUserDetails(),
            isLoading = false,
            isShowingError = false,
            errorMessage = ""
        )
    var currentUIcommentsSectionState = UICommentsSectionState(
        comments = emptyList(),
        isShowingError = false,
        errorMessage = "",
        isLoading = false
    )

    fun getPostsDetails(postDetails: UIPostDetails) {
        viewModelScope.launch {
            getPostDetailsUseCase
                .execute(postDetails.userId)
                .onStart {  }
                .collect { resultUser ->
                    when (resultUser) {
                        is Response.Success -> {
                            val newState = currentUIPostSectionState.copy(
                                postDetails = postDetails,
                                userDetails = UIUserDetails(
                                    name = resultUser.data!!.name,
                                    email = resultUser.data!!.email
                                ),
                                isLoading = false,
                                isShowingError = false,
                            )
                            currentUIPostSectionState = newState
                            liveDataPostSection.postValue(newState)
                        }
                        is Response.Error -> {
                            val newState = currentUIPostSectionState.copy(
                                isShowingError = true,
                                errorMessage = resultUser.errorMessage?: UNKNOWN_ERROR,
                            )
                            currentUIPostSectionState = newState
                            liveDataPostSection.postValue(newState)
                        }
                    }
                }
        }
    }

    fun getCommentsSection(postId: String) {
        viewModelScope.launch {
            getPostCommentsUseCase
                .execute(postId)
                .collect { resultComments ->
                    when (resultComments) {
                        is Response.Success -> {
                            val comments = resultComments.data!!.map { comment ->
                                UIComment(
                                    name = comment.name,
                                    body = comment.body
                                )
                            }
                            val newCommentsSectionState = currentUIcommentsSectionState
                                .copy(
                                    comments = comments,
                                    isLoading = false,
                                    isShowingError = false
                                )
                            currentUIcommentsSectionState = newCommentsSectionState
                            liveDataCommentsSection.postValue(newCommentsSectionState)
                        }
                        is Response.Error -> {
                            val newCommentsSectionState = currentUIcommentsSectionState
                                .copy(
                                    isLoading = false,
                                    isShowingError = true,
                                    errorMessage = resultComments.errorMessage?: UNKNOWN_ERROR
                                )
                            currentUIcommentsSectionState = newCommentsSectionState
                            liveDataCommentsSection.postValue(newCommentsSectionState)
                        }
                    }
                }
        }
    }

    fun warningPostSectionWasShown() {
        liveDataPostSection.postValue(
            currentUIPostSectionState
                .copy(
                    isShowingError = false
                )
        )
    }
    fun warningCommentsSectionWasShown() {
        liveDataCommentsSection.postValue(
            currentUIcommentsSectionState.copy(
                isShowingError = false
            )
        )
    }
}