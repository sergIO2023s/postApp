package com.example.postapp.data.repositories

import android.content.SharedPreferences
import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User
import com.example.postapp.data.datamodels.remote.CommentsFromAPI
import com.example.postapp.data.datamodels.remote.PostFromAPI
import com.example.postapp.data.datamodels.remote.UserFromAPI
import com.example.postapp.data.db.CommentDao
import com.example.postapp.data.db.PostDao
import com.example.postapp.data.db.UserDao
import com.example.postapp.data.remote.JsonPlaceholderApiClient
import com.example.postapp.domain.interfaces.IPostsRepository
import com.example.postapp.domain.utils.Response
import com.example.postapp.domain.utils.asResponse
import com.example.postapp.postapp.activities.STATE_INITIALIZED
import kotlinx.coroutines.flow.*

const val UNKNOWN_ERROR = "UNKNOWN_ERROR"

class Repository(
    private val postApiClient: JsonPlaceholderApiClient,
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val prefs: SharedPreferences
): IPostsRepository{
    override suspend fun getPosts(): Flow<Response<List<Post>>> {
        return postDao.getAllPosts()
            .transform{ postsList ->
                emit(postsList)
                syncData()
            }.map { postLists -> Response.Success(postLists).asResponse() }
            .catch { e ->
                emit(Response.Error(e.message?: UNKNOWN_ERROR))
            }
    }

    override suspend fun fetchRemotePosts(): Flow<Response<Unit>> {
        return flow {
            val remotePosts = postApiClient
                .getPosts()
                .map { post -> mapPostFromAPItoPost(post) }
            postDao.insertPosts(remotePosts)
            emit(Response.Success(Unit).asResponse())
        }.catch { e -> emit(Response.Error(e.message?:UNKNOWN_ERROR)) }
    }

    override suspend fun getUserById(userId: String): Flow<Response<User>> {
        return userDao.getUserById(userId)
            .transform {  localUser ->
                localUser?.let { emit(localUser) }
                val remoteUser = postApiClient.getUserById(userId)
                val user = mapUserFromAPItoUser(remoteUser)
                userDao.insertUser(user)
                emit(user)
            }.map { user -> Response.Success(user) }
            .catch { e -> Response.Error<User>(e.message?: "er") }
    }

    override suspend fun getCommentsByPostId(postId: String): Flow<Response<List<Comment>>> {
        return commentDao.getCommentsByPostId(postId)
            .transform {  localComments ->
                localComments?.let { emit(localComments) }
                val remoteComments = postApiClient.getCommentsByPostId(postId)
                val comments = mapCommentsFromAPItoComment(remoteComments)
                commentDao.insertComments(comments)
                emit(comments)
            }.map { comments -> Response.Success(comments).asResponse() }
            .catch { e -> Response.Error<List<Comment>>(e.message?: UNKNOWN_ERROR) }
    }

    override suspend fun getPostById(id: String): Flow<Response<Post>> {
        return postDao.getPostById(id)
            .map { post -> Response.Success(post!!)}
    }

    override suspend fun setFavorite(postId: String, isFavorite: Boolean): Flow<Boolean> {
        return flowOf(postDao.setFavoriteByPostId(postId, isFavorite))
            .map { postUdpated->
                postUdpated != -1
            }
    }

    override suspend fun deleteAllPosts(): Flow<Boolean> {
        return flowOf(postDao.deleteAllPostButFavorites())
            .map { postDeleted ->
                postDeleted != -1
            }
    }

    override suspend fun deletePostById(idPost: String): Flow<Boolean> {
        return flowOf(postDao.deletePostById(idPost)).map { true }
    }

    private fun mapPostFromAPItoPost(postFromAPI: PostFromAPI): Post {
        return Post(
            id = postFromAPI.id!!,
            userId = postFromAPI.userId?: "",
            title = postFromAPI.title?: "",
            body = postFromAPI.body?: "",
            isFavorite = postFromAPI.isFavorite?: false
        )
    }


    private fun mapUserFromAPItoUser(userFromAPI: UserFromAPI): User {
        return User(
            id = userFromAPI.id!!,
            name = userFromAPI.name?: "",
            email = userFromAPI.email?: ""
        )
    }

    private fun mapCommentsFromAPItoComment(commentsFromAPI: List<CommentsFromAPI>): List<Comment> {
        return commentsFromAPI.map { commentFromAPI ->
            Comment(
                id = commentFromAPI.id!!,
                name = commentFromAPI.name?: "",
                body = commentFromAPI.body?: "",
                postId = commentFromAPI.postId?: ""
            )
        }
    }

    private suspend fun syncData() {
        if (!prefs.getBoolean(STATE_INITIALIZED, false)) {
            val remotePosts = postApiClient
                .getPosts()
                .filter { post -> post.id != null }
                .map { post -> mapPostFromAPItoPost(post) }
            postDao.insertPosts(remotePosts)
            with (prefs.edit()) {
                putBoolean(STATE_INITIALIZED, true)
                apply()
            }
        }
    }


}