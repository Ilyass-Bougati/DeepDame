# DeepDame Client

A cross-platform mobile and desktop client for the **DeepDame** game, built with Flutter.

## 📱 Project Overview

This project serves as the frontend interface for DeepDame. It is designed to run on **iOS**, **Android**, and **macOS**. The application handles user authentication and navigation through a clean, modular structure.

## 📂 Project Structure

The project follows a component-based architecture located in the `lib/` directory:

```text
lib/
├── main.dart               # Application entry point
├── pages/                  # Full-screen views
│   ├── Landing.dart        # Initial welcome screen
│   └── Connect.dart        # User login/registration screen
└── prefabs/                # Reusable UI components
    ├── SubmitButton.dart   # Custom styled action button
    └── Input.dart          # Custom text input field

🧩 Components (Prefabs)
 * SubmitButton.dart: A reusable button widget used for primary actions (e.g., logging in, registering).
 * Input.dart: A standardized text entry field used for capturing user data like usernames and passwords.
📄 Pages
 * Landing.dart: The first screen users see when launching the app.
 * Connect.dart: The screen responsible for user authentication (Login/Register).
🛠️ Getting Started
Prerequisites
 * Flutter SDK (Latest stable version recommended)
 * Xcode (for iOS/macOS)
 * Android Studio or VS Code
Installation
 * Clone the repository:
   git clone [https://github.com/your-username/deepdame-client.git](https://github.com/your-username/deepdame-client.git)
cd deepdame-client

 * Install dependencies:
   flutter pub get

 * Run the application:
   Select your target device and run:
   flutter run

🚀 Supported Platforms
 * iOS (iPhone/iPad)
 * Android
 * macOS (Desktop)
