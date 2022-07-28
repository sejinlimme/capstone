import numpy as np
import cv2

xml = 'haarcascade_frontalface_default.xml'
face_cascade = cv2.CascadeClassifier(xml)

cap = cv2.VideoCapture(0) #노트북 웹캠 카메라로 사용, 0이 내장, 1이 외장
cap.set(3,640) #너비
cap.set(4,480) #높이

while(True):
    ret, frame = cap.read()
    frame = cv2.flip(frame, 1) #좌우 대칭
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY) #gray scale로 변경

    faces = face_cascade.detectMultiScale(gray,1.05,5)
    print("Number of faces detected: " +str(len(faces)))

    if len(faces):
        for (x,y,w,h) in faces:
            cv2.rectangle(frame,(x,y),(x+w,y+h),(255,0,0),2)

    cv2.imshow('result', frame)

    k = cv2.waitKey(3000) & 0xff #waitKey값으로 캡처시간 조정, ms단위
    if k == 27: #Esc 누르면 종료
        break

cap.release()
cv2.destroyAllWindows()
