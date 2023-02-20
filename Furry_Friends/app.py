from flask import Flask, jsonify, render_template, session, g, request
from connect_db import db
from connect_session import sess
from flask_migrate import Migrate
from flask_session import Session


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
    return render_template('index.html')

@app.route('/about')
def about():
    return render_template('about.html')

@app.route('/diary')
def diary():
    return render_template('diary.html')

@app.route('/medical-record')
def medical_record():
    return render_template('medical-record.html')

@app.route('/sign')
def sign():
    return render_template('sign.html')

@app.route('/mypage')
def mypage():
    return render_template('mypage.html')

@app.route('/diary-single')
def ds():
    return render_template('diary-single.html')

@app.route('/check')
def check():
    return render_template('check.html')

@app.route('/calendar')
def calendar():
    return render_template('calendar.html')


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0")