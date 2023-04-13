package com.example.postapp.data.datamodels.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "comments", indices = arrayOf(
    Index(value = ["id"],
        unique = true)
))
data class Comment(
    @PrimaryKey
    val id: String,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val body: String,
    @ColumnInfo
    val postId: String
)