from flask import Flask, request, jsonify, session, Blueprint, url_for, redirect
from flask_sqlalchemy import SQLAlchemy
from models import Routine
from connect_db import db
from sqlalchemy import and_
import json


bp = Blueprint('routine', __name__, url_prefix='/routine')


@bp.route('/routine', methods=['POST']) 
def routine():
    param = request.get_json() #json: animal_id, routine_name, date:{mon=true,tue=true...}, 
    
    #json에서 각 값 임의 변수에 저장
     
    routine_name = param['routine_name']
    animal_id = param['animal_id']

     
    date_dict = param['weekday'] #date 부분의 json array를 object로 변환
    routine_date = [] #월수금
    for key, val in date_dict.items():
        if val=='true':
            routine_date.append(key) #true인 요일값들 저장


    #모델로 routine 객체 만들기
    for i in range(len(routine_date)): #월, 수, 금 3개 저장
        routine = Routine(animal_id=animal_id, routine_name=routine_name, weekday=routine_date[i]) 
        #db에 저장, 업데이트
        db.session.add(routine)
        db.session.commit()

    #json 반환
    return jsonify(param)



#특정 루틴의 체크되어있던 요일 체크 해제 
@bp.route('/weekdaydelete', methods=['POST']) 
def weekdaydelete():
    param = request.get_json() #json: routine_name, animal_id, del_date
    
    #json에서 각 값 임의 변수에 저장
     
    routine_name = param['routine_name']
    animal_id = param['animal_id'] 
    del_date = param['weekday'] #date 부분의 json array를 object로 변환


    #루틴 db 삭제
    # for i in range(len(not_routine_date)): #월화목
    del_routine = Routine.query.filter(and_(Routine.animal_id == animal_id, Routine.routine_name==routine_name,
                                                                Routine.weekday == del_date)).first() #체크였다가 체크해제된 row 탐색
        
    del_routine.routine_id
    del_r = Routine.query.get(del_routine.routine_id)
    db.session.delete(del_r)
    db.session.commit()

    #json 반환
    return jsonify(param)


#루틴 수정: 루틴 이름으로 수정은 안되고 아예 해당 루틴을 지우게끔 
@bp.route('/routinedelete', methods=['POST']) 
def routinedelete():
    param = request.get_json() #json: animal_id, routine_name 

    animal_id = param['animal_id']
    routine_name = param['routine_name']

    del_routines = Routine.query.filter(and_(Routine.animal_id == animal_id, Routine.routine_name==routine_name))
                                        
    for del_r in del_routines:
        r = Routine.query.get(del_r.routine_id)
        db.session.delete(r)
        db.session.commit()

    return jsonify(param)

        
