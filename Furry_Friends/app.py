from flask import Flask, redirect, url_for, session
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


# 메인 화면
@app.route('/', methods=["GET"])
def main():

    # 세션에 로그인한 기록이 있음
    if 'login' in session: 
        # 관리할 동물 선택 o --> 동물 프로필로 이동
        if 'curr_animal' in session:
            return redirect(url_for('pet.profile'))

        # 관리할 동물 선택 x --> 동물 관리로 이동
        else:
            return redirect(url_for('pet.management'))

        # return f"{session['user_id']} is logged in"

    # 세션에 로그인한 기록이 없음
    else:
        # 로그인 페이지 리다이렉트
        return redirect(url_for('authentification.login'))


if __name__ == "__main__":
    app.run(debug=True)
