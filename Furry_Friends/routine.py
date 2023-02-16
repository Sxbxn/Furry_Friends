from flask import request, jsonify, session, Blueprint, g
from sqlalchemy import and_

from util import query_to_dict, to_weekday
from models import User, Animal, Routine
from connect_db import db


bp = Blueprint('routine', __name__, url_prefix='/routine')


@bp.before_app_request
def load_logged_in_user():
    username = session.get('login')
    if username is None:
        g.user = None
    else:
        g.user = db.session.query(User).filter(User.user_id == request.headers['user_id']).first()


@bp.route('/routine', methods=['GET','POST']) 
def routine():
    
 
    if request.method=="POST": #루틴 등록
         #json: 
    
        #json에서 각 값 임의 변수에 저장
        param = request.get_json()

        routine_id = param['routineId']
        weekday = param['weekDay']
        routine_name = param['routineName']
        animal_id = param['animalId']
        
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
    param = request.get_json()

    routine_id = param['routineId']
    weekday = param['weekDay']
    routine_name = param['routineName']
    animal_id = param['animalId']

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

    param = request.get_json()

    routine_id = param['routineId']
    animal_id =  param['animalId']

    del_routines = Routine.query.filter(and_(Routine.animal_id == animal_id, Routine.routine_id==routine_id))
                                        
    for del_r in del_routines:
        r = Routine.query.get(del_r.index)
        db.session.delete(r)
        db.session.commit()

    return "success"
    # return redirect(url_for('routine.routine'))
