package com.task.user_management.viewmodel

import androidx.lifecycle.ViewModel
import com.task.user_management.data.repository.UserRepositoryImpl
import com.task.user_management.data.repository.local.IUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MainViewModel() : ViewModel() {
    private val userRepository: IUserRepository = UserRepositoryImpl()

    fun getAllUsers(): Flow<List<Map<String, Any?>>> {
        return userRepository.getAllUsers().map { users ->
            users.map { user ->
                mapOf(
                    "id" to user.id,
                    "name" to user.name,
                    "address" to user.address,
                    "phoneNumber" to user.phoneNumber,
                    "profileImagePath" to user.profileImagePath,
                    "signatureBase64" to user.signatureBase64,
                    "createdAt" to user.createdAt
                )
            }
        }
    }

    suspend fun deleteUserById(userId: Long, filePath: String?) {
        userRepository.deleteUserById(userId, filePath)
    }
}
