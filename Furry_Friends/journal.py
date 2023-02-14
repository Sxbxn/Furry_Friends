from flask import url_for, redirect, jsonify, request, session, Blueprint
import boto3
import datetime
from connect_db import db
from sqlalchemy import and_
from models import User, Animal, Journal
import json
import os

from werkzeug.utils import secure_filename
# from predict import padding, mk_img, predict_result

from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION, ALLOWED_EXTENSIONS

# app = Flask(__name__)
bp = Blueprint('journal', __name__, url_prefix='/journal')


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
        lst = [obj.__dict__ for obj in objs]
        for obj in lst:
            del obj['_sa_instance_state']
        return lst
    except TypeError: # non-iterable
        if objs == None:
            return []
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst = [objs]
        return lst


def upload_file_to_s3(file):
    filename = secure_filename(file.filename)
    s3.upload_fileobj(
            file,
            AWS_S3_BUCKET_NAME,
            file.filename
            
    )

    return file.filename


s3 = s3_connection()


# 기록 목록 출력
@bp.route('/journals', methods = ['GET'])
def journals():

    # order by도?
    journals = Journal.query.filter(and_(Journal.user_id==session['login'],
                                        Journal.animal_id==session['curr_animal'])).all()
    
    # 기록 존재시
    if journals != []:
        journals = query_to_dict(journals)
        return jsonify(journals)

    # 기록이 없을 시
    else:
        return "no entry"


# 아이템 클릭 시 기록 열람
@bp.route('/content', methods=["GET"])
def journal_content():
    journal_index = request.headers['index']

    journal_entry = Journal.query.get(int(journal_index))
    
    journal_entry = journal_entry.__dict__
    del journal_entry['_sa_instance_state']

    return jsonify(journal_entry)


# 기록 생성
@bp.route('/factory', methods=["GET","POST"])
def journal_factory():
    currdate = request.headers['currdate']

    if request.method == "GET":
        return "journal entry form"
    
    else: # POST
        journal_entry = request.form

        user = User.query.filter_by(user_id = session['login']).first()
        animal = Animal.query.filter_by(animal_id = session['curr_animal']).first()

        journal_entry = json.loads(journal_entry['data'])

        title = journal_entry['title']
        content = journal_entry['content']
        currdate = request.headers['currdate']

        f = request.files['file']
        
        # 사진 업로드 시 사진 링크 반환, 일상 기록 db 저장
        if f:
            extension = f.filename.split('.')[-1]
            if extension in ALLOWED_EXTENSIONS:
                extension = '.' + extension

                newname = (str(datetime.datetime.now()).replace(":","")).replace(" ","_") + extension
                img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
                f.filename = newname

                output = upload_file_to_s3(f)
    
                image = img_url

            # 업로드된 파일이 이미지 파일이 아님
            else:
                return "wrong file extension"
            
        # 사진 업로드 x시 image 링크
        else: 
            image = ""

        new_entry = Journal(animal, user, title, image, content, currdate)
        db.session.add(new_entry)

        db.session.commit()
    
        return "journal successfully created"


# 기록 수정
@bp.route('/update', methods=["GET","PUT"])
def journal_update():
    
    journal_index = request.headers['index']
    
    if request.method == 'GET':
        editing_entry = Journal.query.get(journal_index)
        
        existing_entry = editing_entry.__dict__
        del existing_entry['_sa_instance_state']
        return jsonify(existing_entry)


    else: # POST
        
        journal_index = request.headers['index']
        editing_entry = Journal.query.get(journal_index)
        
        changes = request.form
        changes = json.loads(changes['data'])

        title = changes['title']
        content = changes['content']

        f = request.files['file']

        # 새로 이미지 업로드
        if f:
            # 기존의 이미지 s3에서 삭제
            try:
                s3.delete_object(
                    Bucket = AWS_S3_BUCKET_NAME,
                    Key = (editing_entry.image).split('/')[-1]
                )

            # 기존에 이미지가 없었던 경우 -- pass
            except: 
                pass
            
            extension = f.filename.split('.')[-1]
            if extension in ALLOWED_EXTENSIONS:
                extension = '.' + extension

                newname = (str(datetime.datetime.now()).replace(":","")).replace(" ","_") + extension
                img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
                f.filename = newname

                output = upload_file_to_s3(f)

                editing_entry.image = img_url
            
            else:
                return "wrong file extension"

        # 새로 이미지 업로드 X --> 기존의 image 칼럼 데이터 그대로 유지
        else:
            # 이미지가 있었는데 삭제하는 건?????
            pass

        editing_entry.title = title
        editing_entry.content = content

        db.session.commit()
    return "successfully updated"


# 기록 삭제
@bp.route('/delete', methods=["DELETE"])
def journal_delete():

    journal_index = int(request.headers['index'])
    deleting_journal = Journal.query.get(journal_index)

    if deleting_journal.image != "":
        s3.delete_object(
                        Bucket = AWS_S3_BUCKET_NAME,
                        Key = (deleting_journal.image).split('/')[-1]
                        )
    else:
        pass

    db.session.delete(deleting_journal)
    db.session.commit()
    
    return "successfully removed"
