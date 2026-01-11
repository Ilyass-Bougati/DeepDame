class ChangePasswordRequest {
  final String oldPassword;
  final String newPassword;

  ChangePasswordRequest(this.newPassword, this.oldPassword);

  Map<String, dynamic> toJson() {
    return {"oldPassword": oldPassword, "newPassword": newPassword};
  }
}
