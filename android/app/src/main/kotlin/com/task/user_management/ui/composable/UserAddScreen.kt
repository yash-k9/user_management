package com.task.user_management.ui.composable

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.task.user_management.R
import com.task.user_management.viewmodel.Path
import com.task.user_management.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddScreen(
    viewModel: UserViewModel,
    onFinish: () -> Unit
) {
    val name by viewModel.name.collectAsState()
    val address by viewModel.address.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val profileImagePath by viewModel.profileImagePath.collectAsState()
    val signaturePaths by viewModel.signaturePaths.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val maxDigits = 10
    var currentPath by remember { mutableStateOf(Path(listOf())) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val signatureColor = MaterialTheme.colorScheme.onSurface

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.updateProfileImageFromUri(photoUri!!)
        }
    }

    val launchCamera = {
        photoUri = viewModel.createImageFile()
        photoUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            AppBar(onFinish)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(contentAlignment = Alignment.Center) {
                ProfilePicture(profileImagePath, launchCamera)
            }

            Spacer(modifier = Modifier.height(16.dp))

            InputFields(name, viewModel, address, phoneNumber, maxDigits)

            SignatureCanvas(viewModel, currentPath, signaturePaths, signatureColor)

            Spacer(modifier = Modifier.requiredHeight(16.dp))

            SaveButton(viewModel, onFinish, isLoading, name, address, phoneNumber)

            Spacer(modifier = Modifier.requiredHeight(16.dp))
        }
    }
}

@Composable
private fun InputFields(
    name: String,
    viewModel: UserViewModel,
    address: String,
    phoneNumber: String,
    maxDigits: Int,
) {
    OutlinedTextField(
        value = name,
        maxLines = 1,
        onValueChange = viewModel::updateName,
        label = { Text(stringResource(R.string.name_label)) },
        modifier = Modifier.fillMaxWidth()
    )

    OutlinedTextField(
        value = address,
        maxLines = 1,
        onValueChange = viewModel::updateAddress,
        label = { Text(stringResource(R.string.address_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = { newValue ->
            val filteredValue = newValue.filter { it.isDigit() }.take(10)
            viewModel.updatePhoneNumber(filteredValue)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(stringResource(R.string.phone_number_label)) },
        isError = phoneNumber.length > maxDigits,
        supportingText = {
           if (phoneNumber.isNotEmpty() && phoneNumber.length < maxDigits) {
                Text(
                    text = "${phoneNumber.length}/$maxDigits",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}

@Composable
private fun SignatureCanvas(
    viewModel: UserViewModel,
    currentPath: Path,
    signaturePaths: List<Path>,
    signatureColor: Color
) {
    var currPath = currentPath
    Row(
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.signature_label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = stringResource(R.string.clear),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable {
                    viewModel.clearSignaturePaths()
                    currPath = Path(emptyList())
                }
                .padding(start = 8.dp),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clipToBounds()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currPath = Path(listOf(offset))
                            viewModel.addSignaturePath(currPath)
                        },
                        onDragEnd = {
                            currPath = Path(emptyList())
                        },
                        onDragCancel = {
                            currPath = Path(emptyList())
                        },
                        onDrag = { change, _ ->
                            //Update the current path with the new position
                            val newPoints = currPath.points + change.position
                            val newPath = Path(newPoints)
                            viewModel.updateLastSignaturePath(newPath)
                            currPath = newPath
                        }
                    )
                }
        ) {
            //Update size of the canvas for scaling
            viewModel.setSignatureCanvasSize(size)
            // Draw all stored paths
            signaturePaths.forEach { path ->
                val pathPoints = path.points
                if (pathPoints.size >= 2) {
                    for (i in 0 until pathPoints.size - 1) {
                        drawLine(
                            color = signatureColor,
                            start = pathPoints[i],
                            end = pathPoints[i + 1],
                            strokeWidth = 8f
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveButton(
    viewModel: UserViewModel,
    onFinish: () -> Unit,
    isLoading: Boolean,
    name: String,
    address: String,
    phoneNumber: String
) {
    Button(
        onClick = {
            viewModel.saveUser()
            onFinish()
        },
        enabled = !isLoading && name.isNotBlank() && address.isNotBlank() && phoneNumber.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.requiredSize(16.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(stringResource(R.string.save_user))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppBar(onFinish: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.add_user_title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            BackIcon(onFinish)
        }
    )
}

@Composable
private fun ProfilePicture(profileImagePath: String?, launchCamera: () -> Unit?) {
    if (profileImagePath != null) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .requiredSize(72.dp)
                .clip(CircleShape)
                .clickable { launchCamera() },
            model = profileImagePath,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .requiredSize(72.dp)
                .clip(CircleShape)
                .clickable { launchCamera() },
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun BackIcon(onFinish: () -> Unit) {
    IconButton(
        onClick = onFinish,
        content = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back_button",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}