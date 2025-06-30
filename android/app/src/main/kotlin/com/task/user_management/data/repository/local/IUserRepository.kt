package com.task.user_management.data.repository.local

import com.task.user_management.data.entity.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {

    fun getAllUsers(): Flow<List<User>>

    suspend fun insertUser(user: User): Long

    suspend fun deleteUserById(userId: Long)
}