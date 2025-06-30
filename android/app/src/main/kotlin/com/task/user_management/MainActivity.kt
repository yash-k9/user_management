package com.task.user_management

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.task.user_management.viewmodel.MainViewModel
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity(), EventChannel.StreamHandler {
    private lateinit var viewModel: MainViewModel
    private var eventSink: EventChannel.EventSink? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModel()
        observeUserChanges()
    }

    private fun observeUserChanges() {
        lifecycleScope.launch {
            viewModel.getAllUsers().collect { userList ->
                eventSink?.success(userList)
            }
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            EVENT_CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                ADD_USER_ACTIVITY -> {
                    try {
                        val intent = Intent(this, AddUserActivity::class.java)
                        startActivity(intent)
                    } catch (e: Exception) {
                        result.error("ACTIVITY_ERROR", e.localizedMessage, null)
                    }
                }

                GET_USERS -> {
                    getUsersForFlutter(result)
                }

                DELETE_USER -> {
                    val userId = call.argument<Number>("id")?.toLong()
                    val filePath = call.argument<String>("profileImagePath").takeIf { it != null }
                    if (userId != null) {
                        deleteUser(userId, filePath, result)
                    } else {
                        result.error("INVALID_ARGUMENT", "User ID is required", null)
                    }
                }

                else -> {
                    result.notImplemented()
                }
            }
        }

        EventChannel(flutterEngine.dartExecutor.binaryMessenger, USER_STREAM_CHANNEL)
            .setStreamHandler(this)
    }


    private fun getUsersForFlutter(result: MethodChannel.Result) {
        lifecycleScope.launch {
            try {
                viewModel.getAllUsers().collect { userList ->
                    runOnUiThread {
                        try {
                            result.success(userList)
                        } catch (_: IllegalStateException) { }
                    }
                    return@collect
                }
            } catch (e: Exception) {
                runOnUiThread {
                    result.error("DATABASE_ERROR", "Failed to get users", e.message)
                }
            }
        }
    }

    private fun deleteUser(userId: Long, filePath: String?, result: MethodChannel.Result) {
        lifecycleScope.launch {
            try {
                viewModel.deleteUserById(userId, filePath)
                result.success(true)
            } catch (e: Exception) {
                result.error("DELETE_ERROR", "Failed to delete user", e.message)
            }
        }
    }

    override fun onListen(
        arguments: Any?,
        events: EventChannel.EventSink?
    ) {
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    companion object {
        const val EVENT_CHANNEL = "nativeChannel"
        const val USER_STREAM_CHANNEL = "userStreamChannel"
        const val ADD_USER_ACTIVITY = "add_user"
        const val DELETE_USER = "delete_user"
        const val GET_USERS = "get_users"
    }
}
