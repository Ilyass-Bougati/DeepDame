import 'package:deepdame/main.dart';
import 'package:deepdame/pages/Connect.dart';
import 'package:deepdame/pages/General.dart';
import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/prefabs/ValidationController.dart';
import 'package:deepdame/requests/ChangePasswordRequest.dart';
import 'package:deepdame/requests/EmptyRequest.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Preferences extends StatefulWidget {
  const Preferences({super.key});
  static bool soundActive = false;
  static bool vibrationActive = true;
  @override
  State<StatefulWidget> createState() => _preferencesCreateState();
}

class _preferencesCreateState extends State<Preferences> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(110),
        child: Container(
          padding: EdgeInsets.only(top: 60),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                "Settings",
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
      body: SingleChildScrollView(
        child: Container(
          padding: EdgeInsets.symmetric(horizontal: 30),
          child: Column(
            spacing: 10,
            children: [
              SizedBox(height: 20),
              // Name & icon
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    decoration: BoxDecoration(
                      color: Color.fromARGB(255, 123, 152, 166),
                      borderRadius: BorderRadius.circular(80),
                    ),
                    child: SizedBox(
                      height: 80,
                      width: 80,
                      child: Icon(
                        Icons.person_rounded,
                        color: Color.fromARGB(255, 64, 79, 87),
                        size: 40,
                      ),
                    ),
                  ),
                  SizedBox(width: 30),
                  Expanded(
                    child: Text(
                      softWrap: false,
                      maxLines: 1,
                      overflow: TextOverflow.clip,
                      Utils.userDetails!.username!,
                      style: GoogleFonts.lora(
                        decoration: TextDecoration.underline,
                        decorationColor: Color.fromARGB(255, 170, 188, 180),
                        color: Color.fromARGB(255, 170, 188, 180),
                        fontSize: 40,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),

              SizedBox(height: 60),
              Text(
                "Game settings",
                style: GoogleFonts.lora(
                  color: Color.fromARGB(255, 123, 152, 166),
                  fontSize: 40,
                  fontWeight: FontWeight.bold,
                ),
              ),

              //Game Settings container
              Container(
                padding: EdgeInsets.symmetric(horizontal: 28.5, vertical: 22.5),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(41),
                  color: Color.fromARGB(255, 235, 229, 222),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          "Sound",
                          style: GoogleFonts.nunito(
                            color: Color.fromARGB(255, 123, 152, 166),
                            fontSize: 25,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Switch(
                          value: Preferences.soundActive,
                          onChanged: (bool newValue) {
                            setState(() {
                              Preferences.soundActive = newValue;
                            });
                          },
                        ),
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Text(
                          "Vibration",
                          style: GoogleFonts.nunito(
                            color: Color.fromARGB(255, 123, 152, 166),
                            fontSize: 25,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Switch(
                          value: Preferences.vibrationActive,
                          onChanged: (bool newValue) {
                            setState(() {
                              Preferences.vibrationActive = newValue;
                            });
                          },
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              SizedBox(height: 60),
              Text(
                "Account",
                style: GoogleFonts.lora(
                  color: Color.fromARGB(255, 123, 152, 166),
                  fontSize: 40,
                  fontWeight: FontWeight.bold,
                ),
              ),

              //Account Settings container
              Container(
                width: double.infinity,
                padding: EdgeInsets.symmetric(horizontal: 28.5, vertical: 22.5),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(41),
                  color: Color.fromARGB(255, 235, 229, 222),
                ),
                child: Column(
                  spacing: 20,
                  children: [
                    Submitbutton(
                      "Change password",
                      Color.fromARGB(255, 216, 157, 143),
                      Color.fromARGB(255, 142, 102, 93),
                      () {
                        ValidationController old_password_controller =
                            ValidationController();
                        ValidationController new_password_controller =
                            ValidationController();

                        void sendPasswordChangeRequest() async {
                          String status = "Success";
                          try {
                            await Utils.api_postRequest(
                              ChangePasswordRequest(
                                new_password_controller.getController().text,
                                old_password_controller.getController().text,
                              ),
                              "user/changepw",
                              Utils.API_URL,
                            ).onError((e, trace) {
                              status = "Failure";
                              throw Exception();
                            });
                          } catch (e) {
                            print("failed to change pwd");
                          }
                          Navigator.pop(context);
                          WidgetsBinding.instance.addPostFrameCallback((_) {
                            if (status == "Success") {
                              Navigator.pop(context);
                            }
                            showDialog(
                              context: context,
                              builder: (context) => AlertDialog(
                                content: Text(
                                  status == "Success"
                                      ? "Password changed successfully !"
                                      : "Failure to change password !",

                                  style: GoogleFonts.lora(
                                    fontSize: 20,
                                    fontWeight: FontWeight.bold,
                                    color: status == "Success"
                                        ? Color.fromARGB(255, 170, 188, 180)
                                        : Color.fromARGB(255, 216, 157, 143),
                                  ),
                                ),
                              ),
                            );
                          });
                        }

                        showDialog(
                          context: context,
                          barrierDismissible: true,
                          builder: (BuildContext context) {
                            return AlertDialog(
                              contentPadding: EdgeInsets.all(25),
                              backgroundColor: Color.fromARGB(
                                255,
                                253,
                                251,
                                247,
                              ),
                              content: Column(
                                mainAxisSize: MainAxisSize.min,
                                spacing: 20,
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  SizedBox(
                                    child: Input(
                                      "Old password",
                                      TextInputType.visiblePassword,
                                      old_password_controller.getController(),
                                      "",
                                      () {
                                        return true;
                                      },
                                    ),
                                  ),
                                  SizedBox(
                                    child: Input(
                                      "password",
                                      TextInputType.visiblePassword,
                                      new_password_controller.getController(),
                                      "8+ characters: 1+ Upper, 1+ Digit, 1+ Symbol",
                                      () {
                                        new_password_controller.setState(
                                          Connect.validator(
                                            new_password_controller
                                                .getController(),
                                            "password",
                                          ),
                                        );
                                        return new_password_controller
                                            .getState();
                                      },
                                    ),
                                  ),

                                  Submitbutton(
                                    "Change password",
                                    Color.fromARGB(255, 216, 157, 143),
                                    Color.fromARGB(255, 142, 102, 93),
                                    () {
                                      if (new_password_controller.getState() &&
                                          old_password_controller
                                                  .getController()
                                                  .text !=
                                              "") {
                                        Utils.showLoadingDialog(context);
                                        WidgetsBinding.instance
                                            .addPostFrameCallback((_) {
                                              sendPasswordChangeRequest();
                                            });
                                      }
                                    },
                                  ),
                                ],
                              ),
                            );
                          },
                        );

                        //Build request & Send it

                        //Show loading dialog

                        //On request done show status : OK : notOK
                      },
                    ),
                    Submitbutton(
                      "Log out",
                      Color.fromARGB(255, 216, 157, 143),
                      Color.fromARGB(255, 142, 102, 93),
                      () async {
                        Utils.api_postRequest(
                          EmptyRequest(),
                          "/auth/logout",
                          Utils.API_URL,
                        );
                        connected = false;
                        Utils.userDetails = null;
                        await Utils.clearCookies(() {
                          Utils.client.deactivate();
                          General.emptyChatData();
                          Navigator.of(
                            context,
                          ).popUntil((route) => route.isFirst);
                          Navigator.pushReplacement(
                            context,
                            MaterialPageRoute(builder: (context) => Landing()),
                          );
                        });
                      },
                    ),
                  ],
                ),
              ),

              SizedBox(height: 20),
            ],
          ),
        ),
      ),
      bottomNavigationBar: Utils.getNavbar(context, 2),
    );
  }
}
