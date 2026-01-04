import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/prefabs/ValidationController.dart';
import 'package:deepdame/requests/LoginRequest.dart';
import 'package:deepdame/requests/RegisterRequest.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import '../static/Utils.dart';

class Connect extends StatelessWidget {
  final bool hasAccount;
  const Connect(this.hasAccount, {super.key});

  @override
  Widget build(BuildContext context) {
    return hasAccount ? _loginPage(context) : _registerPage(context);
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
                              "",
                              () {
                                return true;
                              },
                            ),
                          ),
                        ),

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
                              "",
                              () {
                                return true;
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
                    LoginRequest request = LoginRequest(
                      email_controller.getController().text,
                      password_controller.getController().text,
                    );

                    Future<void> login() async {
                      await Utils.api_postRequest(
                        request,
                        "auth/login",
                        api_controller.text,
                      ).onError((e, stacktrace) {
                        if (context.mounted) {
                          Navigator.pop(context);
                        }
                        showErrorDialog(context, e.toString());
                        throw Exception();
                      });
                    }

                    void _login() async {
                      try {
                        await login();
                        FocusManager.instance.primaryFocus?.unfocus();
                        Navigator.pop(context);

                        WidgetsBinding.instance.addPostFrameCallback((_) {
                          Navigator.pop(context);
                          Navigator.pushReplacement(
                            context,
                            PageRouteBuilder(
                              pageBuilder: (context, animation1, animation2) =>
                                  Landing(),
                              transitionDuration: Duration.zero,
                              reverseTransitionDuration: Duration.zero,
                            ),
                          );
                        });
                      } catch (e) {
                        return;
                      }
                    }

                    _login();
                    Utils.showLoadingDialog(context);
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
    String usernameErrorStr = "Invalid username";

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
                            child: Input.onLostFocus(
                              "JohnDoe67",
                              TextInputType.name,
                              map['username']?.getController()
                                  as TextEditingController,
                              usernameErrorStr,
                              () {
                                map['username']?.setState(
                                  validator(
                                    map['username']?.getController()
                                        as TextEditingController,
                                    "username",
                                  ),
                                );
                                return (map['username']?.getState() as bool);
                              },
                              () async {
                                return (await Utils.api_getRequest(
                                      "auth/checkUsername/${map['username']!.getController().text}",
                                      api_controller.text,
                                    )
                                    as Map)['message'];
                              },
                              "Username unavailable",
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

                      Future<void> register() async {
                        await Utils.api_postRequest(
                          request,
                          "auth/register",
                          api_controller.text,
                        ).onError((e, stacktrace) {
                          if (context.mounted) {
                            Navigator.pop(context);
                          }
                          showErrorDialog(context, e.toString());
                          throw Exception();
                        });
                      }

                      void _register() async {
                        try {
                          await register();
                          FocusManager.instance.primaryFocus?.unfocus();
                          Navigator.pop(context);

                          WidgetsBinding.instance.addPostFrameCallback((_) {
                            Navigator.pop(context);
                          });
                        } catch (e) {
                          return;
                        }
                      }

                      _register();
                      Utils.showLoadingDialog(context);
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
