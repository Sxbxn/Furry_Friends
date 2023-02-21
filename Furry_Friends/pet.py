from flask import request, jsonify, session, Blueprint, url_for, redirect, g
from werkzeug.utils import secure_filename
import os
from sqlalchemy import and_
import boto3
import json


from util import s3_connection, query_to_dict, upload_file_to_s3, int_to_bool
from connect_session import sess
from connect_db import db
from models import User, Animal
from config import AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION, AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, ALLOWED_EXTENSIONS


bp = Blueprint("pet", __name__, url_prefix="/pet")


s3 = s3_connection()


@bp.route('/management', methods=['GET'])
def management():

    asd = session._get_current_object()
    req = request.headers['user_id']

    print(asd)
    user_id = request.cookies.get('login')
    test = request.cookies.get('session')

    # 해당 아이디로 등록한 동물 전부
    if asd['login'] == req:
    # if user_id:
        animal_list = Animal.query.filter(Animal.user_id==asd['login']).all()
        animal_list = query_to_dict(animal_list)

        if animal_list == []:

            # resp = {"user_id":session['login'],
            #             "animal_id":-999,
            #             "animal_name":"",
            #             "bday":"",
            #             "sex":"",
            #             "neutered":"",
            #             "weight":0.0,
            #             "image":""}

            return jsonify([])
        
        else:
            for animal in animal_list:
                int_to_bool(animal)

            return jsonify(animal_list) 

    else:
        return jsonify()



@bp.route('/profile', methods=["GET"])
def profile():
    
    user_id = request.cookies.get('login')
    test = request.cookies.get('session')

    print(user_id)
    print(test)

    if user_id:

        # 유저에게 등록된 동물들 id에 헤더로 받은 동물 id가 포함되어 있는지 확인
        # 있으면 헤더로 받은 동물 세션에 저장, 동물 정보 json 반환
        animals = Animal.query.filter_by(user_id = user_id).all()
        animal_id = int(request.headers['animal_id'])

        ids = [animal.animal_id for animal in animals]

        if animal_id in ids:
            session['curr_animal'] = animal_id
            curr_animal = Animal.query.get(animal_id)
            curr_animal = query_to_dict(curr_animal)

            int_to_bool(curr_animal)

            return jsonify(curr_animal)

        else:
            return "wrong request"

    # 로그인 x
    else:
        return "not logged in"


@bp.route('/update', methods=["GET","PUT"])
def info_update():

    asd = session._get_current_object()
    # 수정할 동물 id header로 받음
    animal_id = int(request.headers['animal_id'])

    # 세션과 일치 시
    if asd['curr_animal'] == animal_id:

        # 동물 정보 수정 페이지 접근
        if request.method == "GET":
            animal = Animal.query.filter_by(animal_id = asd['curr_animal']).first()

            animal = query_to_dict(animal)
            return jsonify(animal)  

        # 수정 정보 전달 시
        # 수정사항 한번에 전송받음
        else: # PUT
            animal = Animal.query.filter_by(animal_id = asd['curr_animal']).first()

            # 이미지 업로드
            f = request.files['file']

            changes = request.form
            changes = json.loads(changes['data'])

            animal.animal_name = changes['animal_name']
            animal.bday = changes['bday']
            animal.sex = changes['sex']
            animal.neutered = changes['neutered']
            animal.weight = changes['weight']

            # 기존의 이미지 s3에서 삭제, 새 이미지 등록
            s3.delete_object(
                Bucket = AWS_S3_BUCKET_NAME,
                Key = (animal.image).split('/')[-1]
            )

            extension = '.' + f.filename.split('.')[-1]

            newname = session['login'] + '_' + animal.animal_name + extension
            img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
            f.filename = newname

            upload_file_to_s3(f)

            animal.image = img_url

            db.session.commit()

            return "successfully updated"
    else:
        return "unauthorized"


@bp.route('/delete', methods=["DELETE"])
def pet_delete():
    
    asd = session._get_current_object()
    # 삭제할 동물 id header로 받음
    animal_id = int(request.headers['animal_id'])

    # 세션에 동물과 일치 시
    if asd['curr_animal'] == animal_id:
        
        try:
            animal = Animal.query.filter_by(animal_id = asd['curr_animal']).first()
            
            # 프로필 이미지 s3에서 삭제
            try:
                s3.delete_object(
                    Bucket = AWS_S3_BUCKET_NAME,
                    Key = (animal.image).split('/')[-1]
                )

            # 기존에 이미지가 없었던 경우 -- pass
            except: 
                pass

            # db에서 animal 삭제
            db.session.delete(animal)
            
            db.session.commit()

            animal = Animal.query.filter_by(user_id = asd['login']).first()
            if animal is None:
                resp = {"user_id": asd['login'],
                        "animal_id":-999,
                        "animal_name":"",
                        "bday":"",
                        "sex":"",
                        "neutered":"",
                        "weight":0.0,
                        "image":""}

                return jsonify(resp)
            else:
                animal = query_to_dict(animal)

                int_to_bool(animal)
                
                return jsonify(animal)

        except:
            return "removal failed"
    
    else:
        return "unauthorized"


@bp.route('/AIprofile', methods=["POST"])
def ai_profile():

    asd = session._get_current_object()

    if 'login' not in asd:
        return "not logged in"
    
    else:
        animal = Animal.query.filter_by(animal_id = asd['curr_animal']).first()

        f = request.files['file']

        extension = '.' + f.filename.split('.')[-1] # ai 모델 돌리면 나오는 이미지 형식이 고정인지 확인 후 수정
        newname = asd['login'] + '_' + animal.animal_name + "_aiprofile" + extension
        f.filename = newname
        f.save(f'.\samples\inputs\{newname}')

        os.system("python profile_process.py --checkpoint ./weights/generator_celeba_distill.pt --device cpu")

        s3.upload_file(Filename=f"./samples/results/{newname}", Bucket=AWS_S3_BUCKET_NAME, Key=newname)
        img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"

        os.remove(f".\samples\inputs\{newname}")
        os.remove(f".\samples\\results\{newname}")

        animal.image = img_url

        db.session.commit()

        return img_url



