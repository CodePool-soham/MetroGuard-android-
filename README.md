# MetroGuard AI - Android Client

MetroGuard AI is an intelligent, vision-based enforcement tool designed for the Ministry of Consumer Affairs to automatically assess packaged commodity labels for compliance with the **LMPC Rules, 2011**. This repository contains the Android mobile application, which serves as the frontend for field officers and consumers to scan products and receive instant compliance audits.

---

## 🎯 Project Overview

The MetroGuard AI Android application provides a mobile-first interface for interacting with the compliance engine. It allows users to capture product label images, perform on-device preprocessing (cropping), and upload them for analysis.

- **Frontend (Android):** Handles user interactions, camera integration, image capture, and result visualization.
- **Backend (Spring Boot):** Orchestrates database operations, user management, and proxies requests to the AI service.
- **OCR Service (FastAPI):** Leverages vision-language models (Gemini AI) to extract declarations and evaluate compliance.

---

## ⭐ Key Features

- 🔐 **User Authentication:** Secure Login and Registration flow with JWT-based session management.
- 📷 **Camera Integration:** Purpose-built camera interface using CameraX for capturing high-quality product label photos.
- ✂️ **Professional Image Cropping:** Integrated cropping tool to isolate specific label areas for higher OCR accuracy.
- 📏 **Specification Inputs:** Ability to provide manual package dimensions (width/height) and specify if the package is molded/rigid for refined compliance checks.
- 📊 **Compliance Dashboard:** Visual breakdown of "Compliant", "Non-Compliant", and "Review Required" statuses with detailed violation descriptions and rule citations.
- 🌙 **Theme Support:** Supports Light and Dark modes via Jetpack Compose.

---

## 🛠️ Technology Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose |
| **Design System** | Material 3 |
| **Networking** | Retrofit 2, OkHttp 4 |
| **Image Loading** | Coil |
| **Camera** | CameraX |
| **Persistence** | DataStore (Preferences) |
| **JSON Parsing** | Gson |
| **Image Processing** | CanHub Android Image Cropper |
| **Concurrency** | Kotlin Coroutines & Flow |

---

## 🏗️ Architecture

The application follows the **MVVM (Model-View-ViewModel)** architectural pattern combined with the **Repository Pattern** for a clean separation of concerns:

- **UI Layer (Compose):** Declarative UI components and screens.
- **ViewModel Layer:** Manages UI state and business logic using `StateFlow`.
- **Domain Layer:** Defines the core data models used across the app.
- **Data Layer:** Handles API communication via Retrofit and local session persistence via DataStore.

---

## 📂 Project Structure

```text
app/src/main/java/com/example/metroguardai/
├── data/
│   ├── api/          # Retrofit interface and interceptors
│   ├── dto/          # Data Transfer Objects for API communication
│   ├── local/        # DataStore session management
│   └── repository/   # Data source orchestration (Auth, Compliance)
├── domain/
│   └── model/        # Core business models
├── ui/
│   ├── components/   # Reusable Compose components
│   ├── screens/      # Feature screens (Auth, Dashboard, Scan)
│   └── theme/        # Theme and color definitions
├── util/             # Constants and helper classes
├── viewmodel/        # ViewModels for UI state management
└── MainActivity.kt   # Entry point and Navigation host
```

---

## 🔌 Backend / API Integration

The app communicates with a centralized Spring Boot backend. The primary endpoints used are:

- **Authentication:**
  - `POST /api/auth/login`: Authenticate user and receive JWT.
  - `POST /api/auth/register`: Create a new user account.
- **Compliance:**
  - `POST /api/compliance/analyze-images`: Multipart upload of the product label for AI-driven analysis.

Authentication is handled via a `Bearer` token injected by an `AuthInterceptor`.

---

## 🔍 Compliance Scan Flow

1. **Capture:** Use the integrated camera to take a photo of the product label.
2. **Crop:** Refine the image to focus on mandatory declarations.
3. **Spec Input:** (Optional) Provide package width/height and surface type (molded).
4. **Analyze:** The image is sent as a `MultipartBody.Part` to the backend.
5. **Results:** The app displays a compliance score, found declarations, missing fields, and specific rule violations.

---

## 🚀 Setup & Running

### Requirements
- **Android Studio** (Ladybug 2024.2.1 or newer recommended)
- **JDK 17**
- **Android SDK 37** (Target) / **Min SDK 26** (Android 8.0)

### Local Configuration
Before running the app, you must configure the backend URL:
1. Open [Constants.kt](file:///app/src/main/java/com/example/metroguardai/util/Constants.kt).
2. Replace `YOUR_BACKEND_URL` with your local development host (e.g., `http://10.0.2.2:8081/` for Android Emulator).

### Running the App
1. Clone the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Select a physical device or emulator.
5. Click **Run**.

### Building the APK
To generate a debug APK, run the following command in the terminal:
```bash
./gradlew assembleDebug
```
The APK will be generated in `app/build/outputs/apk/debug/`.

---

## 🔐 Security

This repository does **not** contain:
- Hardcoded production API keys.
- Private JWT signing secrets.
- Real user credentials or database passwords.
- Internal network IP addresses.

All secrets must be provided via local environment variables or configuration files (`local.properties`) and are ignored by Git.

---

## ⚠️ Disclaimer
MetroGuard AI is a prototype/hackathon project. Compliance results generated by the AI engine are for assistance only and should be reviewed by qualified legal metrology personnel before any official regulatory action is taken.
