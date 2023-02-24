from flask import jsonify, request, session, Blueprint
import datetime
from sqlalchemy import and_
import json


# from predict import padding, mk_img, predict_result
from Furry_Friends.util import s3_connection, query_to_dict, upload_file_to_s3
from Furry_Friends.connector import db
from Furry_Friends.models import User, Animal, Journal
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION

# app = Flask(__name__)
bp = Blueprint('journal', __name__, url_prefix='/journal')


s3 = s3_connection()


# 기록 목록 출력
@bp.route('/journals', methods = ['GET'])
def journals():
    user = request.cookies.get('login')
    asd = session._get_current_object()

    if 'login' in asd or user:

        animal_id = int(request.headers['animal_id'])
        animals = Animal.query.filter_by(user_id = asd['login']).all()
        ids = [animal.animal_id for animal in animals]

        if animal_id in ids:
            session['curr_animal'] = animal_id

            journals = Journal.query.filter(and_(Journal.user_id==asd['login'],
                                                Journal.animal_id==asd['curr_animal'])).all()
            
            # 기록 존재시
            if journals != []:
                journals = query_to_dict(journals)
                return jsonify(journals)

            # 기록이 없을 시
            else:
                return []
        else:
            return "no animal registered"
    else:
        return "not logged in"


# 아이템 클릭 시 기록 열람
@bp.route('/content', methods=["GET"])
def journal_content():
    journal_index = request.headers['index']

    journal_entry = Journal.query.get(int(journal_index))
    
    journal_entry = query_to_dict(journal_entry)

    return jsonify(journal_entry)


# 기록 생성
@bp.route('/factory', methods=["POST"])
def journal_factory():
    asd = session._get_current_object()

    journal_entry = request.form

    user = User.query.filter_by(user_id = asd['login']).first()
    animal = Animal.query.filter_by(animal_id = asd['curr_animal']).first()

    journal_entry = json.loads(journal_entry['data'])

    title = journal_entry['title']
    content = journal_entry['content']
    currdate = journal_entry['currdate']
    currdate = currdate.split(" ")[0]

    f = request.files['file']
    
    # 사진 업로드 시 사진 링크 반환, 일상 기록 db 저장
    extension = '.' + f.filename.split('.')[-1]

    newname = user.user_id + '_' + animal.animal_name + '_' + (str(datetime.datetime.now()).replace(":","")).replace(" ","_") + extension
    img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
    f.filename = newname

    upload_file_to_s3(f)

    image = img_url

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
        
        existing_entry = query_to_dict(editing_entry)
        return jsonify(existing_entry)


    else: # PUT

        asd = session._get_current_object()
        
        animal = Animal.query.get(asd['curr_animal'])

        journal_index = request.headers['index']
        editing_entry = Journal.query.get(journal_index)
        
        changes = request.form
        changes = json.loads(changes['data'])

        title = changes['title']
        content = changes['content']

        f = request.files['file']

        try:
            s3.delete_object(
                Bucket = AWS_S3_BUCKET_NAME,
                Key = (editing_entry.image).split('/')[-1]
            )

        except: # s3에서 이미지 못찾은 경우
            pass
        
        extension = '.' + f.filename.split('.')[-1]

        newname = asd['login'] + '_' + animal.animal_name + '_' + (str(datetime.datetime.now()).replace(":","")).replace(" ","_") + extension
        img_url = f"https://{AWS_S3_BUCKET_NAME}.s3.{AWS_S3_BUCKET_REGION}.amazonaws.com/{newname}"
        f.filename = newname

        upload_file_to_s3(f)

        editing_entry.image = img_url
        editing_entry.title = title
        editing_entry.content = content

        db.session.commit()
    return "successfully updated"


# 기록 삭제
@bp.route('/delete', methods=["DELETE"])
def journal_delete():

    journal_index = int(request.headers['index'])
    deleting_journal = Journal.query.get(journal_index)

    s3.delete_object(
                    Bucket = AWS_S3_BUCKET_NAME,
                    Key = (deleting_journal.image).split('/')[-1]
                    )

    db.session.delete(deleting_journal)
    db.session.commit()
    
    return "successfully removed"