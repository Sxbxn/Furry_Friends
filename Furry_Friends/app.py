from flask import Flask, jsonify, session
from connect_db import db
from flask_migrate import Migrate


# models
from models import Animal


# blueprints
import authentification
import routine
import checklist
import journal
import health
import pet


app = Flask(__name__)


app.register_blueprint(authentification.bp)
app.register_blueprint(checklist.bp)
app.register_blueprint(journal.bp)
app.register_blueprint(health.bp)
app.register_blueprint(routine.bp)
app.register_blueprint(pet.bp)


app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///pet_test.db'
app.config['SECRET_KEY'] = "test"
db.init_app(app)


Migrate(app,db)


def query_to_dict(objs):
    try:
        lst = [obj.__dict__ for obj in objs]
        for obj in lst:
            del obj['_sa_instance_state']
        return lst
    except TypeError: # non-iterable
        objs = objs.__dict__
        del objs['_sa_instance_state']
        lst = [objs]
        return lst


# 메인 화면
@app.route('/', methods=["GET"])
def main():

    # 세션에 로그인한 기록이 있음
    if 'login' in session: 
        animal = query_to_dict(Animal.query.filter_by(user_id = session['login']).all())
        return jsonify(animal)
        
    # 세션에 로그인한 기록이 없음
    else:
        return "not logged in"


if __name__ == "__main__":
    app.run(debug=True)