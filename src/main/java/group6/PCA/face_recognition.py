import linecache
import cv2
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.decomposition import PCA
from sklearn.svm import SVC
import warnings
from face_detection import FaceDetection


# Shows original 64x64 images of faces (40 people with 10 images each)
def show_orignal_images(pixels):
    fig, axes = plt.subplots(6, 10, figsize=(11, 7),
                             subplot_kw={'xticks': [], 'yticks': []})
    for i, ax in enumerate(axes.flat):
        ax.imshow(np.array(pixels)[i].reshape(64, 64), cmap='gray')
    plt.show()


# Displaying eigenfaces
def show_eigenfaces(pca):
    fig, axes = plt.subplots(3, 8, figsize=(9, 4),
                             subplot_kw={'xticks': [], 'yticks': []})
    for i, ax in enumerate(axes.flat):
        ax.imshow(pca.components_[i].reshape(64, 64), cmap='gray')
        ax.set_title("PC " + str(i + 1))
    plt.show()


def get_name(idx):
    idx -= 400
    if idx < 1:
        # print("Unrecognized face.")
        return None
    return linecache.getline('names.txt', int(idx))


# Removes warning for df/np array conversions
warnings.filterwarnings("ignore")

df = pd.read_csv("face_data.csv")
# Split and normalize
labels = df["target"]
faces = df.drop(["target"], axis=1)

# -
# Subtracting average face
# avg_face = pd.read_csv('avg_face.csv')
avg_face = faces.mean()
avg_face = avg_face.T
avg_face.columns = avg_face.columns.astype(str)
avg_face = avg_face.iloc[0]
avg_face *= 255.0
faces = faces.subtract(avg_face)
# -

pca = PCA(n_components=faces.shape[0]).fit(faces)
faces_pca = pca.transform(faces)
clf = SVC(kernel='rbf', C=10000, gamma=0.01)
clf = clf.fit(faces_pca, labels)

# Real-time face recognition
detector = FaceDetection()

cam = cv2.VideoCapture(0)
while True:
    ret, frame = cam.read()
    cv2.imshow('Stream', frame)

    img = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    img = detector.detect_face(img)

    if not isinstance(img, type(None)):
        img = cv2.resize(img, (64, 64))
        # - Subtracting average face
        img = np.subtract(img, np.reshape(np.array(avg_face), (64, 64)))
        # -
        img = np.divide(img, 255.0)
        img = np.array(img).reshape(1, -1)
        img = pca.transform(img)
        pred = clf.predict(img)
        # print(pred)
        name = get_name(pred)
        if name:
            print(f'Hello, {name}')
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
cam.release()
cv2.destroyAllWindows()
