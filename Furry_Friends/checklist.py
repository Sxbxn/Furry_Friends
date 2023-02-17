from flask import session, request, jsonify, redirect, Blueprint, url_for
from sqlalchemy import and_

from util import query_to_dict
from models import Animal, Routine, ChecklistDefault, ChecklistRoutine
from connect_db import db


bp = Blueprint('checklist', __name__, url_prefix='/check')


@bp.route('/checklist', methods=["GET", "POST"])
def checklist():
    # 로그인 x시
    if 'login' not in session:
        return "not logged in"
    
    # 로그인 O --> curr_animal도 세션에 O
    else:
        currdate = param['currdate']
        currdate = currdate.split(" ")[0]
        # print(currdate)
        current_weekday_num = param['weekday']

        if request.method=="GET":

#             routines = Routine.query.filter(and_(Routine.animal_id == session['curr_animal'], 
#                                             Routine.weekday == current_weekday_num)).all()
            checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == session['curr_animal'], 
                                                                ChecklistDefault.currdate == currdate)).first()
            checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == session['curr_animal'], 
                                                                ChecklistRoutine.currdate == currdate)).all()

#             routines = query_to_dict(routines)
            checklist_default = query_to_dict(checklist_default)
            checklists_routine = query_to_dict(checklists_routine)

            # 기본 체크리스트, 루틴 체크리스트 검색해서 불러옴
            # 없으면 json 안에 빈 리스트
            return jsonify(checklist_default, checklists_routine)
        
        else: # POST

            routines = Routine.query.filter(and_(Routine.animal_id == session['curr_animal'], 
                                            Routine.weekday == current_weekday_num)).all()
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

                    routine = Routine.query.filter_by(index = json_routine['index']).first()

                    routine_id = json_routine['routine_id']
                    routine_name = json_routine['routine_name']
                    status = json_routine['status']

                    new_cr = ChecklistRoutine(currdate, animal, routine, routine_id, routine_name, status)

                    db.session.add(new_cr)      

                db.session.commit()

            except:
                # 전달받은 루틴이 없음
                db.session.commit()

            return "checklist created"