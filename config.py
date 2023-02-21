import os

BASE_DIR = os.path.dirname(__file__)

SQLALCHEMY_DATABASE_URI = 'sqlite:///{}'.format(os.path.join(BASE_DIR, 'furry_friends.db'))
SQLALCHEMY_TRACK_MODIFICATIONS = False
SECRET_KEY = os.urandom(32)

SESSION_PERMANANENT = True
SESSION_USE_SIGNER = True
SESSION_TYPE = "filesystem"


# AWS_ACCESS_KEY = 
# AWS_SECRET_ACCESS_KEY = 
AWS_S3_BUCKET_REGION = "ap-northeast-1"
AWS_S3_BUCKET_NAME = "cosmosaurtest"