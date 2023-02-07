from flask import Flask, request, jsonify, session, Blueprint, render_template, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
from pybo.model import Routine, ChecklistRoutine, ChecklistDefault, User
from pybo.connect_db import db
import json
from flask import flash
import datetime
from sqlalchemy import and_
from markupsafe import escape
from werkzeug.security import generate_password_hash, check_password_hash





bp = Blueprint('main', __name__, url_prefix='/')
# current_time = datetime.datetime.now()
# weekday = current_time.weekday()

def to_weekday(num):
    weekdays = ['mon','tue','wed','thu','fri','sat','sun']
    num = int(num)
    return weekdays[num]

# -----------------
# from db.query to dictionary (in list)
# -----------------
def query_to_dict(objs):
    try:
        lst = []
        for obj in objs:
            obj = obj.__dict__
            del obj['_sa_instance_state']
            lst.append(obj)
        return lst
    except TypeError:
        return None


# -----------------
# json to ckl_d
# -----------------
def json_to_new_cd(js):
    currdate = datetime.datetime.now().date()
    animal_id = js['animal_id']
    food = js['food']
    bowels = js['bowels']
    note = js['note']

    new_cd = ChecklistDefault(currdate, animal_id, food, bowels, note)

    return new_cd


# ----------------
# json to ckl_r
# ----------------
def json_to_new_cr(animal_id, routine):
    currdate = datetime.datetime.now().date()
    animal_id = animal_id
    routine_id = routine['routine_id']
    routine_name = routine['routine_name']
    status = routine['status']

    new_cr = ChecklistRoutine(currdate, animal_id, routine_id, routine_name, status)

    return new_cr

# -----------------
# from db.query to dictionary (in list)
# -----------------
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
        

# -----------------
# update checklist_default
# -----------------
def update_cd(js, record):
    food = js['food']
    bowels = js['bowels']
    note = js['note']

    record.food = food
    record.bowels = bowels
    record.note = note

    return record


def update_cr(checklists_routine, json_routines, i):
    routine = json_routines[f'routine{i+1}']

    routine_id = routine['routine_id']
    routine_name = routine['routine_name']
    status = routine['status']

    checklists_routine[i].routine_id = routine_id
    checklists_routine[i].routine_name = routine_name
    checklists_routine[i].status = status
    
    return checklists_routine[i]


#------------------------------------------------------------------------------------------------------
@bp.route('/register', methods=['GET','POST']) #GET(정보보기), POST(정보수정) 메서드 허용
def register():
    
    if request.method=="POST":
        forms = request.get_json()
        userid = forms['userid']
        email = forms['email']
        password = forms['password']

        user = User(user_id = userid,
                        pw=generate_password_hash(password), email=email
                        )
        #유효성 검사
        check_email = User.query.filter(User.email==email).first()
        check_userid = User.query.filter(User.userid==userid).first()
        
        if check_userid:
            flash("사용 중인 아이디입니다.", category='error')
            return redirect(url_for('register'))
        if check_email:
            flash("이미 가입된 이메일입니다.", category='error')
            return redirect(url_for('register'))

         

        db.session.add(user)  # id, name 변수에 넣은 회원정보 DB에 저장
        db.session.commit()  #커밋
        flash("가입완료")
        return redirect(url_for('login'))

    

@bp.route('/login', methods=['GET','POST'])  
def login():
    if request.method=="POST":
        forms = request.get_json()
        userid = forms['userid']
        email = forms['email']
        password = forms['password'] 
         #로그인 폼 생성
         #유효성 검사
         
        
        user = User.query.filter(User.user_id == userid).first()
        if not user:
            error = "존재하지 않는 사용자입니다."
        elif not check_password_hash(userid, password):
            error = "비밀번호가 올바르지 않습니다."
        if error is None:
            session.clear()
            session['userid'] = userid #form에서 가져온 userid를 session에 저장
            
            return jsonify(forms)
            
        flash(error)
        
        # return redirect('/') #로그인에 성공하면 홈화면으로 redirect
            
    return jsonify(forms) #보류 아마도 animal쪽으로..?

@bp.route('/logout',methods=['GET'])
def logout():
    session.pop('userid',None)
    return redirect(url_for('main'))

@bp.route('/')
def main():
    if 'userid' in session:  # session안에 userid가 있으면 로그인
        return '로그인 성공! 아이디는 %s' % escape(session['userid']) + \
            "<br><a href = '/logout'>로그아웃</a>"

    return "로그인 해주세요. <br><a href = '/login'> 로그인 하러가기! </a>" # 로그인이 안될 경우

#-------------------------------------------------------------------------------------------------------
# @bp.route('/animal', methods=['Post'])
# def animal():
#     param = request.get_json() 
#     session['animal_id'] = param['animal_id'] 
    
#     return jsonify(param) 

#새 루틴 등록
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

        

#---------------------------------------------------------------------------------------------------------------------
# -----------------
# from number to weekday
# -----------------


@bp.route('/checklist', methods=["GET", "POST"])
def checklist():

    # 임의로 설정한 user & animal, 나중에 삭제
    session['login'] = 'test'
    session['curr_animal'] = 1


    # user, animal, today's date, weekday(num)
    current_user = session['login']
    current_animal = session['curr_animal']
    current_date = datetime.datetime.now().date()
    current_weekday_num = str(datetime.datetime.now().weekday())


    if request.method=="GET": # GET
    
        checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == current_animal, 
                                                                ChecklistDefault.currdate == current_date)).first()
        checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == current_animal, 
                                                                ChecklistRoutine.currdate == current_date)).all()
        routines = Routine.query.filter(and_(Routine.animal_id == current_animal, Routine.weekday == current_weekday_num)).all()


        # routine이 없음 --> default만 
        if routines == []:
            # checklist_default 기록이 있음 --> 수정으로 넘어감
            if checklist_default != None:
                return redirect(url_for('checklist_update'))

            # checklist_default 기록이 없음 --> 입력 폼만 반환
            else: 
                return "checklist_default form"


        # routine이 있음 --> default와 routine 둘 다
        else: 
            # checklist_default 기록이 있음 
            # (== 자동으로 checklist_routine 기록도 있음) --> 수정으로 넘어감
            if checklist_default != None:
                # today_routines = query_to_dict(routines)
                # checklist_d = query_to_dict(checklist_default)
                # checklist_r = query_to_dict(checklists_routine)
                return redirect(url_for('checklist_update'))

            # checklist_default, checklist_routine 기록이 없음 --> routine json, 입력 폼 반환
            else:
                today_routines = query_to_dict(routines)
                return jsonify(today_routines)
   

    else: # POST

        routines = Routine.query.filter(and_(Routine.animal_id == current_animal, Routine.weekday == current_weekday_num)).all()
        checks = request.get_json() 

        # 기록된 routine이 없음 --> checklist_default만 기록
        if routines == []:
            new_cd = json_to_new_cd(checks)
            db.session.add(new_cd)

        # 기록된 routine이 있음 --> checklist_default와 checklist_routine 기록
        else:
            json_routines = checks['routines']

            new_cd = json_to_new_cd(checks)
            db.session.add(new_cd)

            j = 0
            for r in routines:
                j += 1

            for i in range(j):
                routine = json_routines[f'routine{i+1}']
                new_cr = json_to_new_cr(checks['animal_id'], routine)
                db.session.add(new_cr)      
        
        db.session.commit()

    return jsonify(checks)        


@bp.route('/update', methods=["GET", "POST"])
def checklist_update():

    # 임의로 설정한 user & animal, 나중에 삭제
    session['login'] = 'test'
    session['curr_animal'] = 1


    # user, animal, today's date, weekday(num)
    current_user = session['login']
    current_animal = session['curr_animal']
    current_date = datetime.datetime.now().date()
    current_weekday_num = str(datetime.datetime.now().weekday())


    if request.method=="GET": # GET
    
        routines = Routine.query.filter(and_(Routine.animal_id == current_animal, Routine.weekday == current_weekday_num)).all()
        checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == current_animal, 
                                                                ChecklistDefault.currdate == current_date)).first()
        checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == current_animal, 
                                                                ChecklistRoutine.currdate == current_date)).all()
        

        # routine이 없음 
        if routines == []:
            # checklist_default가 있는 경우에만 redirect됨
            checklist_d = query_to_dict(checklist_default)
            
            return jsonify(checklist_d)


        # routine이 있음
        else: 
            # checklist_default, checklist_routine이 이미 있는 경우에 redirect됨
            # # routines, checklist_default와 checklist_routine 반환
            checklist_d = query_to_dict(checklist_default)
            checklist_r = query_to_dict(checklists_routine)
 
            return jsonify(checklist_d, checklist_r)
   

    else: # POST

        checklist_default = ChecklistDefault.query.filter(and_(ChecklistDefault.animal_id == current_animal, 
                                                                ChecklistDefault.currdate == current_date)).first()
        checklists_routine = ChecklistRoutine.query.filter(and_(ChecklistRoutine.animal_id == current_animal, 
                                                                ChecklistRoutine.currdate == current_date)).all()                                                        
        
        checks = request.get_json() 
        currdate = datetime.datetime.now().date()
        animal_id = checks['animal_id']

        # checklist_default만 있을 때
        if checklists_routine == []:
            update_cd(checks, checklist_default)
            db.session.commit()      
        
        else:
        # checklist_routine update
            update_cd(checks, checklist_default)
            db.session.commit()  

            json_routines = checks['routines']

            j = 0
            for r in checklists_routine:
                j += 1

            for i in range(j):
                update_cr(checklists_routine, json_routines, i)
                db.session.commit()
    
    return jsonify(checks)

      


# if __name__ == "__main__":
#     app.run()
