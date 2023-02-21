from flask import url_for, redirect, jsonify, request, session, Blueprint
from sqlalchemy import and_
from PIL import Image
import base64
import datetime
import os
from werkzeug.utils import secure_filename
import json


from Furry_Friends.connect_db import db
from Furry_Friends.models import User, Animal, Health
from Furry_Friends.util import s3_connection, query_to_dict, upload_file_to_s3, get_xray
from Furry_Friends.predict import padding, mk_img, predict_result
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION


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

    # 이미지 파일 form서 request
    file = request.form['file']
    record = json.loads(request.form['data'])

    # 정보
    kind = record['kind']
    affected_area = record['affected_area']
    posture = record['posture'] 

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
    img = mk_img(img)
    
    # 모델 결과 
    if kind == "dog": # 반려견
        if affected_area == "ab": # 복부
            if posture == "vd": # vd, 모델 5개

                dog_ab_vd_diseases = ["거대신장","복부종양","비뇨기결석","탈장","복수"]

                results = get_xray(".\XRAY_Model\dog_ab_vd", img)
                json_results = dict(zip(dog_ab_vd_diseases, results))
                
                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                return jsonify(final_results)

            else: # lateral, 모델 8개

                dog_ab_lt_diseases = ["간비대", '소간증', '복부종양', '비뇨기결석', '장폐색', '거대결장', '탈장', '복수']

                results = get_xray(".\XRAY_Model\dog_ab_lateral", img)
                json_results = dict(zip(dog_ab_lt_diseases, results))

                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                print(final_results)
                return jsonify(final_results)

        elif affected_area == "ch": # 흉부
            if posture == "vd": # vd, 모델 1개
                
                dog_ch_vd_diseases = ["종격동변위"]

                results = get_xray(".\XRAY_Model\dog_ch_vd", img)
                json_results = dict(zip(dog_ch_vd_diseases, results))
                
                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                return jsonify(final_results)

            else: # lateral, 모델 2개
                
                dog_ch_lt_diseases = ["심비대","기관허탈"]

                results = get_xray(".\XRAY_Model\dog_ch_lt", img)
                json_results = dict(zip(dog_ch_lt_diseases, results))
                
                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                return jsonify(final_results)

        else: # 근골격
            # ap, 모델 3개
            dog_mu_ap_diseases = ["사지골절","엉덩관절탈구","슬개골탈구"]

            results = get_xray(".\XRAY_Model\dog_mu_ap", img)
            json_results = dict(zip(dog_mu_ap_diseases, results))

            final_results = {}
            final_results['diseases'] = json_results
            final_results['image'] = img_url

            return jsonify(final_results)

    else: # cat

        if affected_area == "ab": # 복부

            if posture == "vd": # vd, 모델 5개
                
                cat_ab_vd_diseases = ["거대신장", "복부종양","비뇨기결석", "탈장", "복수"]

                results = get_xray(".\XRAY_Model\cat_ab_vd", img)
                json_results = dict(zip(cat_ab_vd_diseases, results))

                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                return jsonify(final_results)
                

            else:               # lateral, 모델 8개
                
                cat_ab_lt_diseases = ["간비대", "소간증", "복부종양", "비뇨기결석", "장폐색", "거대결장", "탈장", "복수"]

                results = get_xray(".\XRAY_Model\cat_ab_lateral", img)
                json_results = dict(zip(cat_ab_lt_diseases, results))

                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                return jsonify(final_results)

        else: # 흉부, lateral 모델 1개
                
                cat_ch_lt_diseases = ["심비대"]

                results = get_xray(".\XRAY_Model\cat_ch_lateral", img)
                json_results = dict(zip(cat_ch_lt_diseases, results))

                final_results = {}
                final_results['diseases'] = json_results
                final_results['image'] = img_url

                return jsonify(final_results)

        # else:                       # 근골격 # DROP
        #     results = []    
        #     return jsonify(json_results)
