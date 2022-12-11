import io

from flask import Flask, request, jsonify
import numpy as np
import pandas as pd
import torch
from pytorch_lightning import Trainer
from pytorch_lightning.callbacks import ModelCheckpoint
from pytorch_lightning.core.lightning import LightningModule
from torch.utils.data import DataLoader, Dataset
from transformers.optimization import AdamW, get_cosine_schedule_with_warmup
from transformers import PreTrainedTokenizerFast, GPT2LMHeadModel
import re
import os
from pathlib import Path
import base64
from PIL import Image
import cv2
import tensorflow as tf
from tensorflow.keras.utils import img_to_array
from keras.models import load_model
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

db_url = 'https://capstone-18a44-default-rtdb.firebaseio.com/'
cred = credentials.Certificate(r"C:\Users\emfor\capstone-18a44-firebase-adminsdk-shxg2-b7a233d42d.json")
default_app = firebase_admin.initialize_app(cred, {'databaseURL':db_url})

#os.environ['CUDA_LAUNCH_BLOCKING'] = "-1"
#os.environ["CUDA_VISIBLE_DEVICES"] = "0"

Q_TKN = "<usr>"
A_TKN = "<sys>"
BOS = '</s>'
EOS = '</s>'
MASK = '<unused0>'
SENT = '<unused1>'
PAD = '<pad>'

koGPT2_TOKENIZER = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2",
            bos_token=BOS, eos_token=EOS, unk_token='<unk>',
            pad_token=PAD, mask_token=MASK)
model = GPT2LMHeadModel.from_pretrained('skt/kogpt2-base-v2')

#data_path = Path(__file__).parent.resolve().joinpath("./data/Chatbot_Data.csv")
Chatbot_Data = pd.read_csv('Chatbot_Data.csv', encoding='cp949')

class ChatbotDataset(Dataset):
    def __init__(self, chats, max_len=40):
        self._data = chats
        self.max_len = max_len
        self.q_token = Q_TKN
        self.a_token = A_TKN
        self.sent_token = SENT
        self.eos = EOS
        self.mask = MASK
        self.tokenizer = koGPT2_TOKENIZER

    def __len__(self):
        return len(self._data)

    def __getitem__(self, idx):
        turn = self._data.iloc[idx]
        q = turn["Q"]
        q = re.sub(r"([?.!,])", r" ", q)

        a = turn["A"]
        a = re.sub(r"([?.!,])", r" ", a)

        q_toked = self.tokenizer.tokenize(self.q_token + q + self.sent_token)
        q_len = len(q_toked)

        a_toked = self.tokenizer.tokenize(self.a_token + a + self.eos)
        a_len = len(a_toked)


        if q_len > self.max_len:
            a_len = self.max_len - q_len
            if a_len <= 0:
                q_toked = q_toked[-(int(self.max_len / 2)) :]
                q_len = len(q_toked)
                a_len = self.max_len - q_len
            a_toked = a_toked[:a_len]
            a_len = len(a_toked)


        if q_len + a_len > self.max_len:
            a_len = self.max_len - q_len
            if a_len <= 0:
                q_toked = q_toked[-(int(self.max_len / 2)) :]
                q_len = len(q_toked)
                a_len = self.max_len - q_len
            a_toked = a_toked[:a_len]
            a_len = len(a_toked)

        labels = [self.mask,] * q_len + a_toked[1:]

        mask = [0] * q_len + [1] * a_len + [0] * (self.max_len - q_len - a_len)

        labels_ids = self.tokenizer.convert_tokens_to_ids(labels)

        while len(labels_ids) < self.max_len:
            labels_ids += [self.tokenizer.pad_token_id]


        token_ids = self.tokenizer.convert_tokens_to_ids(q_toked + a_toked)

        while len(token_ids) < self.max_len:
            token_ids += [self.tokenizer.pad_token_id]


        return (token_ids, np.array(mask), labels_ids)

def collate_batch(batch):
    data = [item[0] for item in batch]
    mask = [item[1] for item in batch]
    label = [item[2] for item in batch]
    a1 = np.array(data)
    a2 = np.array(mask)
    a3 = np.array(label)
    return torch.cuda.LongTensor(a1), torch.cuda.LongTensor(a2), torch.cuda.LongTensor(a3)

device = torch.device("cuda") if torch.cuda.is_available() else ("cpu")
train_set = ChatbotDataset(Chatbot_Data, max_len=40)

train_dataloader = DataLoader(train_set, batch_size=32, num_workers=0, shuffle=True, collate_fn=collate_batch,)

model.to(device)
#next(model.parameters()).is_cuda
model.train()

save_ckpt_path = Path(__file__).parent.resolve().joinpath("./kogpt2.pth")

#torch.save(model, 'kogpt2.pth')

model = torch.load('kogpt2.pth', map_location=device)
model.eval()

sent = '0'

app = Flask(__name__)  # Flask 객체 선언, 파라미터로 어플리케이션 패키지의 이름을 넣어줌

@app.route('/chat', methods=['POST'])
def index():
    req_data = request.get_json()
    q = req_data['message'].strip()

    with torch.no_grad():
        a = ""
        while 1:
            input_ids = torch.cuda.LongTensor(koGPT2_TOKENIZER.encode(Q_TKN + q + SENT + sent + A_TKN + a)).unsqueeze(
                dim=0)
            pred = model(input_ids)
            pred = pred.logits
            gen = koGPT2_TOKENIZER.convert_ids_to_tokens(torch.argmax(pred, dim=-1).squeeze().cpu().numpy().tolist())[-1]
            if gen == EOS:
                break
            a += gen.replace("▁", " ")
        response_string = "{0}".format(a.strip()) #출력문 어디에 저장되는지 확인 필요함
        return jsonify(message=response_string)

@app.route('/picture', methods=['POST'])
def emotion_detect():
    base64_str = request.get_json()
    data = base64_str['message']
    imgdata = base64.b64decode(data)
    dataBytesIO = io.BytesIO(imgdata)
    image = Image.open(dataBytesIO)
    gray = cv2.cvtColor(np.array(image), cv2.COLOR_BGR2GRAY)

    xml = 'haarcascade_frontalface_default.xml'
    face_cascade = cv2.CascadeClassifier(xml)
    face_model = tf.keras.models.load_model('model.h5')
    Emotions = ["Angry", "Disgust", "Fear", "Happiness", "Sad", "Surprise", "Neutral"]

    faces = face_cascade.detectMultiScale(gray, 1.05, 5)

    if len(faces) > 0:
        # 가장 큰 이미지의 경우
        face = sorted(faces, reverse=True, key=lambda x: (x[2] - x[0]) * (x[3] - x[1]))[0]
        (fX, fY, fW, fH) = face

        # 이미지 크기 조정
        roi = gray[fY:fY + fH, fX:fX + fW]
        roi = cv2.resize(roi, (48, 48))
        roi = roi.astype("float") / 255.0
        roi = img_to_array(roi)
        roi = np.expand_dims(roi, axis=0)

        # 표정 탐지
        preds = face_model.predict(roi)[0]
        emotion_probability = np.max(preds)
        label = Emotions[preds.argmax()]

        face_emotion = []

        for (i, (emotion, prob)) in enumerate(zip(Emotions, preds)):

            #text = "{}: {:.2f}%".format(emotion, prob * 100)
            text = "{:.2f}%".format(prob * 100)
            face_emotion.append(text)

        for i in range(len(face_emotion)):
            print(face_emotion[i])

        dir = db.reference()
        dir.push({'Angry': face_emotion[0], 'Disgust': face_emotion[1], 'Fear': face_emotion[2], 'Happiness': face_emotion[3], 'Sad': face_emotion[4], 'Surprise': face_emotion[5], 'Neutral': face_emotion[6]})



if __name__ == "__main__":
    app.run("192.168.219.106", port=5000, debug=True)