from flask import Flask  # 서버 구현을 위한 Flask 객체 import
from flask import request
from flask import jsonify

app = Flask(__name__)  # Flask 객체 선언, 파라미터로 어플리케이션 패키지의 이름을 넣어줌.

@app.route('/', methods=['GET', 'POST'])
def hello_world():
    data = request.get_json()
    return jsonify(data)

if __name__ == "__main__":
    app.run()