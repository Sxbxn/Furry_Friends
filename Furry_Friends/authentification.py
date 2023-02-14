from flask import request, jsonify, session, Blueprint, url_for, redirect
from models import User, Animal
from connect_db import db
from sqlalchemy import and_
import json
import boto3
import datetime
from sqlalchemy import and_
from werkzeug.security import generate_password_hash, check_password_hash
from werkzeug.utils import secure_filename
import os
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION


bp = Blueprint('authentification', __name__, url_prefix='/auth')


def s3_connection():
    try:
        s3 = boto3.client(
            service_name="s3",
            region_name=AWS_S3_BUCKET_REGION,
            aws_access_key_id=AWS_ACCESS_KEY,
            aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
        )
        return s3

    except Exception as e:
        print(e)
        print('ERROR_S3_CONNECTION_FAILED') 


def query_to_dict(objs):
    try:
        lst = [obj.__dict__ for obj in objs]
        for obj in lst:
            del obj['_sa_instance_state']
        return lst
    except TypeError: # non-iterable
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst = [objs]
        return lst
        

s3 = s3_connection()


@bp.route('/register', methods=['GET','POST']) 
def register():

    if request.method=="POST":
        forms = request.get_json()

        user_id = forms['user_id']
        pw = generate_password_hash(forms['pw'])
        email = forms['email']
        vet = forms['vet']

        user = User(user_id = user_id, pw=pw, email=email, vet=vet)
        # 중복 검사
        check_email = User.query.filter(User.email==email).first()
        check_userid = User.query.filter(User.user_id==user_id).first()
        
        if check_userid:
            return "user id taken"

        if check_email:
            return "email already exists"

        db.session.add(user)  # id, pw(hash), email 변수에 넣은 회원정보 DB에 저장
        db.session.commit()  #커밋

        return "successfully registered"

    else: # GET
        return "registration form"


@bp.route('/login', methods=['GET','POST'])  
def login():
    if request.method=="POST":

        forms = request.get_json()

        user_id = forms['user_id']
        password = forms['pw']

         #유효성 검사
        user = User.query.filter(User.user_id == user_id).first()

        if not user:
            return "error - user not in db"
        
        elif not check_password_hash(user.pw, password):
            return "error - wrong pw"

        else:
            session.clear()
            session['login'] = user_id

            animal = query_to_dict(Animal.query.filter_by(user_id = user_id).first())
            session['curr_animal'] = animal[0]['animal_id']

            return jsonify(animal)

            # animal_list = Animal.query.filter(Animal.user_id==session['login']).all()

            # # 등록된 동물이 없음
            # if animal_list == []:
            #     return "no animal registered"

            # # 등록된 동물이 있음
            # else:
            #     animal_list = query_to_dict(animal_list)

            #     # 등록된 동물이 1마리 --> 바로 세션에 저장, json 반환
            #     if len(animal_list) == 1:
            #         session['curr_animal'] = animal_list['animal_id']
            #         return jsonify(animal_list)

            #     # 등록된 동물이 여러 마리 --> 선택 화면으로 이동
            #     else:
            #         return jsonify(animal_list)
            
            
    
    else: # GET
        if 'login' in session:
            return f"{session['login']}"
        else:
            return "login form"


@bp.route('/logout',methods=['GET'])
def logout():
    session.clear()
    return "logged out"


# app.py로 이동 ----- session['login'] 유무에 따라 라우팅
# @bp.route('/', methods=["GET"])
# def main():
#     if 'user_id' in session:  # session안에 user_id가 있으면 로그인
#         animal_list = Animal.query.filter(Animal.user_id==session['login']).all()
#         animal_list = query_to_dict(animal_list)
#         return f"{session['user_id']} is logged in"


    # return redirect(url_for('authentification.login')) 


@bp.route('/registerAnimal', methods=['GET','POST'])
def register_animal():
    
    # 로그인 x
    if 'login' not in session:
        return "not logged in"

    # 로그인 o
    else:

        if request.method=="GET":
            return "animal registration form"
                
        else: # POST
            param = request.form
            param = json.loads(param['data'])

            user = User.query.filter_by(user_id = session['login']).first()
            
            animal_name = param['animal_name']
            bday = param['bday']
            sex = param['sex']
            neutered = param['neutered']
            weight = param['weight']

            f = request.files['file']
            if f:
                newname = session['login'] + '_' + animal_name + ".png"

                imgpath = f"./static/{secure_filename(newname)}"
                f.save(imgpath) # 로컬에 저장

                s3.upload_file(imgpath, AWS_S3_BUCKET_NAME, newname) # s3에 업로드
                img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
                os.remove(imgpath) # 로컬에 저장된 파일 삭제

                image = img_url
            else:
                image = ""

            animal = Animal(user, animal_name, bday, sex, neutered, weight, image)

            db.session.add(animal)
            db.session.commit()

            curr_animal = Animal.query.filter(and_(Animal.user_id == session['login'],
                                                Animal.animal_name == animal_name)).first()

            curr_animal = query_to_dict(curr_animal)

            return jsonify(curr_animal)
            # 등록된 동물 정보 json 반환, 이 뒤로 header에 animal_id 주고받기
