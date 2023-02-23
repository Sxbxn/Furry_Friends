from flask import request, jsonify, session, Blueprint, render_template, make_response
from sqlalchemy import and_
import json
from sqlalchemy import and_
from werkzeug.security import generate_password_hash, check_password_hash
import datetime

from Furry_Friends.util import s3_connection, query_to_dict, upload_file_to_s3, int_to_bool
from Furry_Friends.models import User, Animal
from Furry_Friends.connector import db
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION


bp = Blueprint('authentification', __name__, url_prefix='/auth')


s3 = s3_connection()


@bp.route('/register', methods=['POST']) 
def register():
    
        forms = request.get_json()

        user_id = forms['user_id']
        pw = generate_password_hash(forms['pw'])
        email = forms['email']
        vet = forms['vet']

        user = User(user_id=user_id, pw=pw, email=email, vet=vet)
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


@bp.route('/login', methods=['POST', "GET"])  
def login():
    if request.method=="POST":

        forms = request.get_json()

        user_id = forms['user_id']
        password = forms['pw']

         #유효성 검사
        user = User.query.filter(User.user_id == user_id).first()

        if not user:
            resp = {"user_id":"user unregistered",
                        "animal_id":-999,
                        "animal_name":"",
                        "bday":"",
                        "sex":"",
                        "neutered":"",
                        "weight":0.0,
                        "image":""}
            
            return jsonify(resp)
        
        elif not check_password_hash(user.pw, password):
            resp = {"user_id":"wrong password",
                        "animal_id":-999,
                        "animal_name":"",
                        "bday":"",
                        "sex":"",
                        "neutered":"",
                        "weight":0.0,
                        "image":""}

            return jsonify(resp)

        else:
            # session.clear()
            session['login'] = user_id

            try:
                animal = Animal.query.filter_by(user_id = user_id).first()
                animal = query_to_dict(animal)

                session['curr_animal'] = animal['animal_id']

                animal['vet'] = user.vet

                animal = int_to_bool(animal)
                resp = make_response(jsonify(animal))

                expire_date = datetime.datetime.now() + datetime.timedelta(days=90)
                resp.set_cookie('login', user_id, expires=expire_date)

                return resp

            except:
                
                obj = {"user_id":session['login'],
                        "animal_id":-999,
                        "animal_name":"",
                        "bday":"",
                        "sex":"",
                        "neutered":"",
                        "weight":0.0,
                        "image":"",
                        "vet": user.vet}
                
                resp = make_response(jsonify(obj))

                expire_date = datetime.datetime.now() + datetime.timedelta(days=90)
                resp.set_cookie('login', user_id, expires=expire_date)

                return resp
            
    else: # GET
        return render_template('sign.html')


@bp.route('/logout',methods=['GET'])
def logout():
    session.clear()
    resp = make_response("logged out")
    resp.set_cookie('login','',expires=0)
    return resp


@bp.route('/withdrawal', methods=['DELETE'])
def withdrawal():
    
    asd = session._get_current_object()
    print(asd)

    if 'login' in asd:
        user = User.query.filter_by(user_id=asd['login']).first()

        db.session.delete(user)
        db.session.commit()

        session.clear()
        resp = make_response("user deleted")
        resp.set_cookie('login','',expires=0)

        return resp
    else:
        return "not authorized"

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

            extension = '.' + f.filename.split('.')[-1]

            newname = session['login'] + '_' + animal_name + extension
            img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
            f.filename = newname

            upload_file_to_s3(f)

            image = img_url

            animal = Animal(user, animal_name, bday, sex, neutered, weight, image)

            db.session.add(animal)
            db.session.commit()

            curr_animal = Animal.query.filter(and_(Animal.user_id == asd['login'],
                                                Animal.animal_name == animal_name)).first()

            curr_animal = query_to_dict(curr_animal)
            curr_animal = int_to_bool(curr_animal)
           
            return jsonify(curr_animal)
            # 등록된 동물 정보 json 반환, 이 뒤로 header에 animal_id 주고받기