from flask import url_for, redirect, jsonify, request, session, Blueprint
import boto3
import datetime
from connect_db import db
from sqlalchemy import and_
from models import User, Animal, Health
import json
import predict

from werkzeug.utils import secure_filename
# from predict import padding, mk_img, predict_result

from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION

import os

bp = Blueprint('health', __name__, url_prefix='/health')

# s3 클라이언트 생성
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
        lst = []
        for obj in objs:
            obj = obj.__dict__
            del obj['_sa_instance_state']
            lst.append(obj)
        return lst
    except TypeError:
        lst = []
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst.append(objs)
        return lst


s3 = s3_connection()

@bp.route('/records', methods=["GET"])
def records():
    # 유저아이디, 동물아이디
    session['login'] = request.headers['user_id']
    session['curr_animal'] = request.headers['animal_id']

    health_records = Health.query.filter(and_(Health.user_id==session['login'],
                                            Health.animal_id==session['curr_animal'])).all()

    if health_records != []:
        health_records = query_to_dict(health_records)
        return jsonify(health_records)
    else:
        return "no entry"


@bp.route('/content', methods=["GET"])
def record_content():
    session['login'] = request.headers['user_id']
    session['curr_animal'] = request.headers['animal_id']

    record_index = request.headers['index']

    health_record = Health.query.get(int(record_index))
    health_record = health_record.__dict__
    del health_record['_sa_instance_state']

    return jsonify(health_record)


@bp.route('/factory', methods=["GET","POST"])
def record_factory():
    # 유저아이디, 동물아이디, 날짜
    session['login'] = request.headers['user_id']
    session['curr_animal'] = request.headers['animal_id']

    if request.method=="GET":
        return "health record entry form"


    else: # POST
        try:
            record = request.form
            record = json.loads(record['data'])

            user = User.query.filter_by(user_id = session['login']).first
            animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

            currdate = request.headers['currdate']
            kind = record['kind']
            affected_area = record['affected_area']

            f = request.files['file']

            if f:
                # predict.py 함수로 전처리 후 모델 돌리기
                # f 로 모델 돌려서 나온 값 db에 저장
                # 결과 나오는 데 지연됨 --> Lazy Loading View ?

                # 🔻 인공지능 팀에서 받은 predict.py와 app.py 🔻

                # 모델 서버 내 저장 경로
                # path = "./static/efficientNet_Ab01_224_128_0_001_half_frz.h5"

                # 이미지 전처리
                # img = mk_img(img_path)
                
                # 모델 결과 
                # result = predict_result(path, img)


                # -------------
                # 기존의 s3에 이미지 업로드 하는 코드
                # -------------
                newname = (str(datetime.datetime.now()).replace(":","")).replace(" ","_") + ".png"
                imgpath = f"./static/{secure_filename(newname)}"

                f.save(imgpath) # 로컬에 저장

                s3.upload_file(imgpath, AWS_S3_BUCKET_NAME, newname) # s3에 업로드
                img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"

                os.remove(imgpath) # 로컬에 저장된 파일 삭제

                image = img_url

                # 진단 결과
                # content = 

                # 진단 결과에 따른 피드백 (ex 질병 유의사항, 증상 등)
                # comment = 

            else:
                return "error - no image to diagnose"

            new_record = Health(animal, user, image, currdate, kind, affected_area) # content, comment 포함

            db.session.add(new_record)
            db.session.commit()

            return "successfully created health record"
        except:
            return "failed to create health record"


@bp.route('/delete', methods=["DELETE"])
def record_delete():
    record_index = int(request.headers['index'])
    deleting_record = Health.query.get(record_index)

    s3.delete_object(
                    Bucket = AWS_S3_BUCKET_NAME,
                    Key = (deleting_record.image).split('/')[-1]
                    )

    db.session.delete(deleting_record)
    db.session.commit()
    
    return "record successfully removed"
