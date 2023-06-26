import pickle
import os
import sys

import cv2
import numpy as np
import dlib
from concurrent.futures import ThreadPoolExecutor
import datetime

os.makedirs("People", exist_ok=True)

class IsItMe:
    def __init__(self):
        self.detector = dlib.get_frontal_face_detector()
        self.predictor = dlib.shape_predictor("src/main/java/group6/Python/models/shape_predictor_68_face_landmarks.dat")
        self.facerec = dlib.face_recognition_model_v1("src/main/java/group6/Python/models/dlib_face_recognition_resnet_model_v1.dat")
        self.capture = cv2.VideoCapture(0)
        
        facedir = 'src/main/java/group6/Python/faces'
        self.person_names = []
        self.my_photo_paths = []
        for filename in os.listdir(facedir):
            name = os.path.splitext(filename)[0]
            path = os.path.join(facedir, filename)

            tmp = [path]

            self.person_names.append(name)
            self.my_photo_paths.append(tmp)

        self.last_recognized_person_idx = -1
        self.load_embeddings()
        self.last_photo_taken = {name: datetime.datetime.min for name in self.person_names}
        self.my_photo_embeddings = [list(map(self.get_face_embedding, person_photos)) for person_photos in
                                    self.my_photo_paths]
        # Above is single thread. Down is multithread
        # self.my_photo_embeddings = []
        # self.process_all_person_photos()

    def process_all_person_photos(self):
        with ThreadPoolExecutor() as executor:
            for person_photos in self.my_photo_paths:
                person_embeddings = list(executor.map(self.get_face_embedding, person_photos))
                self.my_photo_embeddings.append(person_embeddings)

    def save_embeddings(self):
        with open('face_embeddings.pickle', 'wb') as file:
            pickle.dump(self.my_photo_embeddings, file)

    # def load_embeddings(self):
    #     if os.path.exists('face_embeddings.pickle'):
    #         with open('face_embeddings.pickle', 'rb') as file:
    #             self.my_photo_embeddings = pickle.load(file)
    #     else:
    #         self.my_photo_embeddings = [list(map(self.get_face_embedding, person_photos)) for person_photos in
    #                                     self.my_photo_paths]
    #         self.save_embeddings()

    def load_embeddings(self):
        if os.path.exists('src/main/java/group6/Python/face_embeddings.pickle'):
            with open('src/main/java/group6/Python/face_embeddings.pickle', 'rb') as file:
                self.my_photo_embeddings = pickle.load(file)
        else:
            self.calculate_embeddings()
            self.save_embeddings()

    def calculate_embeddings(self):
        for person_photos in self.my_photo_paths:
            person_embeddings = list(map(self.get_face_embedding, person_photos))
            self.my_photo_embeddings.append(person_embeddings)

    def save_embeddings(self):
        with open('face_embeddings.pickle', 'wb') as file:
            pickle.dump(self.my_photo_embeddings, file)
            
    def get_face_embedding(self, img_path):
        print(f"Reading image from {img_path}")
        img = cv2.imread(img_path)
        if img is None or img.size == 0:
            print(f"Error: Unable to read image from {img_path}")
            
            f = open("src/main/java/group6/Python/Connection.txt", "w")
            f.write("error")
            f.close()

            return None
        img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        face_detections = self.detector(img_rgb)

        if len(face_detections) > 0:
            shape = self.predictor(img_rgb, face_detections[0])
            face_embedding = self.facerec.compute_face_descriptor(img_rgb, shape)
            return np.array(face_embedding)

        return None

    def is_person_me(self, detected_face):
        if detected_face is None or detected_face.size == 0:
            return -1

        detected_face_rgb = cv2.cvtColor(detected_face, cv2.COLOR_BGR2RGB)
        face_detections = self.detector(detected_face_rgb)

        if len(face_detections) > 0:
            shape = self.predictor(detected_face_rgb, face_detections[0])
            detected_face_embedding = self.facerec.compute_face_descriptor(detected_face_rgb, shape)
            detected_face_embedding = np.array(detected_face_embedding)

            min_distance = float("inf")
            recognized_person_idx = -1

            for person_idx, person_embeddings in enumerate(self.my_photo_embeddings):
                for person_embedding in person_embeddings:
                    if person_embedding is None:
                        continue

                    distance = np.linalg.norm(person_embedding - detected_face_embedding)

                    if distance < min_distance:
                        min_distance = distance
                        recognized_person_idx = person_idx

            if min_distance < 0.6:
                # print(f"Person {recognized_person_idx + 1}")
                return recognized_person_idx
            else:
                print("Different person")
                f = open("src/main/java/group6/Python/Connection.txt", "w")
                f.write("unknown")
                f.close()
                sys.exit()
                return -1

        return -1

    def run(self):
        if not self.capture.isOpened():
            print("Error: Unable to open webcam.")
            f = open("src/main/java/group6/Python/Connection.txt", "w")
            f.write("error")
            f.close()
            return

        self.capture.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
        self.capture.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

        cv2.namedWindow("Face Recognition", cv2.WINDOW_AUTOSIZE)
        while True:
            ret, frame = self.capture.read()
            if not ret:
                print("Error: Unable to read frame from webcam.")
                f = open("src/main/java/group6/Python/Connection.txt", "w")
                f.write("error")
                f.close()
                break

            face_detections = self.detector(frame)

            for d in face_detections:
                detected_face = frame[d.top():d.bottom(), d.left():d.right()]
                person_idx = self.is_person_me(detected_face)
                if person_idx != self.last_recognized_person_idx:
                    self.last_recognized_person_idx = person_idx
                    if person_idx != -1:
                        person_name = self.person_names[person_idx]
                        print(f"{person_name} has been detected")
                        f = open("src/main/java/group6/Python/Connection.txt", "w")
                        f.write(person_name)
                        f.close()
                        sys.exit()

                    else:
                        print("Unknown person has been detected")
                        f = open("src/main/java/group6/Python/Connection.txt", "w")
                        f.write("unknown")
                        f.close()
                        sys.exit()

                # Determine the border color
                border_color = (0, 0, 255)  # Red
                if person_idx != -1:
                    border_color = (0, 255, 0)  # Green

                # Draw border around the frame
                cv2.rectangle(frame, (d.left(), d.top()), (d.right(), d.bottom()), border_color, 3)


                # print("Is this person:", person_idx + 1 if person_idx != -1 else "Unknown")
                # print("Is this me?", is_me)
                # if not is_me:
                #     self.ask_for_password()
            cv2.imshow("Face Recognition", frame)

            if cv2.waitKey(1) & 0xFF == ord("q") or cv2.getWindowProperty("Face Recognition", cv2.WND_PROP_VISIBLE) < 1:
                break

        self.capture.release()
        cv2.destroyAllWindows()


if __name__ == "__main__":
    f = open("src/main/java/group6/Python/Connection.txt", "w")
    f.write("loading")
    f.close()
    face_recognition = IsItMe()
    face_recognition.run()
