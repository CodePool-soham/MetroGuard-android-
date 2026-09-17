package com.example.metroguardai.data.repository

import com.example.metroguardai.data.api.ApiService
import com.example.metroguardai.data.dto.ComplianceResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class ComplianceRepository(private val apiService: ApiService) {

    suspend fun analyzeProductImage(
        imageBytes: ByteArray,
        fileName: String,
        widthCm: Double?,
        heightCm: Double?,
        isMolded: Boolean
    ): Result<ComplianceResponse> {
        return try {
            val fileRequestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, fileRequestBody)

            val widthRequestBody = widthCm?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val heightRequestBody = heightCm?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val isMoldedRequestBody = isMolded.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = apiService.analyzeImage(
                file = filePart,
                manualWidth = widthRequestBody,
                manualHeight = heightRequestBody,
                isMolded = isMoldedRequestBody
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Analysis failed: ${response.message()} (Code: ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
