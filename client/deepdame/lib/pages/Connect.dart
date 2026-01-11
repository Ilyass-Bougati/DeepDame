import 'dart:convert';

import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/prefabs/ValidationController.dart';
import 'package:deepdame/requests/EmptyRequest.dart';
import 'package:deepdame/requests/LoginRequest.dart';
import 'package:deepdame/requests/RegisterRequest.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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

  static bool validator(TextEditingController controller, String type) {
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
    ValidationController email_controller = ValidationController();
    ValidationController password_controller = ValidationController();
    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      resizeToAvoidBottomInset: false,
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
            child: Stack(
              children: [
                Column(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    SingleChildScrollView(
                      hitTestBehavior: HitTestBehavior.translucent,
                      child: Container(
                        padding: EdgeInsets.only(right: 30, left: 30),
                        child: Column(
                          children: [
                            SizedBox(height: 50),
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
                            SizedBox(height: 50),
                          ],
                        ),
                      ),
                    ),
                  ],
                ),
                Positioned(
                  bottom: MediaQuery.of(context).viewInsets.bottom != 0
                      ? MediaQuery.of(context).viewInsets.bottom
                      : 100,
                  left: 0,
                  right: 0,
                  child: Container(
                    alignment: Alignment.bottomCenter,
                    child: Submitbutton(
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
                            Utils.API_URL,
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
                                  pageBuilder:
                                      (context, animation1, animation2) =>
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
                  ),
                ),
                Container(
                  margin: EdgeInsets.only(bottom: 30),
                  child: GestureDetector(
                    onTap: () {
                      var _controller = TextEditingController();

                      showDialog(
                        barrierDismissible: true,
                        context: context,
                        builder: (BuildContext context) {
                          return AlertDialog(
                            contentPadding: EdgeInsets.all(25),
                            backgroundColor: Color.fromARGB(255, 253, 251, 247),
                            content: Column(
                              spacing: 30,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text(
                                  "Reset your password",
                                  style: GoogleFonts.nunito(
                                    fontWeight: FontWeight.bold,
                                    fontSize: 20,
                                    color: Color.fromARGB(111, 55, 62, 59),
                                  ),
                                ),
                                Input(
                                  "Email",
                                  TextInputType.emailAddress,
                                  _controller,
                                  "",
                                  () => true,
                                ),
                                Submitbutton(
                                  "Send email",
                                  Color.fromARGB(255, 170, 188, 180),
                                  Color.fromARGB(255, 119, 133, 127),
                                  () {
                                    if (_controller.text != "") {
                                      void sendConfirmationEmail() async {
                                        bool status = true;
                                        await Utils.api_postRequest(
                                          EmptyRequest(),
                                          "user/forgotPassword/?email=${_controller.text}",
                                          Utils.API_URL,
                                        ).onError((e, trace) {
                                          print(
                                            "failure to send Email !  $e , ${e.toString()}",
                                          );
                                          status = false;
                                        });

                                        Navigator.pop(context);
                                        WidgetsBinding.instance.addPostFrameCallback((
                                          _,
                                        ) {
                                          late TextEditingController
                                          _confCodeController;
                                          if (status) {
                                            Navigator.pop(context);
                                            _confCodeController =
                                                TextEditingController();
                                          }
                                          showDialog(
                                            context: context,
                                            builder: (BuildContext context) => AlertDialog(
                                              contentPadding: EdgeInsets.all(
                                                25,
                                              ),
                                              backgroundColor: Color.fromARGB(
                                                255,
                                                253,
                                                251,
                                                247,
                                              ),
                                              content: Column(
                                                spacing: 30,
                                                mainAxisSize: MainAxisSize.min,
                                                children: [
                                                  Text(
                                                    status
                                                        ? "Email sent !"
                                                        : "Failed to send email",
                                                    style: GoogleFonts.nunito(
                                                      fontWeight:
                                                          FontWeight.bold,
                                                      fontSize: 20,
                                                      color: Color.fromARGB(
                                                        111,
                                                        55,
                                                        62,
                                                        59,
                                                      ),
                                                    ),
                                                  ),
                                                  status
                                                      ? Column(
                                                          children: [
                                                            Text(
                                                              "Check your inbox for the confirmation code",
                                                              style: GoogleFonts.nunito(
                                                                fontWeight:
                                                                    FontWeight
                                                                        .bold,
                                                                fontSize: 15,
                                                                color:
                                                                    Color.fromARGB(
                                                                      111,
                                                                      55,
                                                                      62,
                                                                      59,
                                                                    ),
                                                              ),
                                                            ),
                                                            Input(
                                                              "Confirmation code",
                                                              TextInputType
                                                                  .number,
                                                              _confCodeController,
                                                              "",
                                                              () => true,
                                                            ),
                                                            Submitbutton(
                                                              "Confirm",
                                                              Color.fromARGB(
                                                                255,
                                                                170,
                                                                188,
                                                                180,
                                                              ),
                                                              Color.fromARGB(
                                                                255,
                                                                119,
                                                                133,
                                                                127,
                                                              ),
                                                              () {
                                                                Future<void>
                                                                validateVerificationCode() async {
                                                                  bool
                                                                  verifStatus =
                                                                      true;
                                                                  false;
                                                                  await Utils.api_postRequest(
                                                                    EmptyRequest(),
                                                                    "user/forgotPassword/validateEmail/?validationCode=${_confCodeController.text}",
                                                                    Utils
                                                                        .API_URL,
                                                                  ).onError(
                                                                    (
                                                                      e,
                                                                      trace,
                                                                    ) => verifStatus =
                                                                        false,
                                                                  );

                                                                  Navigator.pop(
                                                                    context,
                                                                  );

                                                                  WidgetsBinding.instance.addPostFrameCallback((
                                                                    _,
                                                                  ) {
                                                                    late TextEditingController
                                                                    _newPwdController;

                                                                    late TextEditingController
                                                                    _confirmedNewPwdController;

                                                                    if (verifStatus) {
                                                                      _newPwdController =
                                                                          TextEditingController();

                                                                      _confirmedNewPwdController =
                                                                          TextEditingController();

                                                                      Navigator.pop(
                                                                        context,
                                                                      );
                                                                    }
                                                                    showDialog(
                                                                      context:
                                                                          context,
                                                                      builder:
                                                                          (
                                                                            BuildContext
                                                                            context,
                                                                          ) => AlertDialog(
                                                                            contentPadding: EdgeInsets.all(
                                                                              25,
                                                                            ),
                                                                            backgroundColor: Color.fromARGB(
                                                                              255,
                                                                              253,
                                                                              251,
                                                                              247,
                                                                            ),
                                                                            content: Column(
                                                                              spacing: 10,
                                                                              mainAxisSize: MainAxisSize.min,
                                                                              children: [
                                                                                Text(
                                                                                  verifStatus
                                                                                      ? "Input and confirm your new password "
                                                                                      : "Wrong confirmation code !",
                                                                                  style: GoogleFonts.nunito(
                                                                                    fontWeight: FontWeight.bold,
                                                                                    fontSize: 20,
                                                                                    color: Color.fromARGB(
                                                                                      111,
                                                                                      55,
                                                                                      62,
                                                                                      59,
                                                                                    ),
                                                                                  ),
                                                                                ),

                                                                                !verifStatus
                                                                                    ? Container()
                                                                                    : Column(
                                                                                        spacing: 10,
                                                                                        children: [
                                                                                          Input(
                                                                                            "password",
                                                                                            TextInputType.visiblePassword,
                                                                                            _newPwdController,
                                                                                            "",
                                                                                            () => true,
                                                                                          ),

                                                                                          Input(
                                                                                            "confirmed password",
                                                                                            TextInputType.visiblePassword,
                                                                                            _confirmedNewPwdController,
                                                                                            "",
                                                                                            () =>
                                                                                                _confirmedNewPwdController.text ==
                                                                                                _newPwdController.text,
                                                                                          ),

                                                                                          Submitbutton(
                                                                                            "Submit",
                                                                                            Color.fromARGB(
                                                                                              255,
                                                                                              170,
                                                                                              188,
                                                                                              180,
                                                                                            ),
                                                                                            Color.fromARGB(
                                                                                              255,
                                                                                              119,
                                                                                              133,
                                                                                              127,
                                                                                            ),
                                                                                            () async {
                                                                                              Future<
                                                                                                void
                                                                                              >
                                                                                              finalizePasswordReset() async {
                                                                                                await Utils.api_postRequest(
                                                                                                  EmptyRequest(),
                                                                                                  "user/forgotPassword/changePassword/${_newPwdController.text}",
                                                                                                  Utils.API_URL,
                                                                                                );

                                                                                                Navigator.pop(
                                                                                                  context,
                                                                                                );

                                                                                                WidgetsBinding.instance.addPostFrameCallback(
                                                                                                  (
                                                                                                    _,
                                                                                                  ) {
                                                                                                    showDialog(
                                                                                                      context: context,
                                                                                                      builder:
                                                                                                          (
                                                                                                            BuildContext context,
                                                                                                          ) => AlertDialog(
                                                                                                            contentPadding: EdgeInsets.all(
                                                                                                              25,
                                                                                                            ),
                                                                                                            backgroundColor: Color.fromARGB(
                                                                                                              255,
                                                                                                              253,
                                                                                                              251,
                                                                                                              247,
                                                                                                            ),
                                                                                                            content: Text(
                                                                                                              "forgot password ?",
                                                                                                              style: GoogleFonts.nunito(
                                                                                                                fontWeight: FontWeight.bold,
                                                                                                                fontSize: 15,
                                                                                                                color: Color.fromARGB(
                                                                                                                  255,
                                                                                                                  130,
                                                                                                                  145,
                                                                                                                  138,
                                                                                                                ),
                                                                                                                decoration: TextDecoration.underline,
                                                                                                                decorationColor: Color.fromARGB(
                                                                                                                  255,
                                                                                                                  130,
                                                                                                                  145,
                                                                                                                  138,
                                                                                                                ),
                                                                                                              ),
                                                                                                            ),
                                                                                                          ),
                                                                                                    );
                                                                                                  },
                                                                                                );
                                                                                              }

                                                                                              if (_newPwdController.text !=
                                                                                                      "" &&
                                                                                                  _confirmedNewPwdController.text ==
                                                                                                      _newPwdController.text) {
                                                                                                Navigator.pop(
                                                                                                  context,
                                                                                                );
                                                                                                WidgetsBinding.instance.addPostFrameCallback(
                                                                                                  (
                                                                                                    _,
                                                                                                  ) async {
                                                                                                    Utils.showLoadingDialog(
                                                                                                      context,
                                                                                                    );
                                                                                                    await finalizePasswordReset();
                                                                                                  },
                                                                                                );
                                                                                              }
                                                                                            },
                                                                                          ),
                                                                                        ],
                                                                                      ),
                                                                              ],
                                                                            ),
                                                                          ),
                                                                    );
                                                                  });
                                                                }

                                                                Utils.showLoadingDialog(
                                                                  context,
                                                                );
                                                                WidgetsBinding
                                                                    .instance
                                                                    .addPostFrameCallback((
                                                                      _,
                                                                    ) async {
                                                                      await validateVerificationCode();
                                                                    });
                                                              },
                                                            ),
                                                          ],
                                                        )
                                                      : Container(),
                                                ],
                                              ),
                                            ),
                                          );
                                        });
                                      }

                                      Utils.showLoadingDialog(context);
                                      WidgetsBinding.instance
                                          .addPostFrameCallback((_) {
                                            sendConfirmationEmail();
                                          });
                                    }
                                  },
                                ),
                              ],
                            ),
                          );
                        },
                      );
                    },
                    child: Container(
                      alignment: AlignmentGeometry.bottomCenter,
                      child: Text(
                        "forgot password ?",
                        style: GoogleFonts.nunito(
                          fontWeight: FontWeight.bold,
                          fontSize: 15,
                          color: Color.fromARGB(255, 130, 145, 138),
                          decoration: TextDecoration.underline,
                          decorationColor: Color.fromARGB(255, 130, 145, 138),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _registerPage(BuildContext context) {
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
                                      Utils.API_URL,
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
                          Utils.API_URL,
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
