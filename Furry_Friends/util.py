from flask import request, session
import boto3
import os
from werkzeug.utils import secure_filename


from Furry_Friends.predict import predict_result
from config import AWS_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION, AWS_SECRET_ACCESS_KEY



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
        return objs


def upload_file_to_s3(file):
    filename = secure_filename(file.filename)
    s3_connection().upload_fileobj(
            file,
            AWS_S3_BUCKET_NAME,
            file.filename
    )
    return file.filename


def to_weekday(num):
    weekdays = ['월요일','화요일','수요일','목요일','금요일','토요일','일요일']
    num = int(num)
    return weekdays[num]


def int_to_bool(animal):
    if animal['neutered'] == 0:
        animal['neutered'] = False
    else:
        animal['neutered'] = True
    return animal


def get_user_info():
    user_id = request.cookies.get('login')
    try:
        s = session._get_current_object()
        animal_id = s['curr_animal']
        return user_id, animal_id
    except:
        animal_id = request.headers['animal_id']
        return user_id, animal_id


def get_xray(path, img):
    models_lst = os.listdir(path)
    model_paths = [path + '\\' + model for model in models_lst]
    results = [predict_result(path, img) for path in model_paths]

    return results