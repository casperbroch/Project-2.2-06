import cv2


xml_file = "lbpcascade_frontalface.xml"
classifier = cv2.CascadeClassifier(xml_file)
capture = cv2.VideoCapture(0)

capture.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
capture.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

while True:
    ret, frame = capture.read()

    face_detections = classifier.detectMultiScale(frame)
    if len(face_detections) > 0:
        print('good')

    border_color = (0, 0, 255)  # Red
    if len(face_detections) > 0:
        border_color = (0, 255, 0)  # Green

    # Draw border around the frame
    frame_height, frame_width, _ = frame.shape
    cv2.rectangle(frame, (0, 0), (frame_width - 1, frame_height - 1), border_color, 3)

    for (x, y, w, h) in face_detections:
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), 3)

    cv2.imshow("Face Recognition", frame)

    if cv2.waitKey(1) & 0xFF == ord("q"):
        break

    self.capture.release()
    cv2.destroyAllWindows()

