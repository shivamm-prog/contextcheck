"""Pydantic Request and Response Schemas."""
from datetime import datetime
from typing import List, Optional
from pydantic import BaseModel, EmailStr, Field, ConfigDict

# User Schemas
class UserRegisterRequest(BaseModel):
    username: str = Field(..., min_length=3, max_length=50)
    email: EmailStr
    password: str = Field(..., min_length=6)
    full_name: Optional[str] = None

class UserLoginRequest(BaseModel):
    username: str
    password: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user_id: int
    username: str
    email: str

class UserResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    username: str
    email: str
    full_name: Optional[str] = None
    created_at: datetime

# Verification Request Schemas
class VerifyTextRequest(BaseModel):
    content: str = Field(..., min_length=1, description="Raw forwarded text from WhatsApp or other platforms")
    context_caption: Optional[str] = Field(None, description="Accompanying caption or context")
    claimed_date: Optional[str] = Field(None, description="Optional claimed event date")
    claimed_location: Optional[str] = Field(None, description="Optional claimed location")
    source_platform: Optional[str] = Field("WhatsApp", description="Origin platform")

class VerifyImageRequest(BaseModel):
    image_url: Optional[str] = None
    image_base64: Optional[str] = None
    caption: str = Field(..., min_length=1, description="Attached message or caption")
    claimed_date: Optional[str] = None
    claimed_location: Optional[str] = None

class VerifyVideoRequest(BaseModel):
    video_url: Optional[str] = None
    caption: str = Field(..., min_length=1, description="Attached message or caption")
    claimed_date: Optional[str] = None
    claimed_location: Optional[str] = None

class VerifyAudioRequest(BaseModel):
    audio_url: Optional[str] = None
    transcript_text: Optional[str] = None
    context_note: Optional[str] = None

# Unified Verification Response
class VerificationResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: Optional[int] = None
    verdict: str = Field(..., description="SUPPORTED | FALSE | MISLEADING CONTEXT | UNVERIFIED | INCONCLUSIVE")
    confidence: float = Field(..., ge=0.0, le=1.0)
    claim: str
    evidence: List[str] = Field(default_factory=list)
    sources: List[str] = Field(default_factory=list)
    date_comparison: str = ""
    location_comparison: str = ""
    context_comparison: str = ""
    recommended_action: str
    language: str = "en"
    entities: List[str] = Field(default_factory=list)
    suspicious_signals: List[str] = Field(default_factory=list)
    content_type: str = "TEXT"
    is_demo: bool = False
    timestamp: Optional[datetime] = None

# History Schemas
class HistoryItemResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    content_type: str
    claim: str
    verdict: str
    confidence: float
    recommended_action: str
    date_comparison: Optional[str] = None
    location_comparison: Optional[str] = None
    context_comparison: Optional[str] = None
    evidence: List[str] = Field(default_factory=list)
    sources: List[str] = Field(default_factory=list)
    timestamp: datetime
    is_demo: bool

class HistoryDeleteResponse(BaseModel):
    status: str
    deleted_id: Optional[int] = None
    message: str
