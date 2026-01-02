class RegisterRequest {
  String username;
  String email;
  String password;

  RegisterRequest(this.username, this.email, this.password);

  Map<String, dynamic> toJson() {
    return {"username": username, "email": email,"password":password};
  }
}
