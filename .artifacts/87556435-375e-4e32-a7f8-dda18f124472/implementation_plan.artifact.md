# Implementation Plan — Scan Product Feature

This plan details the addition of the **Scan Product** module to the MetroGuard AI Android application. The implementation adheres strictly to the existing MVVM + Repository pattern, utilizes Jetpack Compose for a production-ready UI design, integrates standard CameraX and Gallery selection, and connects via Retrofit to the specified Spring Boot backend multipart endpoint.

## User Review Required

> [!IMPORTANT]
> - **Permissions:** Requires requesting `android.permission.CAMERA` at runtime. Standard permission requesting flow will be embedded into the Compose entry point.
> - **Dependencies:** We need to add standard CameraX dependencies (`androidx.camera:camera-camera2`, `androidx.camera:camera-lifecycle`, `androidx.camera:camera-view`) and an image URI helper/coil library if needed, but since we are keeping it minimal and stable, we can use Android's content resolver to convert URI to bytes/file, and native Compose graphics for image display.
> - **Backend Integration:** The request uses multipart form-data as specified (`file`, `manual_pack_width_cm`, `manual_pack_height_cm`, `is_molded`).

## Proposed Changes

### Configuration & Infrastructure

#### [MODIFY] [app/build.gradle.kts](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/build.gradle.kts)
- Add CameraX libraries to the dependencies section.

#### [MODIFY] [AndroidManifest.xml](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/AndroidManifest.xml)
- Declare `<uses-feature android:name="android.hardware.camera" android:required="false" />` and `<uses-permission android:name="android.permission.CAMERA" />`.

### Data Layer (DTOs, API & Repository)

#### [MODIFY] [AuthDtos.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/data/dto/AuthDtos.kt) or [NEW] [ComplianceDtos.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/data/dto/ComplianceDtos.kt)
- Create `ComplianceResponse`, `Violation` or related models mapping the response structure from `POST /api/compliance/analyze-image`.

#### [MODIFY] [ApiService.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/data/api/ApiService.kt)
- Add `@Multipart @POST("api/compliance/analyze-image")` function mapping to the requested signature.

#### [NEW] [ComplianceRepository.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/data/repository/ComplianceRepository.kt)
- Implement `analyzeProductImage(...)` that prepares `MultipartBody.Part` and handles repository success/error mapping.

### Presentation Layer (ViewModel & UI)

#### [NEW] [ScanViewModel.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/viewmodel/ScanViewModel.kt)
- Create `ScanUiState` representing `Idle`, `CameraPreview`, `ImageSelected`, `Analyzing`, `Success(ComplianceResult)`, and `Error(String)`.
- Expose methods to handle manual packet dimension parameter adjustments.

#### [MODIFY] [ScanScreen.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/ui/screens/scan/ScanScreen.kt)
- Replace placeholder screen with a full-fledged production view:
  - Camera preview surface using AndroidView + `PreviewView`.
  - Floating gallery picker button.
  - Image preview overlay once an image is captured or selected.
  - Optional Form fields for `width`, `height`, and `is_molded` toggle.
  - Analysis results section displaying compliance check summaries.

#### [MODIFY] [AppNavigation.kt](file:///D:/PROJECT/METROGUARD/MetroGuard-android-/app/src/main/java/com/example/metroguardai/ui/navigation/AppNavigation.kt)
- Wire `ScanViewModel` instance via a Factory passing down the configured API client context to the new screen route destination.

## Verification Plan

### Automated Tests
- Verification of project build compilation via Gradle build task: `./gradlew app:assembleDebug`.

### Manual Verification
- Verify navigation triggers smoothly via the Dashboard card click event.
- Verify camera permission request dialog pops up natively on app entry or feature trigger.
- Verify dimension parameters inputs work and pass safely to the state model layers.
