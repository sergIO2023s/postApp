package com.example.postapp.data.db

import androidx.room.*
import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPosts(posts: List<Post>): List<Long>

    @Query("SELECT * FROM posts ORDER BY isFavorite DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE posts.id = :postId ORDER BY id DESC")
    fun getPostById(postId: String): Flow<Post?>

    @Query("UPDATE posts SET isFavorite = :isFavorite WHERE posts.id = :postId")
    suspend fun setFavoriteByPostId(postId: String, isFavorite: Boolean): Int

    @Query("DELETE FROM posts WHERE isFavorite = false")
    suspend fun deleteAllPostButFavorites(): Int

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String): Int
}
