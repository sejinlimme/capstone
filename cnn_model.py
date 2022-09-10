import pandas as pd
import numpy as np
import keras
import os
import tensorflow as tf
from tensorflow.keras.utils import to_categorical
from keras.models import load_model
from keras.models import Sequential
from tensorflow.keras.optimizers import Adam
from keras.preprocessing.image import ImageDataGenerator
from keras.layers import Conv2D, MaxPool2D, AveragePooling2D, Input, BatchNormalization, MaxPooling2D, Activation, Flatten, Dense, Dropout
from sklearn.model_selection import train_test_split
from keras.models import Model
from imblearn.over_sampling import RandomOverSampler

#1.데이터셋 불러오기
df = pd.read_csv("fer2013.csv")

#2.데이터 전처리
x_data = df['pixels']
y_data = df['emotion']

oversampler = RandomOverSampler(sampling_strategy='auto')

x_data, y_data = oversampler.fit_resample(x_data.values.reshape(-1,1), y_data)

y_data.value_counts()

x_data = pd.Series(x_data.flatten())

x_data = np.array(list(map(str.split, x_data)), np.float32)
x_data/=255

x_data = x_data.reshape(-1, 48, 48, 1)

y_data = np.array(y_data)
y_data = y_data.reshape(y_data.shape[0], 1)

x_train, x_test, y_train, y_test = train_test_split(x_data, y_data, test_size=0.1, random_state=45)

#3.모델 디자인
model = Sequential([
    Input((48, 48, 1)),

    Conv2D(32, kernel_size=(3,3), strides=(1,1), padding='valid'),
    BatchNormalization(axis=3),
    Activation('relu'),

    Conv2D(64, (3,3), strides=(1,1), padding='same'),
    BatchNormalization(axis=3),
    Activation('relu'),
    MaxPooling2D((2,2)),

    Conv2D(64, (3,3), strides=(1,1), padding='valid'),
    BatchNormalization(axis=3),
    Activation('relu'),

    Conv2D(128, (3,3), strides=(1,1), padding='same'),
    BatchNormalization(axis=3),
    Activation('relu'),
    MaxPooling2D((2,2)),

    Conv2D(128, (3,3), strides=(1,1), padding='valid'),
    BatchNormalization(axis=3),
    Activation('relu'),
    MaxPooling2D((2,2)),

    Flatten(),
    Dense(200, activation='relu'),
    Dropout(0.6),
    Dense(7, activation='softmax')

])

model.summary()

adam = Adam(learning_rate=0.0001)
model.compile(optimizer=adam, loss='categorical_crossentropy', metrics=['accuracy'])

y_train = to_categorical(y_train, 7)
y_train.shape

y_test = to_categorical(y_test, 7)
y_test.shape

history = model.fit(x_train, y_train, epochs=30, validation_data=(x_test, y_test))

print("Accuracy of our model on validation data : " , model.evaluate(x_test, y_test)[1]*100 , "%")

#모델 저장
fer_json = model.to_json()
with open("fer.json", "w") as json_file:
    json_file.write(fer_json)
model.save('model.h5')