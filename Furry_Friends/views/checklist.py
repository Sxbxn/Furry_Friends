from flask import session, request, jsonify, redirect, Blueprint, url_for
from sqlalchemy import and_

from Furry_Friends.util import query_to_dict
from Furry_Friends.models import Animal, Routine, ChecklistDefault, ChecklistRoutine
from Furry_Friends.connect_db import db


bp = Blueprint('checklist', __name__, url_prefix='/check')


@bp.route('/checklist', methods=["GET", "POST"])
def checklist():

    asd = session._get_current_object()

    if 'login' in asd:

        animal_id = int(request.headers['animal_id'])
        animals = Animal.query.filter_by(user_id = asd['login']).all()
        ids = [animal.animal_id for animal in animals]

        if animal_id in ids:
            session['curr_animal'] = animal_id

            if request.method=="GET":

                currdate = request.headers['currdate']

                checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == asd['curr_animal'], 
                                                                    ChecklistDefault.currdate == currdate)).first()
                checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == asd['curr_animal'], 
                                                                    ChecklistRoutine.currdate == currdate)).all()

                checklist_default = query_to_dict(checklist_default)
                checklists_routine = query_to_dict(checklists_routine)

                if checklist_default == []:
                    checklist_default = None
                
                if checklists_routine == []:
                    checklists_routine = None

                dictionary = {"default":checklist_default,
                            "routine":checklists_routine}

                # 기본 체크리스트, 루틴 체크리스트 검색해서 불러옴
                # 없으면 json 안에 빈 리스트
                return jsonify(dictionary)
            
            else: # POST
                param = request.get_json() 

                animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

                currdate = param['currdate']
                date = currdate.split(" ")
                currdate = date[0]
                current_weekday_num = date[1] # str "토요일"
                food = param['food']
                bowels = param['bowels']
                note = param['note']

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

                new_cd = ChecklistDefault(currdate, animal, food, bowels, note)
                db.session.add(new_cd)

                try:
                    # 전달받은 루틴이 있을 때
                    json_routines = param['routines']

                    j = 0
                    for r in routines:
                        j += 1
                    print(j)

                    for i in range(j):

                        json_routine = json_routines[i]
                        print(json_routine)
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

        return "no animal registered"
        
    return "not logged in"


@bp.route('/checklist/all', methods=["GET"])
def checklistAll():

    asd = session._get_current_object()

    if 'login' in asd:

        animal_id = int(request.headers['animal_id'])
        animals = Animal.query.filter_by(user_id = asd['login']).all()
        ids = [animal.animal_id for animal in animals]

        if animal_id in ids:
            session['curr_animal'] = animal_id

            if request.method=="GET":

                checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == asd['curr_animal'])).all()

                checklist_default = query_to_dict(checklist_default)

                if checklist_default == []:
                    checklist_default = None

                dictionary = {"default":checklist_default}

                return jsonify(dictionary)

        return "no animal registered"

    return "not logged in"