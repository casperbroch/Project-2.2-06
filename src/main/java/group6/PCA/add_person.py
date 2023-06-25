import glob
import cv2
from face_detection import FaceDetection
import pandas as pd
import numpy as np
from face_alignment import align_face
import os


def capture():
    while True:
        ret, frame = cam.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        cv2.imshow('Image', frame)
        if cv2.waitKey(1) & 0xFF == ord('s'):  # Takes a screenshot with key 's'
            img = detector.detect_face(gray)
            while isinstance(img, type(None)):
                capture()
            # img = cv2.resize(img, (64, 64), interpolation=cv2.INTER_AREA)
            index = len(glob.glob('new_faces/*.jpg'))
            cv2.imwrite('new_faces/img' + str(index) + '.jpg', frame)
            break
        if cv2.waitKey(1) & 0xFF == ord('q'):
            add_person()


def add_person():
    print("Please input your name.")
    name = '\nMarian'  # TODO: Connect with user input
    file = open("names.txt", "a")
    file.writelines(name)
    file.close()

    with open('names.txt', 'r') as fp:
        line_count = len(fp.readlines())
    # 400 default faces; registered faces start at index 401
    person_idx = 400 + line_count

    # Storing it into df database
    # df = pd.read_csv("face_data_original.csv")
    df = pd.read_csv("face_data.csv")  # TODO: CHANGE

    # Reformatting all the newly inputted faces
    for path in glob.glob('new_faces/*.jpg'):
        print('Processing', path)
        # Returns reformatted, 64x64 gray-scale face
        img = align_face(path)
        if isinstance(img, type(None)):
            print("Face not detected")
            continue
        img = img.reshape(1, -1)
        if img.shape != (1, 4096):
            print("Faulty image")
            continue
        img = np.divide(img, 255.0)  # Normalize between [0,1]
        img = np.append(img, person_idx).reshape(1, -1)
        img = pd.DataFrame(img, columns=df.columns)
        df = pd.concat([df, img])

        # Replaces old csv with new one
        df.to_csv('face_data.csv', index=False)  # TODO: CHANGE
    exit(0)


detector = FaceDetection()

# Inspired by https://answers.opencv.org/question/196531/how-can-i-take-multiple-pictures-with-one-button-press/
cam = cv2.VideoCapture(0)
files = glob.glob('new_faces/*.jpg')
for f in files:
    os.remove(f)
while True:
    capture()
cam.release()
cv2.destroyAllWindows()

