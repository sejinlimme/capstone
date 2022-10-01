import flask
import requests

app = flask.Flask(__name__)  # Flask 객체 선언, 파라미터로 어플리케이션 패키지의 이름을 넣어줌.

#연결 상태 표시
@app.route('/', methods=['GET'])
def handle_call():
    return "Successfully Connected"

#get method
@app.route('/getfact', methods=['GET'])
def get_fact():
    return "Hey!! I'm the fact you got!!!"

#post method
@app.route('/getname/<name>', methods=['POST'])
def extract_name(name):
    return "I got your name" + name;

if __name__ == "__main__":
    app.run("192.168.123.105", port=5000, debug=True)