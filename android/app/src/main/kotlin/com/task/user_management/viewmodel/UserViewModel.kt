package com.task.user_management.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.task.user_management.data.database.AppDatabase
import com.task.user_management.data.entity.User
import com.task.user_management.data.repository.UserRepositoryImpl
import com.task.user_management.data.repository.local.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Path as AndroidPath

data class Path(val points: List<Offset>)

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: IUserRepository

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _profileImagePath = MutableStateFlow<String?>(null)
    val profileImagePath: StateFlow<String?> = _profileImagePath

    private val _signaturePaths = MutableStateFlow<List<Path>>(emptyList())
    val signaturePaths: StateFlow<List<Path>> = _signaturePaths

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var _size: Size? = null

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepositoryImpl(userDao)
    }

    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateAddress(newAddress: String) {
        _address.value = newAddress
    }

    fun updatePhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun setSignatureCanvasSize(size: Size) {
        this._size = size
    }

    private fun generateSignatureBase64(): String? {
        if (_signaturePaths.value.isEmpty()) return null

        try {
            val bitmap = createBitmap(720, 720).apply {
                eraseColor(Color.WHITE)
            }
            val canvas = Canvas(bitmap)

            val scaleFactorX = _size?.width?.let { 720f / it } ?: 1f
            val scaleFactorY = _size?.height?.let { 720f / it } ?: 1f

            _signaturePaths.value.forEach { path ->
                val paint = Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 8f
                    style = Paint.Style.STROKE
                }
                val androidPath = AndroidPath()
                path.points.forEachIndexed { index, offset ->
                    val scaledX = offset.x * scaleFactorX
                    val scaledY = offset.y * scaleFactorY
                    if (index == 0) {
                        androidPath.moveTo(scaledX, scaledY)
                    } else {
                        androidPath.lineTo(scaledX, scaledY)
                    }
                }
                canvas.drawPath(androidPath, paint)
            }

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            return null
        }
    }

    fun saveUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val user = User(
                    name = _name.value,
                    address = _address.value,
                    phoneNumber = _phoneNumber.value,
                    profileImagePath = _profileImagePath.value?.toString(),
                    signatureBase64 = generateSignatureBase64()
                )
                repository.insertUser(user)
                clearForm()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun clearForm() {
        _name.value = ""
        _address.value = ""
        _phoneNumber.value = ""
        _profileImagePath.value = null
        _signaturePaths.value = emptyList()
    }

    suspend fun copyUriToInternalStorage(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "profile_$timeStamp.jpg"
                val internalDir = File(context.filesDir, "profile_images")
                if (!internalDir.exists()) {
                    internalDir.mkdirs()
                }
                val destinationFile = File(internalDir, fileName)

                val outputStream = FileOutputStream(destinationFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                destinationFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun updateProfileImageFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = copyUriToInternalStorage(uri)
            if (filePath != null) {
                _profileImagePath.value = filePath
            }
        }
    }

    fun createImageFile(): Uri {
        val context = getApplication<Application>()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val file = File.createTempFile(
            imageFileName,
            ".jpg",
            context.externalCacheDir
        )
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    fun addSignaturePath(path: Path) {
        _signaturePaths.value = _signaturePaths.value + path
    }

    fun updateLastSignaturePath(path: Path) {
        val currentPaths = _signaturePaths.value.toMutableList()
        if (currentPaths.isNotEmpty()) {
            currentPaths[currentPaths.lastIndex] = path
            _signaturePaths.value = currentPaths
        }
    }

    fun clearSignaturePaths() {
        _signaturePaths.value = emptyList()
    }
}
