# Inspired by https://github.com/nicknochnack/FaceDetection/blob/main/FaceDetection.ipynb

import os
import cv2
import albumentations as alb

# Split into individual changes

index = 'brightness'
augmentor = alb.Compose([alb.RandomBrightnessContrast(brightness_limit=1, always_apply=True)])

# index = 'contrast'
# augmentor = alb.Compose([alb.RandomBrightnessContrast(contrast_limit=1, always_apply=True)])

# index = 'rgb'
# augmentor = alb.Compose([alb.RGBShift(r_shift_limit=50, g_shift_limit=50, b_shift_limit=50, always_apply=True)])

for partition in ['animals', 'faces', 'rooms']:
    for image in os.listdir(os.path.join(partition)):
        img = cv2.imread(os.path.join(partition, image))
        n1, n2, _ = img.shape

        try:
            # Creates a variation from the base image based on the input parameter
            augmented = augmentor(image=img)
            cv2.imwrite(os.path.join('aug_data', partition, index, image), augmented['image'])
        except Exception as e:
            print(e)
