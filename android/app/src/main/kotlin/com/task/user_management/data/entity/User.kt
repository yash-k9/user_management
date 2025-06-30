package com.task.user_management.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val phoneNumber: String,
    val profileImagePath: String? = null,
    val signatureBase64: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
