from retinaface import RetinaFace
import math
import numpy as np
import cv2
from PIL import Image


def resize(image, scale):
    x, y, h = image.shape
    return cv2.resize(image, (y // scale, x // scale))


# Alignment taken from https://www.youtube.com/watch?v=WA9i68g4meI
# Eye detection taken from https://github.com/rk45825243/Face-eye-detection-using-Haar-Cascade-classifier/blob/master/Face%20and%20eye%20detect%20using%20haar%20cascade.py
def align_face(path):
    # Aligning the image
    img = cv2.imread(path, 0)
    resp = RetinaFace.detect_faces(path)
    if isinstance(resp, tuple):
        print("No face detected.")
        return None
    print(resp)
    if 'face_2' in resp.keys():
        print("One person only in front of the camera!")
        return None
    # TODO: TypeError: tuple indices must be integers or slices, not str
    x1, y1 = resp['face_1']['landmarks']['right_eye']
    x2, y2 = resp['face_1']['landmarks']['left_eye']

    a, b = y2 - y1, x2 - x1
    c = math.sqrt(a * a + b * b)
    cos_alpha = (b * b + c * c - a * a) / (2 * b * c)
    alpha = np.arccos(cos_alpha)  # Radius
    alpha = (alpha * 180) / math.pi
    aligned = Image.fromarray(img)
    if a * b < 0:
        alpha = -alpha
    aligned = np.array(aligned.rotate(alpha))

    # Keeping aligned image as a placeholder temp.jpg
    cv2.imwrite('temp.jpg', aligned)
    aligned = cv2.imread('temp.jpg')

    # Finding new eye coordinates
    resp = RetinaFace.detect_faces(aligned)
    x1, y1 = resp['face_1']['landmarks']['right_eye']
    x2, y2 = resp['face_1']['landmarks']['left_eye']

    # Distance between eyes should be roughly 35 pixels
    dist = x2 - x1
    scalar = math.ceil(dist/35)
    aligned = resize(aligned, scalar)

    # Location of right eye
    x1, y1 = int(x1 // scalar), int(y1 // scalar)
    # Should be moved to pixel space (5,5)
    start_x, start_y = x1 - 5*scalar, y1 - 5*scalar
    end_x, end_y = x1 + 59*scalar, y1 + 59*scalar
    box = aligned[start_y:end_y, start_x:end_x]
    box = box[0:64, 0:64]
    return cv2.cvtColor(box, cv2.COLOR_BGR2GRAY)
