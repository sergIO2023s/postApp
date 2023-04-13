package com.example.postapp.domain.model

data class PostdDetails(
    val userData: UserData,
    val postData: PostData,
    val comments: List<Comment>)

data class UserData(
    val id : String = "",
    val name : String = "",
    val email : String = "",
    val phone : String = "",
    val website : String = ""
)

data class PostData (
    val id: String = "",
    val userid: String = "",
    val title: String = "",
    val body: String = "",
    var isFavorite : Boolean = false,
    var isReaded : Boolean = false
)

data class Comment(
    val id : String = "",
    val name : String = "",
    val body : String = "",
    val email : String = ""
)