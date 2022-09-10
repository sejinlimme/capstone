import numpy as np
import cv2
import tensorflow as tf
from tensorflow.keras.utils import img_to_array
from keras.models import load_model

#저장된 xml과 모델 불러오기
xml = 'haarcascade_frontalface_default.xml'
face_cascade = cv2.CascadeClassifier(xml)
model = tf.keras.models.load_model('model.h5')
Emotions = ["Angry", "Disgust", "Fear", "Happiness", "Sad", "Surprise", "Neutral"]

cap = cv2.VideoCapture(0) #노트북 웹캠 카메라로 사용, 0이 내장, 1이 외장
cap.set(3,640) #너비
cap.set(4,480) #높이

while(True):
    ret, frame = cap.read()
    frame = cv2.flip(frame, 1) #좌우 대칭
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY) #gray scale로 변경

    faces = face_cascade.detectMultiScale(gray,1.05,5)

    canvas = np.zeros((250, 300, 3), dtype="uint8") #빈 이미지 생성

    if len(faces) > 0:
        #가장 큰 이미지의 경우
        face = sorted(faces, reverse=True, key=lambda x: (x[2] - x[0]) * (x[3] - x[1]))[0]
        (fX, fY, fW, fH) = face

        # 이미지 크기 조정
        roi = gray[fY:fY + fH, fX:fX + fW]
        roi = cv2.resize(roi, (48, 48))
        roi = roi.astype("float") / 255.0
        roi = img_to_array(roi)
        roi = np.expand_dims(roi, axis=0)

        #표정 탐지
        preds = model.predict(roi)[0]
        emotion_probability = np.max(preds)
        label = Emotions[preds.argmax()]

        #라벨링 할당
        cv2.putText(frame, label, (fX, fY - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.45, (0, 0, 255), 2)
        cv2.rectangle(frame, (fX, fY), (fX + fW, fY + fH), (0, 0, 255), 2)

        #라벨 프린트
        for (i, (emotion, prob)) in enumerate(zip(Emotions, preds)):
            text = "{}: {:.2f}%".format(emotion, prob * 100)
            w = int(prob * 300)
            cv2.rectangle(canvas, (7, (i * 35) + 5), (w, (i * 35) + 35), (0, 0, 255), -1)
            cv2.putText(canvas, text, (10, (i * 35) +23), cv2.FONT_HERSHEY_SIMPLEX, 0.45, (255, 255, 255), 2)

    #창 두개 열기
    cv2.imshow('Emotion Recognition', frame)
    cv2.imshow('Probabilities', canvas)

    k = cv2.waitKey(1) & 0xff #waitKey값으로 캡처시간 조정, ms단위
    if k == 27: #Esc 누르면 종료
        break

#프로그램 종료하고 창 닫기
cap.release()
cv2.destroyAllWindows()