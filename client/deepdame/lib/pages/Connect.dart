import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Connect extends StatelessWidget {
  final bool login;
  const Connect(this.login, {super.key});

  @override
  Widget build(BuildContext context) {
    return login ? _loginPage() : _registerPage() ;
  }

  Widget _loginPage() {
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
                              TextEditingController(),
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
                              TextEditingController(),
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
                  () => print("This is a test behaviour !"),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
  Widget _registerPage() {
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
                              TextEditingController(),
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
                              TextEditingController(),
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
                              TextEditingController(),
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
                              TextEditingController(),
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
                  () => print("This is a test behaviour !"),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
