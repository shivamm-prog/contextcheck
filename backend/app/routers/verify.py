"""Verification API Router for ContextCheck."""
import json
import datetime
from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import User, VerificationHistory
from app.schemas import (
    VerifyTextRequest,
    VerifyImageRequest,
    VerifyVideoRequest,
    VerifyAudioRequest,
    VerificationResponse
)
from app.auth import get_optional_current_user
from app.verification_engine import (
    verify_text_content,
    verify_image_content,
    verify_video_content,
    verify_audio_content
)

router = APIRouter(prefix="/verify", tags=["Verification"])

def _save_history(
    db: Session,
    result: dict,
    raw_content: str,
    content_type: str,
    current_user: Optional[User]
) -> int:
    """Save verification record into SQLite history table."""
    try:
        history_entry = VerificationHistory(
            user_id=current_user.id if current_user else None,
            content_type=content_type,
            raw_content=raw_content[:2000],
            claim=result["claim"][:1000],
            verdict=result["verdict"],
            confidence=float(result["confidence"]),
            evidence_json=json.dumps(result["evidence"]),
            sources_json=json.dumps(result["sources"]),
            date_comparison=result.get("date_comparison", ""),
            location_comparison=result.get("location_comparison", ""),
            context_comparison=result.get("context_comparison", ""),
            recommended_action=result.get("recommended_action", "DON'T FORWARD"),
            language=result.get("language", "en"),
            is_demo=bool(result.get("is_demo", False)),
            timestamp=datetime.datetime.now(datetime.timezone.utc)
        )
        db.add(history_entry)
        db.commit()
        db.refresh(history_entry)
        return history_entry.id
    except Exception as e:
        db.rollback()
        # Non-fatal error; don't break verification response if DB storage fails
        return 0

@router.post("/text", response_model=VerificationResponse)
def verify_text(
    req: VerifyTextRequest,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Verify forwarded text content with attached context."""
    if not req.content or not req.content.strip():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Content cannot be empty"
        )
        
    result = verify_text_content(
        content=req.content,
        context_caption=req.context_caption,
        claimed_date=req.claimed_date,
        claimed_location=req.claimed_location,
        source_platform=req.source_platform
    )
    
    saved_id = _save_history(db, result, req.content, "TEXT", current_user)
    result["id"] = saved_id if saved_id > 0 else None
    return VerificationResponse(**result)

@router.post("/image", response_model=VerificationResponse)
def verify_image(
    req: VerifyImageRequest,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Verify forwarded image and attached caption/context."""
    if not req.caption or not req.caption.strip():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Caption or accompanying context is required for image verification"
        )
        
    result = verify_image_content(
        caption=req.caption,
        image_url=req.image_url,
        image_base64=req.image_base64,
        claimed_date=req.claimed_date,
        claimed_location=req.claimed_location
    )
    
    saved_id = _save_history(db, result, req.caption, "IMAGE", current_user)
    result["id"] = saved_id if saved_id > 0 else None
    return VerificationResponse(**result)

@router.post("/video", response_model=VerificationResponse)
def verify_video(
    req: VerifyVideoRequest,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Verify forwarded video claim against visual and contextual facts."""
    if not req.caption or not req.caption.strip():
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Caption or claim description is required for video verification"
        )
        
    result = verify_video_content(
        caption=req.caption,
        video_url=req.video_url,
        claimed_date=req.claimed_date,
        claimed_location=req.claimed_location
    )
    
    saved_id = _save_history(db, result, req.caption, "VIDEO", current_user)
    result["id"] = saved_id if saved_id > 0 else None
    return VerificationResponse(**result)

@router.post("/audio", response_model=VerificationResponse)
def verify_audio(
    req: VerifyAudioRequest,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Verify forwarded voice note / audio message claim."""
    if not req.transcript_text and not req.context_note and not req.audio_url:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Either transcript_text, context_note or audio_url must be provided"
        )
        
    result = verify_audio_content(
        transcript_text=req.transcript_text,
        audio_url=req.audio_url,
        context_note=req.context_note
    )
    
    raw_desc = req.transcript_text or req.context_note or req.audio_url or "Audio"
    saved_id = _save_history(db, result, raw_desc, "AUDIO", current_user)
    result["id"] = saved_id if saved_id > 0 else None
    return VerificationResponse(**result)
