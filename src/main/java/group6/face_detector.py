from facenet_pytorch import MTCNN, InceptionResnetV1
from PIL import Image, ImageDraw
import cv2

# Pre-trained NN from https://github.com/timesler/facenet-pytorch#use-this-repo-in-your-own-git-project

mtcnn = MTCNN(image_size=640, margin=480)
resnet = InceptionResnetV1(pretrained='vggface2').eval()
cam = cv2.VideoCapture(0)
ret, frame = cam.read()

while(True):

    frame = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
    boxes, _ = mtcnn.detect(frame)
    frame_draw = frame.copy()

    draw = ImageDraw.Draw(frame_draw)
    if boxes is not None:
        # # Actually draws the boxes around the faces
        # for box in boxes:
        #     draw.rectangle(box.tolist(), outline=(255, 0, 0), width=6)
        # frame_draw.show()
        print('Face detected.')

    else:
        print('No face detected.')

    ret, frame = cam.read()
