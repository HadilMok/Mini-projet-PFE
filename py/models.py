
from sqlalchemy import Column, Integer, String, ForeignKey, Boolean
from sqlalchemy.orm import relationship
from database import Base

class Professor(Base):
    __tablename__ = "professors"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, index=True)
    availability = Column(String)  # JSON string of available sessions

class Project(Base):
    __tablename__ = "projects"
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, index=True)
    encadrant_id = Column(Integer, ForeignKey("professors.id"))
    encadrant = relationship("Professor")

class Schedule(Base):
    __tablename__ = "schedule"
    id = Column(Integer, primary_key=True, index=True)
    project_id = Column(Integer, ForeignKey("projects.id"))
    professor_id = Column(Integer, ForeignKey("professors.id"))
    role = Column(String)
    session = Column(Integer)
    room = Column(String)
