package com.task.user_management

import android.content.Intent
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, EVENT_CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                ADD_USER_ACTIVITY -> {
                    try {
                        val intent = Intent(this, AddUserActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    } catch (e: Exception) {
                        result.error("ACTIVITY_ERROR", e.localizedMessage, null)
                    }
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }

    companion object {
        const val EVENT_CHANNEL = "nativeChannel"
        const val USER_STREAM_CHANNEL = "userStreamChannel"
        const val ADD_USER_ACTIVITY = "add_user"
    }
}
