import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/prefabs/ValidationController.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Connect extends StatelessWidget {
  final bool login;
  const Connect(this.login, {super.key});

  @override
  Widget build(BuildContext context) {
    return login ? _loginPage() : _registerPage();
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

  Widget _loginPage() {
    ValidationController username_controller = ValidationController();
    ValidationController password_controller = ValidationController();
    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      resizeToAvoidBottomInset: true,
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(80),
        child: Container(
          padding: EdgeInsets.only(top: 30),
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
                              "JohnDoe69",
                              TextInputType.name,
                              username_controller.getController(),
                              () => username_controller.setState(
                                validator(
                                  username_controller.getController(),
                                  "username",
                                ),
                              ),
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
                              () => password_controller.setState(
                                validator(
                                  password_controller.getController(),
                                  "password",
                                ),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                SizedBox(height: 80),
                Submitbutton(
                  "Login",
                  Color.fromARGB(255, 170, 188, 180),
                  Color.fromARGB(255, 119, 133, 127),
                  () {
                    if (username_controller.getState() == false ||
                        password_controller.getState() == false) {
                      if (username_controller.getState() == false)
                        print("Username is invalid !");

                      if (password_controller.getState() == false)
                        print("Password is invalid !");
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

  Widget _registerPage() {
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
        preferredSize: const Size.fromHeight(80),
        child: Container(
          padding: EdgeInsets.only(top: 30),
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
                              () {
                                map['username']?.setState(
                                  validator(
                                    map['username']?.getController()
                                        as TextEditingController,
                                    "username",
                                  ),
                                );
                                ;
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
                              () {
                                map['email']?.setState(
                                  validator(
                                    map['email']?.getController()
                                        as TextEditingController,
                                    "email",
                                  ),
                                );
                                ;
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
                              () {
                                map['password']?.setState(
                                  validator(
                                    (map['password']?.getController()
                                        as TextEditingController),
                                    "password",
                                  ),
                                );
                                ;
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
                              },
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                SizedBox(height: 80),
                Submitbutton(
                  "Register",
                  Color.fromARGB(255, 170, 188, 180),
                  Color.fromARGB(255, 119, 133, 127),
                  () {
                    for (String s in map.keys.toSet()) {
                      if (map[s]?.getState() == false) {
                        print("Field $s is invalid !");
                      }
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
