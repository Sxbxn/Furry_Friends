from flask import url_for, redirect, jsonify, request, session, Blueprint
from connect_db import db
from sqlalchemy import and_
from PIL import Image

import json

from models import User, Animal, Health
from util import s3_connection, query_to_dict, upload_file_to_s3
from werkzeug.utils import secure_filename
from predict import padding, mk_img, predict_result


from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION
import os

bp = Blueprint('health', __name__, url_prefix='/health')


s3 = s3_connection()

@bp.route('/records', methods=["GET"])
def records():

    animals = Animal.query.filter_by(user_id = session['login']).all()
    animal_id = int(request.headers['animal_id'])

    ids = [animal.animal_id for animal in animals]

    if animal_id in ids:
        session['curr_animal'] = animal_id

    health_records = Health.query.filter(and_(Health.user_id==session['login'],
                                      Health.animal_id==session['curr_animal'])).all()

    if health_records != []:
        health_records = query_to_dict(health_records)
        for i in range(len(health_records)):
            health_records[i]['comment'] = str(health_records[i]['comment'])

        return jsonify(health_records)
    else:
        return []
        

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

            currdate = record['currdate']
            currdate = currdate.split(" ")[0]
            kind = record['kind']
            affected_area = record['affected_area']

            f = request.files['file']

            if f:
                # predict.py 함수로 전처리 후 모델 돌리기
                # f 로 모델 돌려서 나온 값 db에 저장

                # 서버 내 모델 저장 경로
                cat_path = ".\\EYE_Model\\고양이_안구질환_DenseNet.h5"
                dog_path = ".\\EYE_Model\\개_안구질환_DenseNet121.h5"

                # s3에 이미지 업로드
                filename = secure_filename(f.filename)
                img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{filename}"
                image = img_url
                upload_file_to_s3(f)

                # 업로드하면 파일 닫혀서 업로드한 파일 다시 받아옴
                obj = s3.get_object(Bucket=AWS_S3_BUCKET_NAME,
                                Key=filename)
                response = obj['Body']
                img = Image.open(response)

                # 이미지 전처리
                img = mk_img(img)
                
                # 모델 결과 
                if kind == "고양이":
                    result = predict_result(cat_path, img)
                else:
                    result = predict_result(dog_path, img)

                # 진단 결과
                comment = result

                content = record['content'] # 결과에 따른 피드백
                
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