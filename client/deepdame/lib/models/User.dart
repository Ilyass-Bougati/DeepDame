class User {
  final String? id;
  final String? username;
  final String? email;
  final bool? emailValidated;
  final bool? bannedFromChat;
  final bool? bannedFromApp;

  const User({
    this.id,
    required this.username,
    this.email,
    required this.emailValidated,
    required this.bannedFromChat,
    required this.bannedFromApp,
  });
}
