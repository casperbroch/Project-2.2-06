import os
from facenet_pytorch import MTCNN, InceptionResnetV1
from PIL import Image
import cv2
import time


def face_detection_nn(img):
    img = Image.fromarray(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    boxes, _ = mtcnn.detect(img)
    if boxes is not None:
        return True
    else:
        return False


mtcnn = MTCNN(image_size=640, margin=480)
resnet = InceptionResnetV1(pretrained='vggface2').eval()

for partition in ['animals', 'faces', 'rooms']:
    for category in ['brightness', 'contrast', 'rgb']:
        counter = 0
        start_time = time.time()
        path = os.path.join('aug_data', partition, category)
        # Loops through all the images in the category for each partition
        for image in os.listdir(path):
            img_path = path + '\\' + image
            img = cv2.imread(img_path)
            if face_detection_nn(img):
                # Increments number of faces detected
                counter += 1
        duration = time.time() - start_time
        if partition == 'faces':
            # 45 pictures of faces per category
            accuracy = counter / 45
        else:
            # 15 pictures of rooms/animals per category
            accuracy = (15-counter)/15
        print('The accuracy for', partition, 'in category', category, 'is', accuracy, 'after', duration, 'seconds.')
