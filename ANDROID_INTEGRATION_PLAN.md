# MetroGuard AI Android Integration Plan

This document summarizes the current state of the MetroGuard AI repository and outlines the plan for Android integration.

## 1. System Architecture Overview

### Frontend (React/Vite)
- **Tech Stack**: React 18, TypeScript, Tailwind CSS, Framer Motion.
- **Auth Flow**: Custom JWT implementation. Token stored in `localStorage` as `metroguard_jwt`.
- **API Communication**: Uses `fetch` with a `getAuthHeaders` helper to attach the Bearer token.
- **Key Files**: 
    - [AuthContext.tsx](file:///C:/Users/OM/AndroidStudioProjects/projects/MetroGuardAI-Web/frontend/src/context/AuthContext.tsx)
    - [api/index.ts](file:///C:/Users/OM/AndroidStudioProjects/projects/MetroGuardAI-Web/frontend/src/api/index.ts)

### Backend (Spring Boot)
- **Tech Stack**: Java 17, Spring Boot 3, Spring Security, Hibernate/JPA.
- **Database**: PostgreSQL (hosted on Neon).
- **Authentication**: JWT-based security with roles (`viewer`, `enforcement_officer`, `admin`).
- **CORS**: Globally configured to allow all origins (`*`), simplifying mobile integration.
- **Key Controllers**:
    - [AuthController](file:///C:/Users/OM/AndroidStudioProjects/projects/MetroGuardAI-Web/backend/src/main/java/com/sih/lmpc_compliance/controller/AuthController.java)
    - [ScanController](file:///C:/Users/OM/AndroidStudioProjects/projects/MetroGuardAI-Web/backend/src/main/java/com/sih/lmpc_compliance/controller/ScanController.java)
    - [ImageComplianceController](file:///C:/Users/OM/AndroidStudioProjects/projects/MetroGuardAI-Web/backend/src/main/java/com/sih/lmpc_compliance/controller/ImageComplianceController.java)

### AI Service (FastAPI)
- **Tech Stack**: Python, FastAPI, OpenCV, Google GenAI (Gemini).
- **OCR Pipeline**: Preprocessing -> Gemini Extraction -> Compliance Engine.
- **RAG Assistant**: Uses a PDF of LMPC Rules 2011 to answer compliance queries.
- **Key Endpoints**: `/analyze/image`, `/analyze/ecommerce`, `/generate-report`, `/chat`.
- **Port**: 8000 (Internal to backend).

---

## 2. API Contract for Android Integration

### Authentication
- **Login**: `POST /api/auth/login`
    - Request: `{ "email": "...", "password": "..." }`
    - Response: `{ "token": "...", "id": "...", "email": "...", "name": "...", "role": "..." }`
- **Register**: `POST /api/auth/register`
    - Request: `{ "name": "...", "email": "...", "password": "..." }`

### Compliance & Scanning
- **Image Scan**: `POST /api/compliance/analyze-image`
    - Format: `multipart/form-data`
    - Params: `file` (Image), `manual_pack_width_cm` (Optional), `manual_pack_height_cm` (Optional), `is_molded` (Optional boolean).
    - Auth Required: Yes (JWT).
- **E-commerce Scan**: `POST /api/compliance/analyze-ecommerce`
    - Format: `application/json`
    - Request: `{ "url": "..." }`
- **Generate Report**: `POST /api/compliance/generate-report`
    - Params: `file`, `format` ("pdf" or "docx").
    - Response: File blob.

### History & Review
- **List Scans**: `GET /api/scans`
- **Get Scan Details**: `GET /api/scans/{id}`
- **Get Violations**: `GET /api/scans/{id}/violations`
- **Get Product Image**: `GET /api/scans/{id}/image`
- **Resolve Review**: `PUT /api/scans/{id}/review?status=approved` (Enforcement Officer only).

### Assistant
- **Chat**: `POST http://localhost:8000/chat` (Note: Backend currently doesn't proxy this, Android might need to call it directly or a proxy endpoint should be added to Spring Boot).
    - Request: `{ "message": "...", "context": "..." }`

---

## 3. Implementation Status

| Feature | Status | Implementation Detail |
| :--- | :--- | :--- |
| **User Authentication** | Real | Fully implemented with JWT and BCrypt. |
| **Image Analysis** | Real | Integrated with Gemini 1.5/2.0 for OCR. |
| **Compliance Engine** | Real | Logic for font size, placement, and presence. |
| **Database Persistence** | Real | Scans and violations are saved in PostgreSQL. |
| **E-commerce Analysis** | Real | Scrapes images from URLs and analyzes them. |
| **PDF Report Generation** | Real | Generates formatted PDF certificates. |
| **LMPC Assistant** | Real | RAG implementation using Rulebook PDF. |
| **Review Queue** | Incomplete | Resolving reviews exists, but complex workflow is minimal. |

---

## 4. Scan Data Flow

1. **Capture**: Android app captures image and sends to `Spring Boot /api/compliance/analyze-image`.
2. **Proxy**: Spring Boot saves file locally and proxies the request to `FastAPI /analyze/image`.
3. **Enhance**: FastAPI enhances image if resolution is low (using ESPCN model).
4. **Extract**: Gemini AI extracts text declarations and bounding boxes.
5. **Verify**: `ComplianceEngine` checks extracted data against LMPC 2011 rules.
6. **Persist**: Spring Boot receives result, saves it to `scans` and `violations` tables.
7. **Respond**: Android receives full JSON result including compliance status and violation list.

---

## 5. Integration Feasibility & Required Changes

### Can Android integrate as-is?
**YES.** The backend is already designed as a RESTful service with JWT auth and CORS support.

### Recommended Backend Enhancements for Android
1. **Chat Proxy**: The `/chat` endpoint is currently in the FastAPI service. Spring Boot should expose a proxy endpoint (e.g., `/api/assistant/chat`) so Android only needs to talk to one base URL.
2. **Image Serving**: Ensure `ScanController.getScanImage` handles all image formats correctly (currently supports JPEG/PNG/WEBP).
3. **Health Check**: Android should call `/api/health` on startup to wake up the Neon DB (which scales to zero when idle).

---

## 6. Minimum API Set for Android App

1. `POST /api/auth/login` (Authentication)
2. `POST /api/compliance/analyze-image` (Core Scanning)
3. `GET /api/scans` (History)
4. `GET /api/scans/{id}` (Details)
5. `GET /api/scans/{id}/violations` (Evidence)
6. `POST /api/compliance/generate-report` (Export)
7. `POST /api/assistant/chat` (AI Help - *requires proxy*)
