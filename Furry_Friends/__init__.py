from flask import Flask, jsonify, render_template, session, g, request
from Furry_Friends.connect_db import db
from Furry_Friends.connect_session import sess
from flask_migrate import Migrate
from flask_session import Session
import config

migrate = Migrate()

def create_app():
    app = Flask(__name__)

    app.config.from_object(config)
    sess.init_app(app)
    db.init_app(app)
    migrate.init_app(app, db)
    from . import models


    from .views import authentification, routine, checklist, journal, health, pet
    app.register_blueprint(authentification.bp)
    app.register_blueprint(checklist.bp)
    app.register_blueprint(journal.bp)
    app.register_blueprint(health.bp)
    app.register_blueprint(routine.bp)
    app.register_blueprint(pet.bp)


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

    @app.route('/diary-single')
    def ds():
        return render_template('diary-single.html')

    @app.route('/medical-record')
    def medical_record():
        return render_template('medical-record.html')

    @app.route('//medical-single')
    def ms():
        return render_template('medical-single.html')

    @app.route('/calendar')
    def calendar():
        return render_template('calendar.html')

    @app.route('/mypage')
    def mypage():
        return render_template('mypage.html')

    @app.route('/sign')
    def sign():
        return render_template('sign.html')

    @app.route('/check')
    def check():
        return render_template('check.html')

    @app.route('/check-result')
    def cr():
        return render_template('check-result.html')

    if __name__ == "__main__":
        app.run(debug=True, host="0.0.0.0")

    return app
