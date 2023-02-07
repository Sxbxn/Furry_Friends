from pybo.connect_db import db
import datetime
from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from werkzeug.security import generate_password_hash, check_password_hash

class Animals(db.Model):
    animal_id = db.Column(db.Integer, primary_key=True)
    # routines = db.relationship('Routine')
    #추후에 추가

class User(db.Model): 
    __tablename__ = 'user'   #테이블 이름 : user
    email = db.Column(db.String(32), unique=True, nullable=False)
    user_id = db.Column(db.String(32), unique=True, nullable=False, primary_key=True)
    pw = db.Column(db.String(8), nullable=False)


    # def __init__(self, userid, email, password, **kwargs):
    #   self.userid = userid
    #   self.email = email
 
    #   self.set_password(password)


    def set_password(self, password):
        self.password = generate_password_hash(password)
 
    def check_password(self, password):
        return check_password_hash(self.password, password)
      
class Routine(db.Model):
    __tablename__= 'Routine'
    routine_id = db.Column(db.Integer, primary_key=True, unique=True, autoincrement=True)
    animal_id = db.Column(db.Integer, nullable=False)
    routine_name= db.Column(db.String(20), nullable=False)
    weekday = db.Column(db.String(10), nullable=False)

    def __init__(self, animal_id, routine_name, weekday):
        self.animal_id = animal_id
        self.routine_name = routine_name
        self.weekday = weekday

class ChecklistDefault(db.Model):
    __tablename__ = 'checklist_default'

    index = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)
    currdate = db.Column(db.String(20), nullable=False, default=datetime.datetime.now().date())
    animal_id = db.Column(db.ForeignKey('animals.animal_id'), nullable=False)

    food = db.Column(db.String(10))
    bowels = db.Column(db.String(10))
    note = db.Column(db.String(100))

    def __init__(self, currdate, animal_id, food, bowels, note):
        self.currdate = currdate
        self.animal_id = animal_id
        self.food = food
        self.bowels = bowels
        self.note = note


class ChecklistRoutine(db.Model):
    __tablename__='checklist_routine'

    index = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)
    currdate = db.Column(db.String(20), nullable=False, default=datetime.datetime.now().date())
    animal_id = db.Column(db.ForeignKey('animals.animal_id'), nullable=False)
    routine_id = db.Column(db.ForeignKey('Routine.routine_id'), nullable=False)
    routine_name = db.Column(db.String(20), nullable=False)
    status = db.Column(db.Integer, nullable=False, default="0")

    def __init__(self, currdate, animal_id, routine_id, routine_name, status):
        self.currdate = currdate
        self.animal_id = animal_id
        self.routine_id = routine_id
        self.routine_name = routine_name
        self.status = status