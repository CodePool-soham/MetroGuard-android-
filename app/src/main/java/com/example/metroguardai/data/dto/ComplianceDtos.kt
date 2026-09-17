package com.example.metroguardai.data.dto

/**
 * NOTE: This schema is PROVISIONAL and based on expected Spring Boot backend response.
 * Fields are subject to change once the actual backend implementation is verified.
 * [TODO]: Synchronize with backend team and update accordingly.
 */
data class ComplianceResponse(
    val id: String?,
    val timestamp: String?,
    val productName: String?,
    val isCompliant: Boolean,
    val packageType: String?,
    val verifiedDimensions: PackDimensions?,
    val declarations: List<DeclarationCheck>?,
    val violations: List<Violation>?,
    val overallRemarks: String?
)

data class PackDimensions(
    val widthCm: Double?,
    val heightCm: Double?,
    val isMolded: Boolean?,
    val computedVolumeCc: Double?,
    val dimensionStatus: String? // e.g. "VALIDATED", "MISMATCH"
)

data class DeclarationCheck(
    val fieldName: String?, // e.g., "MRP", "Net Quantity", "Manufacturer Address", "Customer Care"
    val foundText: String?,
    val isPresent: Boolean?,
    val isCompliant: Boolean?,
    val confidence: Double?,
    val remarks: String?
)

data class Violation(
    val fieldName: String?,
    val violationType: String?, // e.g., "MISSING_DECLARATION", "FONT_SIZE_INSUFFICIENT", "PLACEMENT_INCORRECT"
    val description: String?,
    val severity: String?, // e.g., "CRITICAL", "WARNING"
    val suggestedAction: String?
)
