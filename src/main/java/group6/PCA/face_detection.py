import cv2


class FaceDetection:
    def __init__(self):
        self.xml_file = "lbpcascade_frontalface.xml"
        self.classifier = cv2.CascadeClassifier(self.xml_file)

    def detect_face(self, frame):
        face_detections = self.classifier.detectMultiScale(frame)
        # print(f"Detected {len(face_detections)} face")

        # Only accepts if exactly one face has been detected
        if len(face_detections) > 1:
            print("One person only in front of the camera!")
            return None
        for (x, y, w, h) in face_detections:
            print("Person detected!")
            return frame[y:y + h, x:x + w]
        print("No face detected.")
        return None

    cv2.destroyAllWindows()
