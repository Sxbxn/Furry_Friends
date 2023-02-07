from flask import Flask, Blueprint
from flask_migrate import Migrate
import config
from pybo.test import bp
from pybo.connect_db import db
from pybo.model import Animals, Routine
migrate = Migrate()

def create_app():
    app = Flask(__name__)

    app.config.from_object(config)

    # ORM
    db.init_app(app)
    migrate.init_app(app, db)
    from . import model

    # 블루프린트
    app.register_blueprint(bp)

    return app
