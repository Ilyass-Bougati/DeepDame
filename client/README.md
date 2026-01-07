# DeepDame README

---

### 🟦 DeepDame Client

A cross-platform **mobile client** for the **DeepDame** checkers game, built with **Flutter**.

---

### 📱 Project Overview

The DeepDame Client is the frontend application of the DeepDame platform.

It targets **Android and iOS** and provides:

- User authentication
- Friend management
- Real-time multiplayer gameplay
- Single-player and AI matches
- Board rendering and game interaction

The architecture emphasizes modularity, separation of concerns, and deterministic game logic.

---

## 📂 Project Structure

All application logic resides in the `lib/` directory:

```
lib/
├── main.dart                    # Application entry point
│
├── dtos/                        # Data Transfer Objects (WebSocket / API payloads)
│   ├── MessageDto.dart
│   └── UserDto.dart
│
├── requests/                    # HTTP / STOMP request models
│   ├── EmptyRequest.dart
│   ├── LoginRequest.dart
│   └── RegisterRequest.dart
│
├── models/                      # Domain models
│   └── User.dart
│
├── game-engine/                 # Pure game logic (UI-agnostic)
│   ├── logic/
│   │   ├── game_engine.dart     # Core game orchestration
│   │   └── move_validator.dart  # Rule enforcement & move validation
│   │
│   ├── model/
│   │   ├── board.dart
│   │   ├── game_state.dart
│   │   ├── move.dart
│   │   ├── piece.dart
│   │   ├── piece_type.dart
│   │   └── position.dart
│   │
│   └── mainFunction.dart        # Engine entry / helpers
│
├── pages/                       # Full-screen application views
│   ├── Landing.dart             # Initial welcome screen
│   ├── Connect.dart             # Login / registration
│   ├── Friends.dart             # Friend list & invitations
│   ├── Game.dart                # Active game screen
│   ├── General.dart             # Navigation & layout
│   └── Preferences.dart         # User settings
│
├── prefabs/                     # Reusable UI components
│   ├── GameBoard.dart           # Board renderer
│   ├── GamePiece.dart           # Individual piece widget
│   ├── Input.dart               # Custom text input field
│   ├── NavBarButton.dart        # Navigation button
│   ├── SendButton.dart          # Message / action button
│   ├── SubmitButton.dart        # Primary action button
│   └── ValidationController.dart# Input validation logic
│
├── static/
│   └── Utils.dart               # Utility helpers & constants
```

---

### 📂 Architecture Overview

The project follows a layered architecture:

- **UI Layer**: Pages and reusable prefabs
- **Domain Layer**: Game engine and domain models
- **Networking Layer**: DTOs and request abstractions
- **Utility Layer**: Shared helpers and constants

---

### 🧠 Game Engine

The game engine is a standalone Dart module, fully independent from the UI.

**Responsibilities**

- Move validation
- Turn management
- Capture and promotion rules
- Win-condition detection
- Board state transitions

**Key Characteristics**

- Uses immutable models (`Board`, `Move`, `GameState`)
- Deterministic and testable
- Reusable for AI, multiplayer, and local games

---

### 🧩 UI Components (Prefabs)

Reusable widgets ensure consistency and reduce duplication:

- GameBoard – Board renderer
- GamePiece – Piece rendering
- Input – Standardized text input
- SubmitButton / SendButton – Action buttons
- ValidationController – Input validation logic

---

### 📄 Pages

Each page represents a full screen:

- Landing – Entry screen
- Connect – Login & registration
- Friends – Friend list and invitations
- Game – Live match screen
- Preferences – User settings

Pages orchestrate UI components and react to backend events.

---

### 🌐 Networking & Communication

- DTOs define serialized REST and WebSocket payloads
- Request models encapsulate API interactions
- WebSockets enable real-time gameplay updates
- No polling — all state changes are event-driven

---

### 🛠️ Getting Started

**Prerequisites**

- Flutter SDK
- Xcode (iOS)
- Android Studio or VS Code

**Steps**

1. Clone the repository
2. Run `flutter pub get`
3. Run `flutter run`

---

### 🚀 Supported Platforms

- Android
- iOS