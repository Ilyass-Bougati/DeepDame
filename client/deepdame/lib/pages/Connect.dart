import 'dart:io';

import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/prefabs/ValidationController.dart';
import 'package:deepdame/requests/LoginRequest.dart';
import 'package:deepdame/requests/RegisterRequest.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../static/Utils.dart';

class Connect extends StatelessWidget {
  final bool login;
  const Connect(this.login, {super.key});

  @override
  Widget build(BuildContext context) {
    return login ? _loginPage(context) : _registerPage(context);
  }

  void showLoadingDialog(BuildContext context) {
    showDialog(
      context: context,
      barrierDismissible:
          false, //comment to make the popup dismissible for debug purposes.
      builder: (BuildContext context) {
        return AlertDialog(
          contentPadding: EdgeInsets.all(25),
          backgroundColor: Color.fromARGB(255, 253, 251, 247),
          content: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircularProgressIndicator.adaptive(
                backgroundColor: Color.fromARGB(255, 170, 188, 180),
              ),
              SizedBox(width: 20),
              Text(
                "Connecting ..",
                style: GoogleFonts.nunito(
                  color: Color.fromARGB(255, 170, 188, 180),
                  fontWeight: FontWeight.bold,
                  fontSize: 20,
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  void showErrorDialog(BuildContext context, String error) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(error)));
  }

  bool validator(TextEditingController controller, String type) {
    String content = controller.text;
    switch (type) {
      case "username":
        if (content.contains(" ")) {
          return false;
        }
        return true;
      case "email":
        final emailRegex = RegExp(
          r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$',
        );
        return emailRegex.hasMatch(content);
      case "password":
        //Temporary password conditions
        final passwordRegex = RegExp(
          r'^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!@#\$&*~]).{8,}$',
        );
        return passwordRegex.hasMatch(content);
      default:
        return false;
    }
  }

  Widget _loginPage(BuildContext context) {
    TextEditingController api_controller = TextEditingController();

    ValidationController email_controller = ValidationController();
    ValidationController password_controller = ValidationController();
    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      resizeToAvoidBottomInset: true,
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(110),
        child: Container(
          padding: EdgeInsets.only(top: 60),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                "Login",
                style: GoogleFonts.lora(
                  color: Color.fromARGB(255, 170, 188, 180),
                  fontSize: 50,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ),
      body: CustomScrollView(
        physics: NeverScrollableScrollPhysics(),
        hitTestBehavior: HitTestBehavior.translucent,
        slivers: [
          SliverFillRemaining(
            hasScrollBody: false,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                SingleChildScrollView(
                  hitTestBehavior: HitTestBehavior.translucent,
                  child: Container(
                    padding: EdgeInsets.only(right: 30, left: 30),
                    child: Column(
                      children: [
                        //Username
                        Align(
                          alignment: AlignmentGeometry.centerLeft,
                          child: Text(
                            "Email :",
                            style: GoogleFonts.nunito(
                              color: Color.fromARGB(255, 170, 188, 180),
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "example@email.com",
                              TextInputType.emailAddress,
                              email_controller.getController(),
                              "email is invalid",
                              () {
                                email_controller.setState(
                                  validator(
                                    email_controller.getController(),
                                    "email",
                                  ),
                                );
                                return email_controller.getState();
                              },
                            ),
                          ),
                        ),

                        //Password & Confirm password
                        SizedBox(height: 50),
                        Align(
                          alignment: AlignmentGeometry.centerLeft,
                          child: Text(
                            "Password :",
                            style: GoogleFonts.nunito(
                              color: Color.fromARGB(255, 170, 188, 180),
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "password",
                              TextInputType.visiblePassword,
                              password_controller.getController(),
                              "password is invalid",
                              () {
                                password_controller.setState(
                                  validator(
                                    password_controller.getController(),
                                    "password",
                                  ),
                                );
                                return password_controller.getState();
                              },
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "Api",
                              TextInputType.text,
                              api_controller,
                              "",
                              () {
                                return true;
                              },
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Submitbutton(
                  "Login",
                  Color.fromARGB(255, 170, 188, 180),
                  Color.fromARGB(255, 119, 133, 127),
                  () {
                    if (email_controller.getState() == false ||
                        password_controller.getState() == false) {
                      if (email_controller.getState() == false) {
                        print("Username is invalid !");
                      }

                      if (password_controller.getState() == false) {
                        print("Password is invalid !");
                      }
                    } else {
                      LoginRequest request = LoginRequest(
                        email_controller.getController().text,
                        password_controller.getController().text,
                      );

                      void login() async {
                        await Utils.api_postRequest(
                          request,
                          "auth/login",
                          api_controller.text,
                        ).onError((e, stacktrace) {
                          if (context.mounted) {
                            Navigator.pop(context);
                          }
                          showErrorDialog(context, e.toString());
                        });

                        List<Cookie> cookies = await persistCookieJar
                            .loadForRequest(
                              Uri.parse("${api_controller.text}/auth/login"),
                            );

                        if (cookies.isEmpty) {
                          print("📭 No cookies found ");
                        } else {
                          print("--- 🍪 Cookies ");
                          for (var cookie in cookies) {
                            print("Name: ${cookie.name}");
                            print("Value: ${cookie.value}");
                            print("Expires: ${cookie.expires}");
                            print("--------------------------");
                          }
                        }
                      }

                      login();
                      showLoadingDialog(context);
                    }
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _registerPage(BuildContext context) {
    TextEditingController api_controller = TextEditingController();
    Map<String, ValidationController> map = <String, ValidationController>{};

    final entries = <String, ValidationController>{
      "username": ValidationController(),
      "email": ValidationController(),
      "password": ValidationController(),
      "confirmed password": ValidationController(),
    };
    map.addEntries(entries.entries);

    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      resizeToAvoidBottomInset: true,
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(110),
        child: Container(
          padding: EdgeInsets.only(top: 60),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                "Register",
                style: GoogleFonts.lora(
                  color: Color.fromARGB(255, 170, 188, 180),
                  fontSize: 50,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ),
      body: CustomScrollView(
        physics: NeverScrollableScrollPhysics(),
        hitTestBehavior: HitTestBehavior.translucent,
        slivers: [
          SliverFillRemaining(
            hasScrollBody: false,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                SingleChildScrollView(
                  hitTestBehavior: HitTestBehavior.translucent,
                  child: Container(
                    padding: EdgeInsets.only(right: 30, left: 30),
                    child: Column(
                      children: [
                        //Username
                        Align(
                          alignment: AlignmentGeometry.centerLeft,
                          child: Text(
                            "Username :",
                            style: GoogleFonts.nunito(
                              color: Color.fromARGB(255, 170, 188, 180),
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "JohnDoe67",
                              TextInputType.name,
                              map['username']?.getController()
                                  as TextEditingController,
                              "username invalid",
                              () {
                                map['username']?.setState(
                                  validator(
                                    map['username']?.getController()
                                        as TextEditingController,
                                    "username",
                                  ),
                                );
                                return map['username']?.getState() as bool;
                              },
                            ),
                          ),
                        ),

                        //Email
                        SizedBox(height: 50),
                        Align(
                          alignment: AlignmentGeometry.centerLeft,
                          child: Text(
                            "Email :",
                            style: GoogleFonts.nunito(
                              color: Color.fromARGB(255, 170, 188, 180),
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "example@email.com",
                              TextInputType.emailAddress,
                              map['email']?.getController()
                                  as TextEditingController,
                              "email invalid",
                              () {
                                map['email']?.setState(
                                  validator(
                                    map['email']?.getController()
                                        as TextEditingController,
                                    "email",
                                  ),
                                );
                                return map['email']?.getState() as bool;
                              },
                            ),
                          ),
                        ),

                        //Password & Confirm password
                        SizedBox(height: 50),
                        Align(
                          alignment: AlignmentGeometry.centerLeft,
                          child: Text(
                            "Password :",
                            style: GoogleFonts.nunito(
                              color: Color.fromARGB(255, 170, 188, 180),
                              fontSize: 20,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "password",
                              TextInputType.visiblePassword,
                              map['password']?.getController()
                                  as TextEditingController,
                              "8+ characters: 1+ Upper, 1+ Digit, 1+ Symbol",
                              () {
                                map['password']?.setState(
                                  validator(
                                    (map['password']?.getController()
                                        as TextEditingController),
                                    "password",
                                  ),
                                );
                                return map['password']?.getState() as bool;
                              },
                            ),
                          ),
                        ),
                        SizedBox(height: 20),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "confirm password",
                              TextInputType.visiblePassword,
                              map['confirmed password']?.getController()
                                  as TextEditingController,
                              "passwords do not match !",
                              () {
                                map['confirmed password']?.setState(
                                  (map['password']?.getController()
                                              as TextEditingController)
                                          .text ==
                                      (map['confirmed password']
                                                  ?.getController()
                                              as TextEditingController)
                                          .text,
                                );
                                return map['confirmed password']?.getState()
                                    as bool;
                              },
                            ),
                          ),
                        ),
                        Align(
                          alignment: Alignment.centerRight,
                          child: SizedBox(
                            child: Input(
                              "Api",
                              TextInputType.text,
                              api_controller,
                              "",
                              () {
                                return true;
                              },
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Submitbutton(
                  "Register",
                  Color.fromARGB(255, 170, 188, 180),
                  Color.fromARGB(255, 119, 133, 127),
                  () {
                    bool isValid = true;
                    for (String s in map.keys.toSet()) {
                      if (map[s]?.getState() == false) {
                        print("Field $s is invalid !");
                        isValid = false;
                        break;
                      }
                    }

                    if (isValid) {
                      RegisterRequest request = RegisterRequest(
                        (map['username']?.getController()
                                as TextEditingController)
                            .text,
                        (map['email']?.getController() as TextEditingController)
                            .text,
                        (map['password']?.getController()
                                as TextEditingController)
                            .text,
                      );

                      void register() async {
                        var resp = await Utils.api_postRequest(
                          request,
                          "auth/register",
                          api_controller.text,
                        );
                        print(resp.data);
                      }

                      register();
                      showLoadingDialog(context);
                    }
                    ;
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
