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
    
    animals = Animal.query.filter_by(user_id = session['login']).all()
    animal_id = int(request.headers['animal_id'])

    ids = [animal.animal_id for animal in animals]

    if animal_id in ids:
        session['curr_animal'] = animal_id
 
    if request.method=="POST": #루틴 등록
    
        #json에서 각 값 임의 변수에 저장
        param = request.get_json()

        routine_id = param['routine_id']
        weekday = param['weekday']
        routine_name = param['routine_name']
        animal_id = param['animal_id']
        
        animal = Animal.query.filter_by(animal_id = animal_id).first()
        weekday = to_weekday(weekday)

        routine = Routine(routine_id=routine_id, animal=animal, routine_name=routine_name, weekday=weekday) 
        #db에 저장, 업데이트
        db.session.add(routine)
        db.session.commit()

        return "success"
        
    
    else: #get 루틴 불러오기
    
        animal_id = request.headers['animal_id']
        routines = Routine.query.filter(Routine.animal_id==animal_id).all()
        routines = query_to_dict(routines)
        return jsonify(routines)


#특정 루틴의 체크되어있던 요일 체크 해제 
@bp.route('/weekdaydelete', methods=['POST']) 
def weekdaydelete():
     #json: routine_name, animal_id, weekday
    
    #json에서 각 값 임의 변수에 저장
    param = request.get_json()

    routine_id = param['routine_id']
    weekday = param['weekday']
    routine_name = param['routine_name']
    animal_id = param['animal_id']

    del_date = to_weekday(weekday)

    #루틴 db 삭제
    
    del_routine = Routine.query.filter(and_(Routine.animal_id == animal_id, 
                                            Routine.routine_id==routine_id,
                                            Routine.weekday == del_date)).first() #체크였다가 체크해제된 row 탐색
    
    del_r = Routine.query.get(del_routine.index)
    db.session.delete(del_r)
    db.session.commit()   
    
    return "success"


#루틴 수정: 루틴 이름으로 수정은 안되고 아예 해당 루틴을 지우게끔 
@bp.route('/routinedelete', methods=['POST']) 
def routinedelete():
     #json:  animal_id, routine_name

    param = request.get_json()

    routine_id = param['routineId']
    animal_id =  param['animal_id']

    del_routines = Routine.query.filter(and_(Routine.animal_id == animal_id, 
                                            Routine.routine_id==routine_id))
                                        
    for del_r in del_routines:
        r = Routine.query.get(del_r.index)
        db.session.delete(r)
        db.session.commit()

    return "success"