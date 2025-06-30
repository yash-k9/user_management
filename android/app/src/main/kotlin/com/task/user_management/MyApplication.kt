package com.task.user_management

import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        fun getContext(): Context {
            return instance?.applicationContext ?: throw IllegalStateException("Application context is null")
        }
    }
}
