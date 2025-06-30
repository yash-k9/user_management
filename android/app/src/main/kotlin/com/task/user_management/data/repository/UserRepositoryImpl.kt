package com.task.user_management.data.repository

import com.task.user_management.data.dao.UserDao
import com.task.user_management.data.entity.User
import com.task.user_management.data.repository.local.IUserRepository
import kotlinx.coroutines.flow.Flow

class UserRepositoryImpl(private val userDao: UserDao) : IUserRepository {
    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    override suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    override suspend fun deleteUserById(userId: Long) = userDao.deleteUserById(userId)
}
