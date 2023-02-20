from flask import url_for, redirect, jsonify, request, session, Blueprint
from connect_db import db
from sqlalchemy import and_
from PIL import Image
import base64
import datetime
import os

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
    asd = session._get_current_object()

    if 'login' in asd:

        animal_id = int(request.headers['animal_id'])
        animals = Animal.query.filter_by(user_id = asd['login']).all()
        ids = [animal.animal_id for animal in animals]

        if animal_id in ids:
            session['curr_animal'] = animal_id

            health_records = Health.query.filter(and_(Health.user_id==asd['login'],
                                                Health.animal_id==asd['curr_animal'])).all()

            if health_records != []:
                health_records = query_to_dict(health_records)
                for i in range(len(health_records)):
                    health_records[i]['comment'] = str(health_records[i]['comment'])

                return jsonify(health_records)
            else:
                return []
        else:
            return "no animal registered"
    else:
        "not logged in"
        

@bp.route('/content', methods=["GET"])
def record_content():
    
    record_index = request.headers['index']

    health_record = Health.query.get(int(record_index))
    health_record = health_record.__dict__
    del health_record['_sa_instance_state']
    health_record['content'] = str(health_record['content'])
    return jsonify(health_record)


# 앱 건강기록 생성
@bp.route('/factory', methods=["GET","POST"])
def record_factory():

    asd = session._get_current_object()

    if request.method=="GET":
        return "health record entry form"


    else: # POST
        
            record = request.form
            record = json.loads(record['data'])

            user = User.query.filter_by(user_id = asd['login']).first()
            animal = Animal.query.filter_by(animal_id = asd['curr_animal']).first()

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

                content = record['content'] # 사용자 입력 사항
                
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


@bp.route('/check', methods=["POST"])
def check():

    # 로그인 확인
    # asd = session._get_current_object()

    # if 'login' in asd:
    #     user = User.query.filter_by(user_id = asd['login']).first()
    #     if user.vet == 1:

    # 이미지 파일 form서 request
    file = request.form['file']
    record = request.form['data']
    record = json.loads(record)

    # 정보
    kind = record['kind']
    affected_area = record['affected_area']
    posture = record['posture']

    print(kind,affected_area, posture)  

    # 이미지
    imgfile = file.split(',')[1] # base64 string

    extension = '.' + ((file.split(',')[0]).split('/')[1]).split(';')[0] # .png, .jpeg, ...

    datenow = str(datetime.datetime.now()).replace(":",'-') # 
    newname = '_'.join(datenow.split(" "))
    newname = newname + extension

    # base64 decode하여 로컬에 저장
    with open(newname, 'wb') as f:
        f.write(base64.b64decode(imgfile))

    # s3에 업로드 (결과 창에 출력용)
    s3.upload_file(Filename=newname, Bucket=AWS_S3_BUCKET_NAME, Key=newname)
    img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
    os.remove(f"./{newname}")

    obj = s3.get_object(Bucket=AWS_S3_BUCKET_NAME,
                                Key=newname)
    response = obj['Body']
    img = Image.open(response)

    # 이미지 전처리
    img = mk_img(img)
    
    # 모델 결과 
    if kind == "dog": # 반려견

        if affected_area == "ab": # 복부

            if posture == "vd": # vd, 모델 5개

                # dog_ab_vd 디렉토리 안에 있는 경로 리스트화
                dog_ab_vd = os.listdir("")
                
                results = [predict_result(i, img) for i in dog_ab_vd]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                        
                    else:
                        str_results.append("normal")                        
                return str_results

# ------------------------------------------------------------- 테스트 ---------------------------------------------
            else: # lateral, 모델 8개
                
                print(kind,affected_area, posture)

                dog_ab_lt = os.listdir(".\XRAY_Model\dog_ab_lateral")

                model_path = [".\XRAY_Model\dog_ab_lateral\\" + path for path in dog_ab_lt]
                
                results = [predict_result(i, img) for i in model_path]

                print(results)
                return results

# ------------------------------------------------------------- 테스트 ---------------------------------------------

        elif affected_area == "ch": # 흉부

            if posture == "vd": # vd, 모델 1개

                dog_ch_vd = os.listdir("")
                
                results = [predict_result(i, img) for i in dog_ch_vd]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                        
                    else:
                        str_results.append("normal")                        
                return str_results

            else: # lateral, 모델 2개
                dog_ch_lt = os.listdir("")
                
                results = [predict_result(i, img) for i in dog_ch_lt]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                        
                    else:
                        str_results.append("normal")                        
                return str_results

        else: # 근골격
            # ap, 모델 3개
            dog_mu_ap = os.listdir("")
                
            results = [predict_result(i, img) for i in dog_mu_ap]

            str_results = []
            for result in results:
                if result > 0.5:
                    str_results.append("abnormal")
                else:
                    str_results.append("normal") 
            return str_results

    else: # cat

        if affected_area == "ab": # 복부

            if posture == "vd": # vd, 모델 5개
                
                cat_ab_vd = os.listdir("")
                
                results = [predict_result(i, img) for i in cat_ab_vd]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                       
                    else:
                        str_results.append("normal")                        
                return str_results

            else:               # lateral, 모델 8개
                cat_ab_lt = os.listdir("")
                
                results = [predict_result(i, img) for i in cat_ab_lt]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                       
                    else:
                        str_results.append("normal")                        
                return str_results

        elif affected_area == "ch": # 흉부, lateral 모델 1개
                cat_ch_lt = os.listdir("")
                
                results = [predict_result(i, img) for i in cat_ch_lt]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                       
                    else:
                        str_results.append("normal")                        
                return str_results

        else:                       # 근골격
                cat_mu_ = os.listdir("")
                
                results = [predict_result(i, img) for i in cat_mu_]

                str_results = []
                for result in results:
                    if result > 0.5:
                        str_results.append("abnormal")                       
                    else:
                        str_results.append("normal")                        
                return str_results


    # record = json.loads(record)
    # kind = record['kind']
    # affected_area = record['affected_area']

    # if f:
    #     return "."
        
        # else:
            # return "not a vet"

