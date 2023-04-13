package com.example.postapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.postapp.data.datamodels.local.Comment
import com.example.postapp.data.datamodels.local.Post
import com.example.postapp.data.datamodels.local.User

@Database(entities = [Post::class, User::class, Comment::class],
    version = 6,
    exportSchema = false)
abstract class MainDataBase : RoomDatabase() {

    abstract fun postDao(): PostDao

    abstract fun userDao(): UserDao

    abstract fun commentDao(): CommentDao
}