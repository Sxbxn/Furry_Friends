from flask import Flask, jsonify, render_template, session, g, request
from connect_db import db
from connect_session import sess
from flask_migrate import Migrate
from flask_session import Session

from util import query_to_dict

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

app.config['SESSION_PERMANANENT'] = True # default
app.config["SESSION_USE_SIGNER"] = True
app.config['SESSION_TYPE'] = "filesystem"

# app.config.update(SESSION_COOKIE_SAMESITE="None", SESSION_COOKIE_SECURE=True)

# app.config['SESSION_TYPE'] = "sqlalchemy" 
# app.config["SESSION_SQLALCHEMY_TABLE"] = 'sessions'

# app.config['SESSION_SQLALCHEMY'] = db
# app.session_interface.sql_session_model.__table__.create(bind = db.session.bind)


sess.init_app(app)
db.init_app(app)


Migrate(app,db)


# 메인 화면
@app.route('/', methods=["GET"])
def main():

    try:
        asd = session._get_current_object()
        print(asd['login'])
    
    except:
        pass
    
    # 세션에 로그인한 기록이 있음
    if 'login' in session: 
        animal = query_to_dict(Animal.query.filter_by(user_id = session['login']).first())

        if "curr_animal" not in session:     
            
            resp = {"user_id":session['login'],
                    "animal_id":-999,
                    "animal_name":"",
                    "bday":"",
                    "sex":"",
                    "neutered":"",
                    "weight":0.0,
                    "image":""}

            return jsonify(resp)

        else:
            animal = query_to_dict(Animal.query.filter_by(animal_id = session['curr_animal']).first())
            return jsonify(animal)
        
    # 세션에 로그인한 기록이 없음
    else:
        resp = {"user_id":"",
                "animal_id":-999,
                "animal_name":"",
                "bday":"",
                "sex":"",
                "neutered":"",
                "weight":0.0,
                "image":""}

        return jsonify(resp)


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")