package com.task.user_management.data.repository

import com.task.user_management.MyApplication
import com.task.user_management.data.dao.UserDao
import com.task.user_management.data.database.AppDatabase
import com.task.user_management.data.entity.User
import com.task.user_management.data.repository.local.IUserRepository
import kotlinx.coroutines.flow.Flow
import java.io.File

class UserRepositoryImpl : IUserRepository {
    private val userDao: UserDao

    init {
        val context = MyApplication.getContext()
        val appDatabase = AppDatabase.getDatabase(context)
        userDao = appDatabase.userDao()
    }

    override fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    override suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    override suspend fun deleteUserById(userId: Long, filePath: String?) {
        userDao.deleteUserById(userId)
        if (filePath != null) {
            File(filePath).takeIf { it.exists() }?.delete()
        }
    }
}
