import time
import os
import cv2
start_time = time.time()
for partition in ['faces', 'rooms']:
    for category in ['brightness', 'contrast', 'rgb']:
        counter = 0
        start_time = time.time()
        path = os.path.join('aug_data', partition, category)
        # Loops through all the images in the category for each partition
        for image in os.listdir(path):
            img_path = path + '\\' + image
            img = cv2.imread(img_path)
                # Increments number of faces detected
            counter += 1
print((time.time() - start_time), 'seconds')
