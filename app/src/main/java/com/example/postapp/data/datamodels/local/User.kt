package com.example.postapp.data.datamodels.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = arrayOf(
    Index(value = ["id"],
        unique = true)
))
data class User(
    @PrimaryKey
    val id: String,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val email: String
)