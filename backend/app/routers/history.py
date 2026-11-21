"""History API Router for ContextCheck."""
import json
from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import User, VerificationHistory
from app.schemas import HistoryItemResponse, HistoryDeleteResponse
from app.auth import get_optional_current_user

router = APIRouter(prefix="/history", tags=["History"])

def _format_history_item(item: VerificationHistory) -> HistoryItemResponse:
    try:
        evidence = json.loads(item.evidence_json) if item.evidence_json else []
    except Exception:
        evidence = []
        
    try:
        sources = json.loads(item.sources_json) if item.sources_json else []
    except Exception:
        sources = []
        
    return HistoryItemResponse(
        id=item.id,
        content_type=item.content_type,
        claim=item.claim,
        verdict=item.verdict,
        confidence=item.confidence,
        recommended_action=item.recommended_action,
        date_comparison=item.date_comparison,
        location_comparison=item.location_comparison,
        context_comparison=item.context_comparison,
        evidence=evidence,
        sources=sources,
        timestamp=item.timestamp,
        is_demo=item.is_demo
    )

@router.get("", response_model=List[HistoryItemResponse])
def get_history(
    limit: int = 50,
    offset: int = 0,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Retrieve verification history. If logged in, shows user items; otherwise shows demo/recent items."""
    query = db.query(VerificationHistory)
    if current_user:
        query = query.filter(VerificationHistory.user_id == current_user.id)
        
    items = query.order_by(VerificationHistory.timestamp.desc()).offset(offset).limit(limit).all()
    return [_format_history_item(i) for i in items]

@router.get("/{id}", response_model=HistoryItemResponse)
def get_history_detail(
    id: int,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Retrieve detailed verification report from history by ID."""
    item = db.query(VerificationHistory).filter(VerificationHistory.id == id).first()
    if not item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Verification record with ID {id} not found"
        )
    return _format_history_item(item)

@router.delete("/{id}", response_model=HistoryDeleteResponse)
def delete_history_item(
    id: int,
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Delete a specific verification record by ID."""
    item = db.query(VerificationHistory).filter(VerificationHistory.id == id).first()
    if not item:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"Verification record with ID {id} not found"
        )
    db.delete(item)
    db.commit()
    return HistoryDeleteResponse(
        status="success",
        deleted_id=id,
        message="Verification history record deleted successfully"
    )

@router.delete("", response_model=HistoryDeleteResponse)
def clear_all_history(
    db: Session = Depends(get_db),
    current_user: Optional[User] = Depends(get_optional_current_user)
):
    """Clear all verification history for current user or all guest history."""
    query = db.query(VerificationHistory)
    if current_user:
        query = query.filter(VerificationHistory.user_id == current_user.id)
    
    deleted_count = query.delete(synchronize_session=False)
    db.commit()
    return HistoryDeleteResponse(
        status="success",
        message=f"Cleared {deleted_count} history records"
    )
