
# Multi-modal Digital Assistant - Group 06

This project is about the creation of a multi-modal digital assistant. It is part of a study about digital assistants and their functionalities. The client for this research is Maastricht University. This project is fully made using Java and the GUI is made using JavaFX (references at the end of this read.me). 

This project is currently at an early point of development, more features will be added later on.

## Screenshots
### GUI (chat interface)
![](https://cdn.discordapp.com/attachments/1072880041194692639/1090241892685447178/image.png)
### Face Recognition
![](https://cdn.discordapp.com/attachments/1072880041194692639/1122874704785588324/facerecognition.PNG)
### Hand Sign Recognition
![](https://cdn.discordapp.com/attachments/1072880041194692639/1122872687006601246/goodrecognition.PNG)

## Features
This project in the current version supports a number of features. They are listed below.

- Face detection & recognition 
- Hand sign detection
- Light/dark mode toggle for the chat interface
- Scrollable chat box
- Asking questions in form of added skills
- CYK algorithm (skills editing functionality)
    - Adding new Skills
    - Deleting Skills
    - Adding/deleting actions in existing Skills
    - Getting an overview of all the created Skills
- Google Calendar API connection
    - Insert new events
    - Delete existing events
    - Get the next 10 upcoming events
    - Fetch a specific event
- ChatGPT functionality

## Run Locally

Clone the project

```bash
  git clone https://github.com/casperbroch/Project-2.2-06
```
Then, build the maven project so you obtain the projects' dependencies.

Go to the projectory directory where the main class is located

```bash
  cd core
  cd src
  cd main
  cd java
  cd group6
```

Finally, go to the Start.java file and run its main method located at the bottom.


## Face detection & recognition
This project contains a python project which is responsible for the face detection feature. This python project is opened simultaneously when the java project is launched (see section "Run Locally"). The python project will open a window which will contain the users' webcam, once a face on the webcam is found (indicated by a red box), the screen (and thus python script) terminates itself and launches the JavaFX GUI, from which the user can utilize the other features of this project. The face detection algorithm is made using OpenCV.

The latest update of the digital assistant now also contains person recognition. When a person is recognized, he/she is greeted by their name. If the person is unknown to the assistant, it will ask for the user to take a picture. This picture is then saved in the asistant's database which will be utilized to recognize the person when they try to log in the next time.

## Hand Sign Recognition
The latest version of the assistant contains hand sign recognition software. This software is still being tested but has many posiblities for future uses. It is launched after a person is recognized and when the user has entered the GUI. It is visualized by a webcam screen which shows the sign the user is making with their hand. 

This software can be utilized to further enhance the user's experience with the assistant. To demonstrate its capabilities a single example action has been made. This action is the 'bad' hand signal (thumbs down). When the user makes this handsignal in front of the webcam, the digital assistant closes. 

## Connect with Google Account
The API Google Calendar feature needs to connect with a Google account. Not every Google account will be to connect with this application because it is still under development. We have a free to use Google account which is white-listed and which you can use to log-in when Google asks you to after you make a request. The log-in details are:

Address: jacktediore@gmail.com

Password: Tetrahydro72$

## Ask ChatGPT something
You can now ask ChatGPT a question using the "Ask me something" skill. It will send a request to the OpenAI servers and if the request iss valid, an answer wil be returned using ChatGPT 3.5 - Turbo model.

Please note, the secret token has to be replaced in the class as it cannot be uploaded to GitHub. Please contact Tomas Schreuder to retrieve the latest one.

## Authors
- [@Guilherme Sequeira](https://github.com/sequeiragui)
- [@Casper Bröcheler](https://github.com/casperbroch)
- [@Marian Chen](https://github.com/marchen03)
- [@Eden Sharabi](https://github.com/EdenRochmanSharabi)
- [@Panos Binikos](https://github.com/PanosBin)
- [@TomasSchreuder](https://github.com/tomasschreuder)

## References

- [Java official site](https://www.java.com/nl/)
- [JavaFX official site](https://openjfx.io/)
- [OpenCV official site](https://opencv.org/)

