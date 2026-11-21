# ContextCheck

> **Think Before You Believe. Verify Before You Share.**

ContextCheck is a context-aware forward verification platform engineered to help users verify forwarded WhatsApp messages, images, videos, and voice notes before believing or sharing them.

---

## The Core Problem & Innovation

People frequently receive forwarded WhatsApp messages, photos, videos, and audio clips and struggle to determine whether the information is authentic. Misinformation causes panic, mistrust, social tension, confusion, and real-world harm.

Most AI tools act as generic text detectors. **ContextCheck is fundamentally different.**

### Core Innovation: Context-Aware Forward Verification

ContextCheck evaluates both:
1. **The Content** (text, visual elements, audio transcription, entities, claims)
2. **The Context Attached to the Content** (claimed date, claimed location, caption, origin, timeline match)

#### Primary Product USP: Real Media, False Context

A genuine historical video from an old flood (e.g., Katraj, Pune in September 2019) is forwarded with:
> *"This video shows today's flooding in Pune."* / *"हा video पुण्यातला आहे आणि आजचा आहे."*

The video itself is authentic and unmanipulated. But the **context attached to it is completely false**.

ContextCheck detects this temporal/spatial disconnect and returns:
```
Verdict: MISLEADING CONTEXT
USP Tag: REAL MEDIA, FALSE CONTEXT
Date Comparison: Claimed: Today | Evidence: September 26, 2019 (Historical archive)
Location Comparison: Claimed: Pune (Current flood) | Evidence: Katraj (2019 incident)
Context Comparison: Genuine flood footage repurposed to falsely depict current weather conditions.
Recommended Action: DON'T FORWARD
```

---

## Supported Verdicts

ContextCheck produces calibrated, evidence-backed verdicts:

| Verdict | Meaning |
| :--- | :--- |
| **SUPPORTED** | Claim matches verified facts from authoritative and regulatory sources. |
| **FALSE** | Factually debunked or identified as a fraudulent campaign / phishing scheme. |
| **MISLEADING CONTEXT** | Authentic media or partial truths presented with inaccurate dates, locations, or stories (**Primary USP**). |
| **UNVERIFIED** | Insufficient public evidence exists to substantiate the claim. |
| **INCONCLUSIVE** | Evidence is conflicting or requires primary source verification. |

> **Ethical Guarantee**: When evidence is insufficient, ContextCheck returns **UNVERIFIED** or **INCONCLUSIVE**. It **never fabricates** fake fact checks, imaginary government circulars, or synthetic sources.

---

## The 1-Tap WhatsApp Experience

WhatsApp does not allow third-party apps to inject actions into its native long-press popup. ContextCheck implements a legitimate, privacy-respecting Android flow:

```
Forwarded WhatsApp Message / Media
                 ↓
User long-presses or selects message
                 ↓
ContextCheck Action Pill appears ("[ ✓ Verify with ContextCheck ]")
                 ↓
User taps ContextCheck ONCE
                 ↓
Verification starts immediately (No confirmation screens)
                 ↓
Animated Progress Screen
                 ↓
Structured Context Verification Report
```

- **ONE TAP** starts verification.
- **NO** second "what do you want to verify?" screen.
- **NO** manual copy-paste required in the normal flow.
- **NO** continuous background conversation monitoring or microphone snooping.

---

## Visual Design System

ContextCheck strictly adheres to this calm, trustworthy, professional palette:

- **Sage Green (`#668F7A`)**: Primary brand color, headers, action buttons, verification badges.
- **Warm White (`#F8FAF7`)**: Background and clean surface cards.
- **Dark Charcoal (`#26332D`)**: High-contrast, readable typography and accents.
- **Soft Mint (`#DCEBE2`)**: Secondary containers, highlights, and subtle pill badges.

---

## Tech Stack & Architecture

### Android Application
- **Language**: Kotlin 1.9.24
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM + Repository pattern with Coroutines & StateFlow
- **Networking**: Retrofit 2.11 + OkHttp 4.12
- **Persistence**: SharedPreferences / Preferences DataStore
- **Services**: Android AccessibilityService (`com.whatsapp`, `com.whatsapp.w4b`) + SystemAlertWindow Overlay
- **Target SDK**: Android 34 (Min SDK 26+)

### Backend API
- **Framework**: FastAPI (Python 3.13)
- **Validation**: Pydantic V2
- **Server**: Uvicorn
- **Database**: SQLite (SQLAlchemy ORM)
- **Security**: Bcrypt password hashing + PyJWT Bearer Authentication
- **Test Suite**: Pytest (12/12 unit and integration tests passing)

---

## Deterministic Demo Scenarios

ContextCheck includes deterministic offline-resilient demo scenarios ready for hackathon presentation:

1. **Demo 1: False Government Recharge Scheme**
   - *Message*: `"Urgent! PM Free Recharge Scheme gives ₹500 free recharge to all Indian users. Click link to claim now!"`
   - *Verdict*: `FALSE`
   - *Action*: `DON'T FORWARD`
2. **Demo 2: Real Media with False Context (USP - Multilingual)**
   - *Message*: `"हा video पुण्यातला आहे आणि आजचा आहे. Massive flooding near highway in Pune!"`
   - *Verdict*: `MISLEADING CONTEXT`
   - *USP*: Real media, false context (September 2019 Katraj flood footage re-circulated as today).
   - *Action*: `DON'T FORWARD`
3. **Demo 3: Unverified Local Rumor**
   - *Message*: `"Attention all parents: school holiday declared tomorrow due to surprise inspection across city schools."`
   - *Verdict*: `UNVERIFIED`
   - *Action*: `WAIT FOR OFFICIAL CONFIRMATION`

---

## Backend API Endpoints

- `GET /health` - System health and tagline check
- `POST /auth/register` - Create user account (returns JWT token)
- `POST /auth/login` - Authenticate existing user (returns JWT token)
- `GET /auth/me` - Current authenticated user profile
- `POST /verify/text` - Verify forwarded text and attached context
- `POST /verify/image` - Verify forwarded image and context
- `POST /verify/video` - Verify forwarded video and context
- `POST /verify/audio` - Verify voice notes from transcription
- `GET /history` - List verification history
- `GET /history/{id}` - Fetch specific verification report
- `DELETE /history/{id}` - Delete single verification record
- `DELETE /history` - Clear entire verification history

---

## Running the Project

### 1. Backend Setup & Tests

```bash
# Navigate to backend directory
cd backend

# Install dependencies
pip install -r requirements.txt

# Run automated tests
python -m pytest tests/ -v

# Start FastAPI server
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```

### 2. Android Build & Execution

```bash
# Navigate to android directory
cd android

# Build debug APK
./gradlew clean assembleDebug
```

Output APK will be generated at:
`android/app/build/outputs/apk/debug/app-debug.apk`

### 3. Physical Device Testing

The app is built for testing on physical devices:
1. Open ContextCheck > Settings > Backend Server Configuration.
2. Enter your computer's LAN IP (e.g. `http://192.168.1.50:8000/`).
3. Tap **Test Server Connection** to verify `/health`.

---

## Privacy & Security

ContextCheck does **not**:
- Read or scrape private conversations continuously
- Upload WhatsApp messages without explicit user initiation
- Record microphone audio in the background
- Store unverified raw message content permanently

Users have full control to inspect, export, or permanently delete their verification history.
