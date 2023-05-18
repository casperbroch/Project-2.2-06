import os
import time
import uuid
import cv2

IMAGES_PATH = 'mda/CNN/data/images/'
number_images = 30

cap = cv2.VideoCapture(0)
for num in range(number_images):
    print('Collecting image {}'.format(num))
    ret, frame = cap.read()
    if ret:
        name = os.path.join(IMAGES_PATH,f'{str(uuid.uuid1())}.jpg')
        cv2.imwrite(name, frame)
        cv2.imshow('frame', frame)
        time.sleep(0.5)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
    else:
        print('Unable to return image.')
        break
cap.release()
cv2.destroyAllWindows()
