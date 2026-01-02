import 'package:deepdame/models/User.dart';

class UserDTO {
  final String? id;
  final String? username;
  final String? email;
  final bool emailValidated;
  final bool bannedFromChat;
  final bool bannedFromApp;

  const UserDTO({
    this.id,
    this.username,
    this.email,
    required this.emailValidated,
    required this.bannedFromChat,
    required this.bannedFromApp,
  });

  static UserDTO fromJson(Map<String, dynamic> decodedJson) {
    return UserDTO(
      id: decodedJson['id'],
      username: decodedJson['username'],
      email: decodedJson['email'],
      emailValidated: decodedJson['emailValidated'],
      bannedFromApp: decodedJson['bannedFromApp'],
      bannedFromChat: decodedJson['bannedFromChat'],
    );
  }

  User toUser() {
    return User(
      id: id,
      username: username,
      email: email,
      emailValidated: emailValidated,
      bannedFromChat: bannedFromChat,
      bannedFromApp: bannedFromApp,
    );
  }
}
