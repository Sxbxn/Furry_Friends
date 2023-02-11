from flask_sqlalchemy import SQLAlchemy
import datetime
from connect_db import db
from werkzeug.security import generate_password_hash, check_password_hash
from sqlalchemy.orm import declarative_base, relationship


Base = declarative_base()
# db = SQLAlchemy()


class User(db.Model):
    __tablename__ = 'user'

    user_id = db.Column(db.String(10), primary_key=True, nullable=False, unique=True)
    pw = db.Column(db.String(20), nullable=False)
    email = db.Column(db.String(40), nullable=False, unique=True)


    def __init__(self, user_id, pw, email):
        self.user_id = user_id
        self.pw = pw
        self.email = email


class Animal(db.Model):
    __tablename__ = 'animals'

    animal_id = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)    
    user_id = db.Column(db.ForeignKey('user.user_id', ondelete='CASCADE'), nullable=False)

    user = db.relationship('User', backref=db.backref('animal_set')) #User의 객체에서 등록된 동물들 참조 가능

    animal_name = db.Column(db.String(10), nullable=False)
    bday = db.Column(db.String(10))
    sex = db.Column(db.String(10))
    neutered = db.Column(db.String(10))
    weight = db.Column(db.Float)
    image = db.Column(db.String, default="")


    def __init__(self, user, animal_name, bday, sex, neutered, weight, image):
        self.user = user
        self.animal_name = animal_name
        self.bday = bday
        self.sex = sex
        self.neutered = neutered
        self.weight = weight
        self.image = image


class Routine(db.Model):
    __tablename__= 'Routine'
    index = db.Column(db.Integer, primary_key=True, unique=True, autoincrement=False)
    animal_id = db.Column(db.ForeignKey('animals.animal_id', ondelete='CASCADE'), nullable=False)
    routine_id = db.Column(db.Integer)

    animal = db.relationship('Animal', backref=db.backref('routine_set'))

    routine_name= db.Column(db.String(20), nullable=False)
    weekday = db.Column(db.Integer, nullable=False)

    def __init__(self, animal, routine_id, routine_name, weekday):
        self.animal = animal
        self.routine_id = routine_id
        self.routine_name = routine_name
        self.weekday = weekday


class ChecklistDefault(db.Model):
    __tablename__ = 'checklist_default'

    index = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)
    currdate = db.Column(db.String(20), nullable=False, default=datetime.datetime.now().date())
    animal_id = db.Column(db.ForeignKey('animals.animal_id', ondelete='CASCADE'), nullable=False)

    animal = db.relationship('Animal', backref=db.backref('checklistDefault_set'))

    food = db.Column(db.String(10))
    bowels = db.Column(db.String(10))
    note = db.Column(db.String(100))

    def __init__(self, currdate, animal, food, bowels, note):
        self.currdate = currdate
        self.animal = animal
        self.food = food
        self.bowels = bowels
        self.note = note


class ChecklistRoutine(db.Model):
    __tablename__='checklist_routine'

    index = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)
    routine_index = db.Column(db.ForeignKey('Routine.index', ondelete='CASCADE'), nullable=False)
    animal_id = db.Column(db.ForeignKey('animals.animal_id', ondelete='CASCADE'), nullable=False)

    routine = db.relationship('Routine', backref=db.backref('checklistRoutine_set'))
    animal = db.relationship('Animal')
    
    currdate = db.Column(db.String(20), nullable=False, default=datetime.datetime.now().date())
    routine_name = db.Column(db.String(20))
    status = db.Column(db.Integer, default=0)

    def __init__(self, currdate, animal, routine, routine_name, status):
        self.currdate = currdate
        self.animal = animal
        self.routine = routine
        self.routine_name = routine_name
        self.status = status


class Journal(db.Model):
    __tablename__ = 'journal'

    index = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)
    animal_id = db.Column(db.ForeignKey('animals.animal_id', ondelete='CASCADE'), nullable=False)
    user_id = db.Column(db.ForeignKey('user.user_id', ondelete='CASCADE'), nullable=False)

    animal = db.relationship('Animal', backref=db.backref('journal_set'))
    user = db.relationship('User')

    title = db.Column(db.String)
    image = db.Column(db.String)
    content = db.Column(db.String)
    currdate = db.Column(db.String(10))

    def __init__(self, animal, user, title, image, content, currdate):
        self.animal = animal
        self.user = user
        self.title = title
        self.image = image
        self.content = content
        self.currdate = currdate


class Health(db.Model):
    __tablename__ = 'health'

    index = db.Column(db.Integer, primary_key=True, nullable=False, unique=True, autoincrement=True)
    animal_id = db.Column(db.ForeignKey('animals.animal_id'), nullable=False)
    user_id = db.Column(db.ForeignKey('user.user_id'), nullable=False)

    animal = db.relationship('Animal', backref=db.backref('health_set'))
    user = db.relationship('User')

    content = db.Column(db.String)
    image = db.Column(db.String, default="")
    comment = db.Column(db.String)
    currdate = db.Column(db.String(10))
    kind = db.Column(db.String)
    affected_area = db.Column(db.String)


    def __init__(self, animal, user, content, image, comment, currdate, kind, affected_area):
        self.animal = animal
        self.user = user
        self.content = content
        self.image = image
        self.comment = comment
        self.currdate = currdate
        self.kind = kind
        self.affected_area = affected_area
