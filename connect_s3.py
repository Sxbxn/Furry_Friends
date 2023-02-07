from flask import boto3
from PIL import Image
from config import AWS_ACCESS_KEY, AWS_SECRET_ACCESS_KEY
from config import AWS_S3_BUCKET_NAME, AWS_S3_BUCKET_REGION

def s3_connection():
    try:
        # s3 클라이언트 생성
        s3 = boto3.client(
            service_name="s3",
            region_name=AWS_S3_BUCKET_REGION,
            aws_access_key_id=AWS_ACCESS_KEY,
            aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
        )
        print("s3 bucket connected!") 
        return s3

    except Exception as e:
        print(e)
        print('ERROR_S3_CONNECTION_FAILED')       
        

def s3_put_object(s3, bucket, filepath, filename):
    try:
        s3.upload_file(filepath, bucket, filename)
    except Exception as e:
        print(e)
        return False
    return True


def s3_get_object(s3, bucket, object_name, file_name):
    try:
        s3.download_file(bucket, object_name, file_name)
    except Exception as e:
        print(e)
        return False
    return True