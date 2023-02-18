import boto3

from werkzeug.utils import secure_filename
from config import AWS_ACCESS_KEY, AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION, AWS_SECRET_ACCESS_KEY, ALLOWED_EXTENSIONS



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
    weekdays = ['mon','tue','wed','thu','fri','sat','sun']
    num = int(num)
    return weekdays[num]