"""ContextCheck FastAPI Application Entry Point."""
import json
import datetime
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.config import settings
from app.database import engine, Base, SessionLocal
from app.models import VerificationHistory
from app.demo_scenarios import ALL_DEMO_SCENARIOS
from app.routers import auth, verify, history

# Initialize database schema
Base.metadata.create_all(bind=engine)

def seed_demo_history():
    """Seed initial demo verifications if database is fresh."""
    db = SessionLocal()
    try:
        count = db.query(VerificationHistory).count()
        if count == 0:
            for s in ALL_DEMO_SCENARIOS:
                item = VerificationHistory(
                    user_id=None,
                    content_type="TEXT" if s["id"] != "demo-pune-flood" else "VIDEO",
                    raw_content=s["claim"],
                    claim=s["claim"],
                    verdict=s["verdict"],
                    confidence=s["confidence"],
                    evidence_json=json.dumps(s["evidence"]),
                    sources_json=json.dumps(s["sources"]),
                    date_comparison=s["date_comparison"],
                    location_comparison=s["location_comparison"],
                    context_comparison=s["context_comparison"],
                    recommended_action=s["recommended_action"],
                    language="en",
                    is_demo=True,
                    timestamp=datetime.datetime.now(datetime.timezone.utc) - datetime.timedelta(hours=ALL_DEMO_SCENARIOS.index(s) * 2)
                )
                db.add(item)
            db.commit()
    except Exception as e:
        db.rollback()
    finally:
        db.close()

seed_demo_history()

app = FastAPI(
    title=settings.APP_NAME,
    description="Context-Aware Forward Verification API. Think Before You Believe. Verify Before You Share.",
    version="1.0.0"
)

# Configure CORS for mobile emulator, physical device, and web clients
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Mount API Routers
app.include_router(auth.router)
app.include_router(verify.router)
app.include_router(history.router)

@app.get("/health", tags=["Health"])
def health_check():
    """Health check endpoint to verify backend status."""
    return {
        "status": "healthy",
        "service": settings.APP_NAME,
        "tagline": settings.APP_TAGLINE,
        "version": "1.0.0"
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=8000, reload=True)
