# Onpa Assistant - Thesis App

**Onpa Assistant** is an Android application designed to support individuals with hearing or speaking loss. The app listens to spoken language, generates context-aware response suggestions using an AI model, allows the user to select and play a response using Text-to-Speech (TTS), and detects important environmental sounds (such as sirens or alarms) to help protect users in critical situations. 

## 📱 Features

- 🎤 **Speech Recognition**: Converts live speech into text.
- 🔊 **Sound Detection**: Identifies key environmental sounds like sirens or alarms using a machine learning model (e.g. YAMNet).
- 🤖 **AI Response Generator**: Suggests 3 personalized responses using the Gemini API, taking into account the user's context and preferences to improve communication relevance.
- 🔊 **Text-to-Speech**: Plays the selected response aloud to the other person.
- 🎨 **Accessible UI**: Designed with large buttons and minimal interaction for accessibility.

## 🧠 Technologies Used

- **Kotlin**, **Jetpack Compose** – for modern and reactive UI
- **MVVM Modular Architecture** – separates concerns into clear layers (App, Data, Domain) for maintainability and scalability
- **Room Database** – for local storage of user data
- **User Prefences** - for user prefences data
- **TensorFlow Lite / YAMNet** – for on-device sound classification
- **Gemini API** – for generating AI-powered, context-aware response suggestions
- **Google SpeechRecognizer** – for STT (Speech-to-Text)
- **Android TextToSpeech API** – for TTS
- **Custom Themes** – for visual accessibility (e.g. color customization)

## 📱 Video 

https://github.com/user-attachments/assets/98bdc3e4-48b7-4e92-8133-ae7bd1996a4c

