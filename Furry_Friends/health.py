from flask import Flask, request, jsonify, session, Blueprint, render_template, redirect, url_for
from flask_sqlalchemy import SQLAlchemy
from pybo.model import Routine, ChecklistRoutine, ChecklistDefault, User, Animal, Health
from pybo.connect_db import db
import json
import boto3
import requests
from flask import flash
import datetime
from sqlalchemy import and_
from markupsafe import escape
from werkzeug.security import generate_password_hash, check_password_hash
from werkzeug.utils import secure_filename
import os
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION
from PIL import Image
from PIL import ImageFile
import numpy as np
import tensorflow as tf
from pybo.connect_s3 import s3_put_object
import logging
import binascii


#model 함수--------------------------------------------------------------------------
def padding(file):
    img = Image.open(file.stream)
    img = img.convert('RGB')
    w, h = img.size
    img = np.array(img)
    
    margin = [np.abs(w - h) // 2 , np.abs(w - h) // 2]
    if w > h:
        margin_list = [margin, [0,0]]
    else:
        margin_list = [[0,0], margin]
        
    if len(img.shape) == 3:
        margin_list.append([0,0])
        
    output = np.pad(img, margin_list, mode='constant')
    img = Image.fromarray(output)
    # 이미지 크기
    img = img.resize((224, 224))
    img = np.array(img)
    return img

def mk_img(file):
    np_img = padding(file)
    np_img = np.expand_dims(np_img, axis = 0)
    return np_img

def predict_result(model_path, img):
    model = tf.keras.models.load_model(model_path)
    result = model.predict(img)
    return result

#s3 ----------------------------------------------------------------------
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
    except TypeError: # non-iterable
        lst = []
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst.append(objs)
        return lst

def upload_file_to_s3(file):
    filename = secure_filename(file.filename)
    s3.upload_fileobj(
            file,
            AWS_S3_BUCKET_NAME,
            file.filename
            
    )

    # after upload file to s3 bucket, return filename of the uploaded file
    return file.filename

    
s3 = s3_connection()

# current_time = datetime.datetime.now()
# weekday = current_time.weekday()


bp = Blueprint('health', __name__, url_prefix='/health')


@bp.route('/records', methods=["GET"])
def records():

    health_records = Health.query.filter(and_(Health.user_id==session['login'],
                                      Health.animal_id==session['curr_animal'])).all()

    if health_records != []:
        health_records = query_to_dict(health_records)
        for i in range(len(health_records)):
            health_records[i]['content'] = str(health_records[i]['content'])

        return jsonify(health_records)
    else:
        return "no entry"
        

@bp.route('/content', methods=["GET"])
def record_content():
    
    record_index = request.headers['index']

    health_record = Health.query.get(int(record_index))
    health_record = health_record.__dict__
    del health_record['_sa_instance_state']
    health_record['content'] = str(health_record['content'])
    return jsonify(health_record)


@bp.route('/factory', methods=["GET","POST"])
def record_factory():

    if request.method=="GET":
        return "health record entry form"


    else: # POST
        
            record = request.form
            record = json.loads(record['data'])

            user = User.query.filter_by(user_id = session['login']).first()
            animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

            currdate = request.headers['currdate']
            kind = record['kind']
            affected_area = record['affected_area']

            f = request.files['file']

            if f:
                # predict.py 함수로 전처리 후 모델 돌리기
                # f 로 모델 돌려서 나온 값 db에 저장

                # 서버 내 모델 저장 경로
                cat_path = "C:\\Users\\Admin\\Desktop\\EYE_Model\\고양이_안구질환_DenseNet.h5"
                dog_path = "C:\\Users\\Admin\\Desktop\\EYE_Model\\개_안구질환_DenseNet121.h5"

                # 이미지 전처리
                img = mk_img(f)
                
                # 모델 결과 
                if kind == "cat":
                    result = predict_result(cat_path, img)
                else:
                    result = predict_result(dog_path, img)


                filename = secure_filename(f.filename)
                img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{filename}"
                image = img_url
                output = upload_file_to_s3(f)

                # 진단 결과
                content = result

                comment = "1" # 유저가 입력? or 피드백 받아오기?
                
                new_record = Health(animal=animal, user=user, content=content, image=image, currdate=currdate, kind=kind, comment=comment, affected_area=affected_area)  

                db.session.add(new_record)
                db.session.commit()

                return f"{result}"
            else:
                return "error - no image to diagnose"


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
