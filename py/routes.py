
from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import SessionLocal
from scheduler import generate_schedule
from models import Project, Professor

router = APIRouter()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/generate_schedule")
def generate_schedule_api(db: Session = Depends(get_db)):
    projects = db.query(Project).all()
    professors = db.query(Professor).all()
    encadrants = {p.id: p.encadrant_id for p in projects}
    result = generate_schedule([p.id for p in projects], [p.id for p in professors], encadrants)
    return result
