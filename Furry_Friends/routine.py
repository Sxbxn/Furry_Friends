#새 루틴 등록
from flask import Flask, request, jsonify, session, Blueprint, render_template, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
from pybo.model import Routine, ChecklistRoutine, ChecklistDefault, User, Animal
from pybo.connect_db import db
import json
import boto3
import requests
from flask import flash
import datetime
from sqlalchemy import and_
from markupsafe import escape
from werkzeug.security import generate_password_hash, check_password_hash
from werkzeug.utils import secure_filename
import os



def query_to_dict(objs):
    try:
        lst = []
        for obj in objs:
            obj = obj.__dict__
            del obj['_sa_instance_state']
            lst.append(obj)
        return lst
    except TypeError: # non-iterable
        lst = []
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst.append(objs)
        return lst

def to_weekday(num):
    weekdays = ['mon','tue','wed','thu','fri','sat','sun']
    num = int(num)
    return weekdays[num]
 
bp = Blueprint('routine', __name__, url_prefix='/routine')

@bp.route('/routine', methods=['GET','POST']) 
def routine():
    
 
    if request.method=="POST": #루틴 등록
         #json: 
    
        #json에서 각 값 임의 변수에 저장
        routine_id = request.headers['routineId']
        weekday = request.headers['weekDay']
        routine_name = request.headers['routineName']
        animal_id = request.headers['animalId']
        
        animal = Animal.query.filter_by(animal_id = animal_id).first()
        weekday = to_weekday(weekday)

        routine = Routine(routine_id=routine_id, animal=animal, routine_name=routine_name, weekday=weekday) 
        #db에 저장, 업데이트
        db.session.add(routine)
        db.session.commit()

        # date_dict = param['weekday'] #date 부분의 json array를 object로 변환
        # routine_date = [] #월수금
        # for key, val in date_dict.items():
        #     if val=='true':
        #         routine_date.append(key) #true인 요일값들 저장


        # #모델로 routine 객체 만들기
        # for i in range(len(routine_date)): #월, 수, 금 3개 저장
        #     routine = Routine(animal_id=animal_id, routine_name=routine_name, weekday=routine_date[i]) 
        #     #db에 저장, 업데이트
        #     db.session.add(routine)
        #     db.session.commit()

        return "success"
        # return redirect(url_for('routine.routine'))
    
    else: #get 루틴 불러오기
         #json: animal_id, routine_name, date:{mon=true,tue=true...}, 
    
        #json에서 각 값 임의 변수에 저장
     
        
        animal_id = request.headers['animalId']
        routines = Routine.query.filter(Routine.animal_id==animal_id).all()
        routines = query_to_dict(routines)
        return jsonify(routines)
        # return redirect(url_for('routine.routine'), routines = jsonify(routines))





#특정 루틴의 체크되어있던 요일 체크 해제 
@bp.route('/weekdaydelete', methods=['POST']) 
def weekdaydelete():
     #json: routine_name, animal_id, weekday
    
    #json에서 각 값 임의 변수에 저장
     
    routine_id = request.headers['routineId']
    weekday = request.headers['weekDay']
    routine_name = request.headers['routineName']
    animal_id = request.headers['animalId']
    del_date = to_weekday(weekday)

    #루틴 db 삭제
    
    del_routine = Routine.query.filter(and_(Routine.animal_id == animal_id, Routine.routine_id==routine_id,
                                                                Routine.weekday == del_date)).first() #체크였다가 체크해제된 row 탐색
        
    
    del_r = Routine.query.get(del_routine.index)
    db.session.delete(del_r)
    db.session.commit()

    
    
    return "success"
    # return redirect(url_for('routine.routine'))


#루틴 수정: 루틴 이름으로 수정은 안되고 아예 해당 루틴을 지우게끔 
@bp.route('/routinedelete', methods=['POST']) 
def routinedelete():
     #json:  animal_id, routine_name

    routine_id = request.headers['routineId']
    animal_id = request.headers['animalId']

    del_routines = Routine.query.filter(and_(Routine.animal_id == animal_id, Routine.routine_id==routine_id))
                                        
    for del_r in del_routines:
        r = Routine.query.get(del_r.index)
        db.session.delete(r)
        db.session.commit()

    return "success"
    # return redirect(url_for('routine.routine'))
