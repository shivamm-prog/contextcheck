"""SQLAlchemy Database Models for ContextCheck."""
import datetime
from sqlalchemy import Column, Integer, String, Text, Float, Boolean, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from app.database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, index=True, nullable=False)
    email = Column(String(120), unique=True, index=True, nullable=False)
    hashed_password = Column(String(255), nullable=False)
    full_name = Column(String(100), nullable=True)
    created_at = Column(DateTime, default=datetime.datetime.utcnow)
    is_active = Column(Boolean, default=True)

    verifications = relationship("VerificationHistory", back_populates="user", cascade="all, delete-orphan")


class VerificationHistory(Base):
    __tablename__ = "verification_history"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=True, index=True)
    content_type = Column(String(20), default="TEXT", nullable=False)  # TEXT, IMAGE, VIDEO, AUDIO
    raw_content = Column(Text, nullable=False)
    claim = Column(Text, nullable=False)
    verdict = Column(String(50), nullable=False)  # SUPPORTED, FALSE, MISLEADING CONTEXT, UNVERIFIED, INCONCLUSIVE
    confidence = Column(Float, default=0.0)
    evidence_json = Column(Text, default="[]")
    sources_json = Column(Text, default="[]")
    date_comparison = Column(Text, default="")
    location_comparison = Column(Text, default="")
    context_comparison = Column(Text, default="")
    recommended_action = Column(String(100), default="DON'T FORWARD")
    language = Column(String(20), default="en")
    is_demo = Column(Boolean, default=False)
    timestamp = Column(DateTime, default=datetime.datetime.utcnow, index=True)

    user = relationship("User", back_populates="verifications")
