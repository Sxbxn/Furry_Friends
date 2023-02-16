from flask import request, jsonify, session, Blueprint, url_for, redirect, g
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

from util import s3_connection, query_to_dict, upload_file_to_s3
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION, ALLOWED_EXTENSIONS


bp = Blueprint('authentification', __name__, url_prefix='/auth')


s3 = s3_connection()


# 세션에 로그인 기록이 있나 사용자를 확인하는 기능
@bp.before_app_request
def load_logged_in_user():
    username = session.get('login')
    if username is None:
        g.user = None
    else:
        g.user = db.session.query(User).filter(User.user_id == request.headers['user_id']).first()


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
            # session.clear()
            g.user = user_id
            session['login'] = user_id

            try:
                animal = Animal.query.filter_by(user_id = user_id).first()
                animal = query_to_dict(animal)
                session['curr_animal'] = animal['animal_id']

                if animal['neutered'] == 0:
                    animal['neutered'] = False
                else:
                    animal['neutered'] = True

                return jsonify(animal)

            except:
                resp = {"user_id":session['login'],
                        "animal_id":-999,
                        "animal_name":"",
                        "bday":"",
                        "sex":"",
                        "neutered":"",
                        "weight":0.0,
                        "image":""}

                return jsonify(resp)

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
    
    asd = session._get_current_object()

    req = request.headers['user_id']

    # 로그인 x
    if not asd['login']:
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
                extension = f.filename.split('.')[-1]
                if extension in ALLOWED_EXTENSIONS:
                    extension = '.' + extension

                    newname = session['login'] + '_' + animal_name + extension
                    img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
                    f.filename = newname

                    upload_file_to_s3(f)

                    image = img_url

                # 업로드된 파일이 이미지 파일이 아님
                else:
                    return "wrong file extension"
            else:
                image = ""

            animal = Animal(user, animal_name, bday, sex, neutered, weight, image)

            db.session.add(animal)
            db.session.commit()

            curr_animal = Animal.query.filter(and_(Animal.user_id == session['login'],
                                                Animal.animal_name == animal_name)).first()

            curr_animal = query_to_dict(curr_animal)

            if curr_animal['neutered'] == 0:
               curr_animal['neutered'] = False
            else:
                curr_animal['neutered'] = True

            return jsonify(curr_animal)
            # 등록된 동물 정보 json 반환, 이 뒤로 header에 animal_id 주고받기