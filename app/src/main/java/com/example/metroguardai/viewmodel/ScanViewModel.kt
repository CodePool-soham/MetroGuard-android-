package com.example.metroguardai.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metroguardai.data.dto.ComplianceResponse
import com.example.metroguardai.data.repository.ComplianceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScanStep {
    CAMERA,
    ANALYSIS,
    RESULT
}

data class ScanUiState(
    val isLoading: Boolean = false,
    val complianceResult: ComplianceResponse? = null,
    val error: String? = null,
    val selectedImageUri: Uri? = null,
    val originalImageUri: Uri? = null,
    val currentStep: ScanStep = ScanStep.CAMERA,
    val manualWidth: String = "",
    val manualHeight: String = "",
    val isMolded: Boolean = false,
)

class ScanViewModel(private val repository: ComplianceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            originalImageUri = uri,
            currentStep = ScanStep.ANALYSIS,
            complianceResult = null,
            error = null
        )
    }

    fun onConfirmCrop(croppedUri: Uri?) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = croppedUri,
            currentStep = ScanStep.ANALYSIS
        )
    }

    fun onRetake() {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = null,
            currentStep = ScanStep.CAMERA,
            complianceResult = null,
            error = null
        )
    }

    fun onWidthChanged(width: String) {
        _uiState.value = _uiState.value.copy(manualWidth = width)
    }

    fun onHeightChanged(height: String) {
        _uiState.value = _uiState.value.copy(manualHeight = height)
    }

    fun onMoldedChanged(isMolded: Boolean) {
        _uiState.value = _uiState.value.copy(isMolded = isMolded)
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(
            complianceResult = null,
            selectedImageUri = null,
            currentStep = ScanStep.CAMERA,
            error = null
        )
    }

    fun analyzeImage(imageBytes: ByteArray, fileName: String) {
        val currentState = _uiState.value
        val width = currentState.manualWidth.toDoubleOrNull()
        val height = currentState.manualHeight.toDoubleOrNull()
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.analyzeProductImage(
                imageBytes = imageBytes,
                fileName = fileName,
                widthCm = width,
                heightCm = height,
                isMolded = currentState.isMolded
            )
            
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    complianceResult = response,
                    currentStep = ScanStep.RESULT
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = exception.message ?: "An unexpected error occurred during analysis"
                )
            }
        }
    }
}
