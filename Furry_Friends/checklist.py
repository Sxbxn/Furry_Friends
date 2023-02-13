from flask import session, request, jsonify, redirect, Blueprint, url_for
from sqlalchemy import and_

from models import Animal, Routine, ChecklistDefault, ChecklistRoutine
from connect_db import db


bp = Blueprint('checklist', __name__, url_prefix='/check')


def query_to_dict(objs):
    try:
        lst = [obj.__dict__ for obj in objs]
        for obj in lst:
            del obj['_sa_instance_state']
        return lst
    except TypeError: # non-iterable
        if objs == None:
            return []
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst = [objs]
        return lst


@bp.route('/checklist', methods=["GET", "POST"])
def checklist():
    # 로그인 x시
    if 'login' not in session:
        return "not logged in"
    
    # 로그인 O --> curr_animal도 세션에 O
    else:
        currdate = request.headers['currdate']
        current_weekday_num = request.headers['weekday']

        if request.method=="GET":

            routines = Routine.query.filter(and_(Routine.animal_id == session['curr_animal'], 
                                            Routine.weekday == current_weekday_num)).all()
            checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == session['curr_animal'], 
                                                                ChecklistDefault.currdate == currdate)).first()
            checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == session['curr_animal'], 
                                                                ChecklistRoutine.currdate == currdate)).all()

            routines = query_to_dict(routines)
            checklist_default = query_to_dict(checklist_default)
            checklists_routine = query_to_dict(checklists_routine)

            # 루틴, 기본 체크리스트, 루틴 체크리스트 검색해서 불러옴
            # 없으면 json 안에 빈 리스트
            return jsonify(routines, checklist_default, checklists_routine)
        
        else: # POST

            checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == session['curr_animal'], 
                                                                ChecklistDefault.currdate == currdate)).first()
            checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == session['curr_animal'], 
                                                                ChecklistRoutine.currdate == currdate)).all()

            # 이미 기록이 있는 경우, 기존 기록 삭제
            try:
                db.session.delete(checklist_default)
                db.session.delete(checklists_routine)
            except:
                pass

            param = request.get_json() 

            animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

            food = param['food']
            bowels = param['bowels']
            note = param['note']

            new_cd = ChecklistDefault(currdate, animal, food, bowels, note)
            db.session.add(new_cd)

            try:
                # 전달받은 루틴이 있을 때
                json_routines = param['routines']

                j = 0
                for r in routines:
                    j += 1

                for i in range(j):
                    json_routine = json_routines[f'routine{i+1}']

                    routine = Routine.query.filter_by(routine_id = json_routine['routine_id']).first()

                    routine_name = json_routine['routine_name']
                    status = json_routine['status']

                    new_cr = ChecklistRoutine(currdate, animal, routine, routine_name, status)
                    db.session.add(new_cr)      
            
            except:
                # 전달받은 루틴이 없음
                db.session.commit()

            return "checklist created"
