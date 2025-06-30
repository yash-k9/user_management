package com.task.user_management

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewmodel.compose.viewModel
import com.task.user_management.ui.composable.UserAddScreen
import com.task.user_management.ui.theme.AppTheme
import com.task.user_management.viewmodel.UserViewModel

class AddUserActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val viewModel: UserViewModel = viewModel()
                UserAddScreen(
                    viewModel = viewModel,
                    onFinish = ::finish
                )
            }
        }
    }
}