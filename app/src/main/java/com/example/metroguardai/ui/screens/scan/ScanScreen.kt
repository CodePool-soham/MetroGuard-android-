package com.example.metroguardai.ui.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.metroguardai.data.dto.ComplianceResponse
import com.example.metroguardai.viewmodel.ScanStep
import com.example.metroguardai.viewmodel.ScanViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Professional Crop Launcher
    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.onConfirmCrop(result.uriContent)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            cropImageLauncher.launch(
                CropImageContractOptions(
                    uri = it,
                    cropImageOptions = CropImageOptions(
                        guidelines = CropImageView.Guidelines.ON,
                        activityTitle = "Crop Product Image",
                        cropMenuCropButtonTitle = "Done",
                        toolbarColor = android.graphics.Color.parseColor("#0D47A1"),
                        toolbarTitleColor = android.graphics.Color.WHITE,
                        toolbarTintColor = android.graphics.Color.WHITE
                    )
                )
            )
        }
    }

    val imageCapture = remember { ImageCapture.Builder().build() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (uiState.currentStep) {
                            ScanStep.CAMERA -> "Product Scan"
                            ScanStep.ANALYSIS -> "Inspection Details"
                            ScanStep.RESULT -> "Audit Results"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep == ScanStep.CAMERA) onBack() else viewModel.onRetake()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.currentStep != ScanStep.CAMERA) {
                        IconButton(onClick = { viewModel.clearResult() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentStep) {
                ScanStep.RESULT -> {
                    ComplianceResultView(response = uiState.complianceResult!!)
                }
                ScanStep.ANALYSIS -> {
                    ImageAnalysisForm(
                        imageUri = uiState.selectedImageUri!!,
                        isLoading = uiState.isLoading,
                        error = uiState.error,
                        manualWidth = uiState.manualWidth,
                        manualHeight = uiState.manualHeight,
                        isMolded = uiState.isMolded,
                        onWidthChanged = viewModel::onWidthChanged,
                        onHeightChanged = viewModel::onHeightChanged,
                        onMoldedChanged = viewModel::onMoldedChanged,
                        onAdjustCrop = {
                            cropImageLauncher.launch(
                                CropImageContractOptions(
                                    uri = uiState.originalImageUri!!,
                                    cropImageOptions = CropImageOptions(
                                        guidelines = CropImageView.Guidelines.ON,
                                        activityTitle = "Adjust Crop",
                                        cropMenuCropButtonTitle = "Done",
                                        toolbarColor = android.graphics.Color.parseColor("#0D47A1"),
                                        toolbarTitleColor = android.graphics.Color.WHITE,
                                        toolbarTintColor = android.graphics.Color.WHITE
                                    )
                                )
                            )
                        }
                    ) {
                        try {
                            val inputStream = context.contentResolver.openInputStream(uiState.selectedImageUri!!)
                            val bytes = inputStream?.readBytes()
                            if (bytes != null) {
                                viewModel.analyzeImage(bytes, "audit_image.jpg")
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error reading image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                ScanStep.CAMERA -> {
                    if (hasCameraPermission) {
                        CameraView(
                            imageCapture = imageCapture,
                            onImageCaptured = { viewModel.onImageSelected(it) },
                            onGalleryClick = { galleryLauncher.launch("image/*") }
                        )
                    } else {
                        PermissionFallback(onGrant = { permissionLauncher.launch(Manifest.permission.CAMERA) })
                    }
                }
            }
        }
    }
}

@Composable
fun CameraView(
    imageCapture: ImageCapture,
    onImageCaptured: (Uri) -> Unit,
    onGalleryClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isTakingPicture by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onGalleryClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White)
            }

            Button(
                onClick = {
                    if (isTakingPicture) return@Button
                    isTakingPicture = true
                    val file = File(context.cacheDir, "CAP_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                isTakingPicture = false
                                onImageCaptured(Uri.fromFile(file))
                            }
                            override fun onError(exc: ImageCaptureException) {
                                isTakingPicture = false
                                Toast.makeText(context, "Capture Error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isTakingPicture) Color.Gray else MaterialTheme.colorScheme.primary)
                ) {
                    if (isTakingPicture) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White, strokeWidth = 2.dp)
                    }
                }
            }

            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}

@Composable
fun PermissionFallback(onGrant: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("Camera access is needed to scan products.", textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGrant) { Text("Allow Permission") }
    }
}

@Composable
fun ImageAnalysisForm(
    imageUri: Uri,
    isLoading: Boolean,
    error: String?,
    manualWidth: String,
    manualHeight: String,
    isMolded: Boolean,
    onWidthChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onMoldedChanged: (Boolean) -> Unit,
    onAdjustCrop: () -> Unit,
    onAnalyzeClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().height(280.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Captured Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                
                FilledTonalButton(
                    onClick = onAdjustCrop,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adjust Crop", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Specifications", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        
        Row(modifier = Modifier.padding(vertical = 12.dp)) {
            OutlinedTextField(
                value = manualWidth,
                onValueChange = onWidthChanged,
                label = { Text("Width (cm)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = manualHeight,
                onValueChange = onHeightChanged,
                label = { Text("Height (cm)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(checked = isMolded, onCheckedChange = onMoldedChanged)
            Text("Molded/Rigid Package", style = MaterialTheme.typography.bodyMedium)
        }

        if (error != null) {
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAnalyzeClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Analyze Metrology Compliance", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ComplianceResultView(response: ComplianceResponse) {
    val isCompliant = response.status == "COMPLIANT"
    val color = when (response.status) {
        "COMPLIANT" -> Color(0xFF2E7D32)
        "REVIEW_REQUIRED" -> Color(0xFFF57C00)
        else -> Color(0xFFC62828)
    }
    
    val statusIcon = when (response.status) {
        "COMPLIANT" -> Icons.Default.CheckCircle
        "REVIEW_REQUIRED" -> Icons.Default.Info
        else -> Icons.Default.Warning
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Surface(
            color = color.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, color),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(statusIcon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = response.status.replace("_", " "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text("Compliance Score: ${response.score}%", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (response.foundDeclarations.isNotEmpty()) {
            Text("Found Declarations", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            response.foundDeclarations.forEach { declaration ->
                ListItem(
                    headlineContent = { Text(declaration, fontWeight = FontWeight.SemiBold) },
                    trailingContent = { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (response.missingDeclarations.isNotEmpty()) {
            Text("Missing Declarations", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
            Spacer(modifier = Modifier.height(8.dp))
            response.missingDeclarations.forEach { declaration ->
                ListItem(
                    headlineContent = { Text(declaration, fontWeight = FontWeight.SemiBold) },
                    trailingContent = { Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFC62828)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (response.violations.isNotEmpty()) {
            Text("Detected Violations", fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
            Spacer(modifier = Modifier.height(8.dp))
            response.violations.forEach { violation ->
                ListItem(
                    headlineContent = { Text(violation, style = MaterialTheme.typography.bodyMedium) },
                    leadingContent = { Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp)) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Scan ID: ${response.scanId}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}
