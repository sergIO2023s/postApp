package com.example.postapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.postapp.data.datamodels.local.Comment
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertComments(commentsList: List<Comment>): List<Long>

    @Query("SELECT * FROM comments WHERE comments.postId = :postId ORDER BY id DESC")
    fun getCommentsByPostId(postId: String): Flow<List<Comment>?>
}