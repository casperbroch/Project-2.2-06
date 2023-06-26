import os
import cv2
import time


def face_detection_opencv(img):
    face_detections = classifier.detectMultiScale(img)
    if len(face_detections) > 0:
        return True
    else:
        return False


# Initializing detector
# Taken from https://github.com/opencv-java/face-detection/blob/master/resources/lbpcascades/lbpcascade_frontalface.xml
xml_file = "lbpcascade_frontalface.xml"
classifier = cv2.CascadeClassifier(xml_file)

for partition in ['animals', 'faces', 'rooms']:
    for category in ['brightness', 'contrast', 'rgb']:
        counter = 0
        start_time = time.time()
        path = os.path.join('aug_data', partition, category)
        # Loops through all the images in the category for each partition
        for image in os.listdir(path):
            img_path = path + '\\' + image
            img = cv2.imread(img_path)
            if face_detection_opencv(img):
                # Increments number of faces detected
                counter += 1
        duration = time.time() - start_time
        if partition == 'faces':
            # 45 pictures of faces per category
            accuracy = counter / 45
        else:
            # 15 pictures of rooms per category
            accuracy = (15-counter)/15
        print('The accuracy for', partition, 'in category', category, 'is', accuracy, 'after', duration, 'seconds.')
