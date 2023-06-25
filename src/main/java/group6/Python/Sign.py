import sys

import cv2
import mediapipe as mp
import imutils
import os
import numpy as np
from sklearn.neighbors import KNeighborsClassifier


class Sign:
    mpHands = mp.solutions.hands
    hands = mpHands.Hands()
    mpDraw = mp.solutions.drawing_utils

    # dataset: https://github.com/4Tsuki4/Handy-Sign-Language-Detection/tree/main/data
    labels_dict = {0: "hello", 1: "i love you", 2: "yes", 3: "good", 4: "bad", 5: "okay", 6: "you", 7: "i/i'm",
                   8: "why", 9: "no"}

    @staticmethod
    def process_image(img):
        gray_image = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        results = Sign.hands.process(gray_image)
        return results

    @staticmethod
    def draw_hand_connections(img, results):
        if results.multi_hand_landmarks:
            for handLms in results.multi_hand_landmarks:
                for id, lm in enumerate(handLms.landmark):
                    h, w, c = img.shape
                    cx, cy = int(lm.x * w), int(lm.y * h)
                    cv2.circle(img, (cx, cy), 10, (0, 255, 0), cv2.FILLED)

                Sign.mpDraw.draw_landmarks(img, handLms, Sign.mpHands.HAND_CONNECTIONS)
            return img

    # Extracting features from hand landmarks
    @staticmethod
    def extract_features(landmarks):
        features = []
        for lm in landmarks.landmark:
            features.extend([lm.x, lm.y, lm.z])
        return np.array(features)

    # Comparing features with dataset images
    @staticmethod
    def compare_hand_position(features):
        min_distance = float("inf")
        label = None

        for i in range(10):
            folder_path = os.path.join("src/main/java/group6/Python/data", str(i))
            for file in os.listdir(folder_path):
                img_path = os.path.join(folder_path, file)
                img = cv2.imread(img_path)
                results = Sign.process_image(img)

                if results.multi_hand_landmarks:
                    sample_features = Sign.extract_features(results.multi_hand_landmarks[0])
                    distance = np.linalg.norm(features - sample_features)

                    if distance < min_distance:
                        min_distance = distance
                        label = i

        return Sign.labels_dict[label]

    @staticmethod
    def prepare_dataset():
        X = []
        y = []

        for i in range(10):
            f = open("src/main/java/group6/Python/ConnectionHand.txt", "w")
            towrite = "Iteration number "+ str(i+1) + "/10 of launching hand signals"
            f.write(towrite)
            f.close()

            folder_path = os.path.join("src/main/java/group6/Python/data", str(i))
            for file in os.listdir(folder_path):
                img_path = os.path.join(folder_path, file)
                img = cv2.imread(img_path)
                results = Sign.process_image(img)

                if results.multi_hand_landmarks:
                    features = Sign.extract_features(results.multi_hand_landmarks[0])
                    X.append(features)
                    y.append(i)

        return np.array(X), np.array(y)

    @staticmethod
    def main():
        f = open("src/main/java/group6/Python/ConnectionHand.txt", "w")
        f.write("loading")
        f.close()

        X, y = Sign.prepare_dataset()
        knn = KNeighborsClassifier(n_neighbors=3)
        knn.fit(X, y)

        cap = cv2.VideoCapture(0)
        label_last = None
        while True:
            success, image = cap.read()

            if not success:
                print("Failed to read frame")
                continue

            image = imutils.resize(image, width=500, height=500)
            results = Sign.process_image(image)
            Sign.draw_hand_connections(image, results)

            if results.multi_hand_landmarks:
                features = Sign.extract_features(results.multi_hand_landmarks[0])
                position = knn.predict(features.reshape(1, -1))
                label = Sign.labels_dict[position[0]]

                if label_last != label:
                    label_last = label
                    print("Detected position:", label)

                f = open("src/main/java/group6/Python/ConnectionHand.txt", "w")
                f.write(label)
                f.close()
                if label == "bad":
                    sys.exit()

                cv2.putText(image, label, (20, 50), cv2.FONT_HERSHEY_SIMPLEX, 2, (255, 0, 0), 3)
            cv2.imshow("Hand tracker", image)

            if cv2.waitKey(1) == ord('q'):
                cap.release()
                cv2.destroyAllWindows()


if __name__ == "__main__":
    Sign.main()
