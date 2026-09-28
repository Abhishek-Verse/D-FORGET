# AI Task Organizer

> Android-first AI productivity app that turns screenshots, PDFs, notices, and pasted text into structured, reviewable tasks.

AI Task Organizer helps users turn scattered information into clear actions. Instead of manually reading a notice, extracting deadlines, and rebuilding reminders, the user can simply submit the content and let AI suggest tasks that can be reviewed and confirmed before saving.

This project combines a Kotlin + Jetpack Compose Android client with a FastAPI backend that performs AI analysis, PDF extraction, and source processing.

## Project status: local use

This is a personal project for local development and testing. It is not production-ready and does not include a hosted backend or a public AI service. Run the backend on your computer and connect the Android app to it.

### Quick local run (Windows PowerShell)

1. Start the backend setup from the repository root:

```powershell
cd backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
Copy-Item .env.example .env
```

2. Add your AI provider key to `backend/.env`:

```env
AI_API_KEY="your_own_api_key_here"
AI_MODEL="gpt-4o"
```

3. Start the backend from the `backend` directory:

```powershell
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

4. In Android Studio, open the `android` folder and run the app on an emulator. The default backend URL is `http://10.0.2.2:8000`. For a physical phone on the same Wi-Fi, set the backend URL in the app's Settings to your computer's LAN IP, such as `http://192.168.1.10:8000`.

The API key stays in your local backend environment; it is not included in the Android app. See [Local installation](#local-installation) for the full setup steps.

---

## Why this project

Students and busy professionals often receive important information in fragmented ways:

- screenshots
- PDFs
- text messages
- notices
- copied content from web pages
- shared documents

The app is designed to reduce that friction by transforming raw information into a clean task workflow.

---

## Key features

### AI-powered task generation
- analyzes plain text, screenshots, and PDFs
- extracts deadlines, priorities, and action items
- creates structured task suggestions
- keeps the user in control of final task creation

### AI inbox
- central place for unorganized information
- supports text input and file input
- supports source tracking for each task suggestion

### PDF and image processing
- PDF text extraction using PyMuPDF
- optional OCR support for image-based content
- structured cleanup before AI analysis

### Local-first task management
- tasks and reminders stored locally on Android
- works offline for viewing and managing saved tasks
- AI analysis depends on backend connectivity

### Human confirmation workflow
- AI only proposes tasks
- user reviews, edits, accepts, or rejects suggestions
- final tasks are created only after confirmation

### Reminder and notification support
- reminders are handled on-device
- notifications are created through Android native workflows

---

## Architecture

```text
User
  │
  ▼
Android App (Kotlin + Compose)
  │
  │  uploads / shares / analyzes content
  ▼
FastAPI Backend
  │
  ├─ PDF Extraction
  ├─ OCR Processing
  ├─ Source Storage
  └─ AI Task Analysis
        │
        ▼
   Structured Task Proposal
        │
        ▼
Android App
        │
        ▼
Review / Confirm / Save
        │
        ▼
Room Database + Local Reminder System
        │
        ▼
Notifications / Task Completion Flow
```

---

## Tech stack

### Android
- Kotlin
- Jetpack Compose
- Material 3
- ViewModel
- Room
- Navigation Compose
- Retrofit
- OkHttp

### Backend
- Python
- FastAPI
- Uvicorn
- Pydantic
- SQLAlchemy
- SQLite
- PyMuPDF

### AI layer
- OpenAI-compatible API integration via backend
- configuration-driven model selection
- server-side secret management

---

## Project structure

```text
D!FORGET/
├── android/                   # Android project
│   ├── app/
│   ├── gradle/
│   ├── build.gradle.kts
│   ├── gradle.properties
│   └── settings.gradle.kts
├── backend/                  # FastAPI backend
│   ├── app/
│   ├── tests/
│   ├── requirements.txt
│   ├── .env
│   ├── .env.example
│   └── app.db
├── docs/                     # project docs and design notes
├── stitch/                   # design assets / references
├── .gitignore
├── README.md
├── INSTALLATION.md
└── .venv/                    # local Python environment
```

---

## Prerequisites

Before starting, install:

- Python 3.10+
- JDK 17
- Android Studio
- Android SDK
- Git
- A valid AI provider API key for real model responses

---

## Local installation

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd D!FORGET
```

### 2. Set up the backend

Create and activate a virtual environment:

```bash
python -m venv .venv
.venv\Scripts\Activate.ps1
```

On macOS/Linux:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

Install dependencies:

```bash
cd backend
pip install -r requirements.txt
```

Create the environment file:

```bash
copy .env.example .env
```

Update the file with your API key:

```env
PROJECT_NAME="AI Task Organizer"
DATABASE_URL="sqlite:///./app.db"
AI_API_KEY="your_real_api_key_here"
AI_MODEL="gpt-4o"
UPLOAD_DIRECTORY="uploads"
ALLOWED_ORIGINS=["*"]
```

> Keep the API key on the server side. Do not hardcode it into Android client code.

Start the backend:

```bash
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Verify it is running:

- http://127.0.0.1:8000/health
- http://127.0.0.1:8000/docs

### 3. Set up the Android app

Open the Android project from the `android` folder in Android Studio.

Build the debug APK:

```bash
cd android
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
cd android
.\gradlew.bat assembleDebug
```

The APK will be generated at:

```text
android/app/build/outputs/apk/debug/app-debug.apk
```

### 4. Connect the app to the backend

The app defaults to the Android emulator address:

```text
http://10.0.2.2:8000
```

This works when running in the Android emulator. For a physical device, change the backend URL in the app's Settings to your machine's LAN IP, for example:

```text
http://192.168.1.10:8000/api/v1/
```

### 5. Run the app

- launch the emulator from Android Studio, or
- connect a physical Android device, or
- install the generated debug APK manually

Example manual install:

```bash
adb install -r android\app\build\outputs\apk\debug\app-debug.apk
```

---

## Usage flow

1. Open the Android app.
2. Paste text, upload an image, or share a PDF.
3. The backend extracts and processes the source.
4. AI generates structured task proposals.
5. Review the task details.
6. Edit, accept, or reject the proposal.
7. Save the confirmed task locally.
8. Add reminders and complete tasks from the Android app.

---

## AI behavior and rules

The app is designed around a human-in-the-loop workflow.

- AI suggests tasks, but does not automatically create them
- unclear or ambiguous deadlines remain reviewable
- multiple tasks may be created from one input
- information-only content is supported without forcing a task
- source details are preserved for transparency

---

## API overview

The backend exposes REST APIs for sources, tasks, and AI analysis.

```http
GET    /api/v1/tasks
GET    /api/v1/tasks/{id}
POST   /api/v1/tasks
PUT    /api/v1/tasks/{id}
DELETE /api/v1/tasks/{id}
PATCH  /api/v1/tasks/{id}/complete

POST   /api/v1/sources
GET    /api/v1/sources
GET    /api/v1/sources/{id}
DELETE /api/v1/sources/{id}

POST   /api/v1/analyze/{source_id}
```

---

## Security notes

- keep API keys in the backend `.env` file
- never embed secrets into the Android app
- keep `.env` excluded from Git
- validate uploaded files and sizes
- avoid exposing internal backend credentials in public repos

---

## Common issues

### Backend does not start
- ensure the venv is activated
- install requirements
- confirm `.env` exists and is valid
- ensure port 8000 is free

### Android cannot reach backend
- confirm the backend is running
- use `10.0.2.2` for the emulator
- use your machine LAN IP on a physical device

### AI analysis not working
- set `AI_API_KEY` in the backend `.env`
- verify the model name is supported
- confirm internet access is available

---

## Roadmap

- [x] product concept and MVP architecture
- [x] Android UI and app flow
- [x] backend API and source processing
- [x] AI-backed task suggestion flow
- [x] local storage and reminder handling
- [ ] release polish and production hardening
- [ ] OCR expansion and document quality improvements
- [ ] broader testing and edge-case validation

---

## License

This project does not currently include a public license declaration. Add your preferred license before public release if you plan to distribute it openly.

---

## Project summary

AI Task Organizer is a practical Android-first AI assistant for productivity. It aims to reduce the effort of turning messy information into structured tasks, reminders, and accountability.

Built with:

- Kotlin
- Jetpack Compose
- FastAPI
- Python
- SQLite
- PyMuPDF
- AI API integration

If you are building a local-first productivity app with AI-assisted task extraction, this project is a strong starting point for a real-world implementation.

## Tasks

```http
GET    /api/tasks
GET    /api/tasks/{id}
POST   /api/tasks
PUT    /api/tasks/{id}
DELETE /api/tasks/{id}
PATCH  /api/tasks/{id}/complete
```

## Sources

```http
POST   /api/sources
GET    /api/sources
GET    /api/sources/{id}
DELETE /api/sources/{id}
```

## AI

```http
POST   /api/analyze
```

## Reminders

```http
POST   /api/reminders
GET    /api/reminders
PUT    /api/reminders/{id}
DELETE /api/reminders/{id}
```

The `/api/analyze` endpoint returns an **AI proposal**, not an automatically created task.

---

# 🔐 Security

The project follows basic security practices.

- API keys are stored on the backend.
- API keys are never included in the APK.
- Uploaded files are validated.
- File sizes are validated.
- Filenames are sanitized.
- Uploaded files are never executed.
- AI responses are validated.
- Secrets are stored in environment variables.
- `.env` files are excluded from Git.

---

# 📴 Privacy & Data

The application is designed around a local-first productivity model.

Tasks and reminders are stored locally on the Android device.

When AI analysis is requested, the required content may be sent to the backend and configured AI provider for processing.

Future versions may support more local processing and local AI models.

---

# 🚧 Project Status

**Status: Active Development**

### Planning

- [x] Product concept
- [x] Product requirements
- [x] Android-first architecture
- [x] UI/UX design
- [x] Google Stitch prototype

### Development

- [ ] Android implementation
- [ ] Local Room database
- [ ] FastAPI backend
- [ ] Task APIs
- [ ] PDF processing
- [ ] OCR
- [ ] AI integration
- [ ] Android ↔ Backend integration
- [ ] AI proposal confirmation
- [ ] Local reminders
- [ ] Android notifications
- [ ] Share-to-App
- [ ] Final testing
- [ ] Release APK

---

# 🗺️ Roadmap

## Phase 1 — Android Foundation

- Android project
- Kotlin
- Jetpack Compose
- Theme
- Navigation

## Phase 2 — UI

- Home
- Tasks
- Task Detail
- AI Inbox
- AI Analysis
- Remaining screens

## Phase 3 — Local Data

- Room
- Task CRUD
- Reminder data
- Source data

## Phase 4 — Backend

- FastAPI
- SQLite
- APIs
- Validation

## Phase 5 — Document Processing

- PDF extraction
- OCR

## Phase 6 — AI

- AI service abstraction
- Structured output
- `/api/analyze`
- Confidence handling
- Deadline handling

## Phase 7 — Integration

- Android ↔ FastAPI
- AI proposal workflow
- Source tracking

## Phase 8 — Reminders

- Local scheduling
- Android notifications
- Notification actions

## Phase 9 — Android Integration

- Share-to-App
- File picker
- Clipboard analysis

## Phase 10 — Release

- Testing
- Performance improvements
- Security review
- Release APK

---

# 🧪 Testing

Backend testing uses:

```bash
pytest
```

Android testing will cover:

- Task creation
- Task editing
- Task completion
- Room persistence
- AI proposal confirmation
- Reminder scheduling
- Notification behavior
- API integration
- Error handling
- Offline behavior

---

# 💻 Local Development

## Prerequisites

Install:

- Android Studio
- Android SDK
- JDK compatible with the Android project
- Python 3.x
- Git
- Tesseract OCR

---

## Clone

```bash
git clone <repository-url>
cd AI-Task-Organizer
```

---

# 📱 Android

Open the `android/` directory in Android Studio.

Sync Gradle dependencies and run the application on:

- Android Emulator
- Physical Android device

---

# 🐍 Backend

Navigate to:

```bash
cd backend
```

Create a virtual environment:

```bash
python -m venv .venv
```

### Windows

```bash
.venv\Scripts\activate
```

### Linux / macOS

```bash
source .venv/bin/activate
```

Install dependencies:

```bash
pip install -r requirements.txt
```

Run FastAPI:

```bash
uvicorn app.main:app --reload
```

---

# ⚙️ Environment Variables

Create a local `.env` file inside the backend directory.

Example:

```env
AI_API_KEY=your_api_key_here
```

Never commit `.env`.

Use `.env.example` to document required environment variables without exposing secrets.

---

# 🤝 Development Philosophy

This project intentionally avoids unnecessary complexity.

The goal is to build a useful application with a clean architecture before introducing advanced infrastructure.

Principles:

- Keep the MVP simple.
- Prefer local-first productivity features.
- Keep AI as an assistant.
- Validate AI output.
- Never invent information.
- Keep reminders reliable.
- Avoid unnecessary dependencies.
- Build incrementally.
- Test every major feature.
- Keep Android and backend responsibilities separate.

---

# 🔮 Future Possibilities

Potential future features include:

- Desktop application
- Web application
- Cloud synchronization
- PostgreSQL
- Local AI
- Calendar integration
- Recurring tasks
- Voice input
- Email integration
- Browser extension
- Android widgets
- Smart scheduling
- Duplicate task detection
- Wear OS support

These are intentionally outside the initial MVP.

---

# 📜 License

License information will be added before public release.

---

## 👨‍💻 Project

**AI Task Organizer**

An AI-powered productivity project focused on turning unstructured information into actionable tasks and reliable reminders.

Built with:

**Kotlin • Jetpack Compose • Room • FastAPI • Python • PyMuPDF • Tesseract • AI**
