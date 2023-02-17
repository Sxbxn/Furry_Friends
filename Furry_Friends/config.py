SQLALCHEMY_DATABASE_URI = 'sqlite:///pet_test.db'
SECRET_KEY = "test"

SESSION_PERMANANENT = True # default
SESSION_USE_SIGNER = True
SESSION_TYPE = "filesystem"

ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])

AWS_ACCESS_KEY = "AKIAVE2UBMDZLASEUIZM"
AWS_SECRET_ACCESS_KEY = "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"
AWS_S3_BUCKET_REGION = "ap-northeast-1"
AWS_S3_BUCKET_NAME = "cosmosaurtest"