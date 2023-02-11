from flask import session, request, jsonify, redirect, Blueprint, url_for
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import and_


# for checklist
from models import Animal, Routine, ChecklistDefault, ChecklistRoutine
import datetime
from connect_db import db


bp = Blueprint('checklist', __name__, url_prefix='/check')


def query_to_dict(objs):
    try:
        lst = []
        for obj in objs:
            obj = obj.__dict__
            del obj['_sa_instance_state']
            lst.append(obj)
        return lst
    except TypeError: # non-iterable
        objs = objs.__dict__
        del objs['_sa_instance_state']
        return objs


@bp.route('/checklist', methods=["GET", "POST"])
def checklist():

    # 유저아이디, 동물아이디, 날짜, 요일
    session['login'] = request.headers['user_id']
    session['curr_animal'] = request.headers['animal_id']

    currdate = request.headers['currdate']
    current_weekday_num = request.headers['weekday']

    if request.method=="GET":
        checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == session['curr_animal'], 
                                                                ChecklistDefault.currdate == currdate)).first()
        routines = Routine.query.filter(and_(Routine.animal_id == session['curr_animal'], 
                                            Routine.weekday == current_weekday_num)).all()

        # routine이 없음
        if routines == []:
            # checklist_default 기록이 있음 --> 수정으로 넘어감
            if checklist_default != None:
                return redirect(url_for('checklist.checklist_update'))

            # checklist_default 기록이 없음 --> 입력 폼만 반환
            else: 
                return "checklist_default form"


        # routine이 있음
        else: 
            # checklist_default 기록이 있음 --> 수정으로 넘어감
            if checklist_default != None:
                return redirect(url_for('checklist.checklist_update'))

            # checklist_default, checklist_routine 기록이 없음 --> routine json, 입력 폼 반환
            else:
                today_routines = query_to_dict(routines)
                return jsonify(today_routines)
   

    else: # POST

        currdate = request.headers['currdate']
        current_weekday_num = request.headers['weekday']

        routines = Routine.query.filter(and_(Routine.animal_id == session['curr_animal'], 
                                            Routine.weekday == current_weekday_num)).all()

        param = request.get_json() 

        # routine이 없음
        if routines == []:
            # checklist_default
            animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

            food = param['food']
            bowels = param['bowels']
            note = param['note']

            new_cd = ChecklistDefault(currdate, animal, food, bowels, note)
            db.session.add(new_cd)

        # routine이 있음
        else:
            # checklist_default
            animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

            json_routines = param['routines']
            food = param['food']
            bowels = param['bowels']
            note = param['note']

            new_cd = ChecklistDefault(currdate, animal, food, bowels, note)
            db.session.add(new_cd)

            # checklist_routine
            j = 0
            for r in routines:
                j += 1

            for i in range(j):
                json_routine = json_routines[f'routine{i+1}']

                routine = Routine.query.filter_by(routine_id = json_routine['routine_id']).first()

                # animal_id = session['curr_animal']
                routine_name = json_routine['routine_name']
                status = json_routine['status']

                new_cr = ChecklistRoutine(currdate, animal, routine, routine_name, status)
                db.session.add(new_cr)      
        
        db.session.commit()

    return "checklist succefully created"


@bp.route('/update', methods=["GET", "POST"])
def checklist_update():

    # 유저아이디, 동물아이디, 날짜, 요일
    session['login'] = request.headers['user_id']
    session['curr_animal'] = request.headers['animal_id']

    currdate = request.headers['currdate']
    current_weekday_num = request.headers['weekday']


    if request.method=="GET": # GET
        currdate = request.headers['currdate']
        checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == session['curr_animal'], 
                                                                ChecklistDefault.currdate == currdate)).first()
        checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == session['curr_animal'], 
                                                                ChecklistRoutine.currdate == currdate)).all()
        
        # checklist_default만 있음
        if checklists_routine == []:
            checklist_d = query_to_dict(checklist_default)
            return jsonify(checklist_d)

        # 둘 다 있음
        else: 
            checklist_d = query_to_dict(checklist_default)
            checklist_r = query_to_dict(checklists_routine)
            return jsonify(checklist_d, checklist_r)

    else: # POST

        currdate = request.headers['currdate']
        param = request.get_json() 
        checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == session['curr_animal'], 
                                                                ChecklistDefault.currdate == currdate)).first()
        checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == session['curr_animal'], 
                                                                ChecklistRoutine.currdate == currdate)).all()

        # checklist_default만 수정
        if checklists_routine == []:
            food = param['food']
            bowels = param['bowels']
            note = param['note']

            checklist_default.food = food
            checklist_default.bowels = bowels
            checklist_default.note = note

            db.session.commit()      
        
        # 둘 다 수정
        else:
            food, bowels, note = param['food'], param['bowels'], param['note']

            checklist_default.food = food
            checklist_default.bowels = bowels
            checklist_default.note = note
            db.session.commit()  

            json_routines = param['routines']

            j = 0
            for r in checklists_routine:
                j += 1

            for i in range(j):
                routine = json_routines[f'routine{i+1}']

                checklists_routine[i].status = routine['status']

                db.session.commit()
    
    return jsonify(param)