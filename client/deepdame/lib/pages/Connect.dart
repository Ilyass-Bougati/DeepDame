// import 'dart:ffi';

import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Connect extends StatelessWidget {
  // final Bool type;

  // const Connect(this.type, {super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: true,
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(100),
        child: Container(
          padding: EdgeInsets.only(top: 10),
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
      body: SingleChildScrollView(
        hitTestBehavior: HitTestBehavior.translucent,
        child: Column(
          children: [
            SingleChildScrollView(
              hitTestBehavior : HitTestBehavior.translucent,
              child: Container(
                padding: EdgeInsets.only(right: 30, left: 30),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    //Username
                    Align(
                      alignment: AlignmentGeometry.centerLeft,
                      child: Text(
                        "Username :",
                        style: GoogleFonts.nunito(
                          color: Color.fromARGB(255, 170, 188, 180),
                          fontSize: 30,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    Align(
                      alignment: Alignment.centerRight,
                      child: SizedBox(
                        width: 300,
                        child: Input(
                          "JohnDoe67",
                          TextInputType.name,
                          TextEditingController(),
                        ),
                      ),
                    ),

                    //Email
                    SizedBox(height: 25),
                    Align(
                      alignment: AlignmentGeometry.centerLeft,
                      child: Text(
                        "Email :",
                        style: GoogleFonts.nunito(
                          color: Color.fromARGB(255, 170, 188, 180),
                          fontSize: 30,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    Align(
                      alignment: Alignment.centerRight,
                      child: SizedBox(
                        width: 300,
                        child: Input(
                          "example@email.com",
                          TextInputType.emailAddress,
                          TextEditingController(),
                        ),
                      ),
                    ),

                    //Password & Confirm password
                    SizedBox(height: 25),
                    Align(
                      alignment: AlignmentGeometry.centerLeft,
                      child: Text(
                        "Password :",
                        style: GoogleFonts.nunito(
                          color: Color.fromARGB(255, 170, 188, 180),
                          fontSize: 30,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                    Align(
                      alignment: Alignment.centerRight,
                      child: SizedBox(
                        width: 300,
                        child: Input(
                          "password",
                          TextInputType.visiblePassword,
                          TextEditingController(),
                        ),
                      ),
                    ),
                    SizedBox(height: 25),
                    Align(
                      alignment: Alignment.centerRight,
                      child: SizedBox(
                        width: 300,
                        child: Input(
                          "confirmed password",
                          TextInputType.visiblePassword,
                          TextEditingController(),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
            SizedBox(height: 20,),
            Submitbutton(
              "Done",
              Color.fromARGB(255, 170, 188, 180),
              Color.fromARGB(255, 119, 133, 127),
              ()=> print("This is a test behaviour !")
            ),
          ],
        ),
      ),
    );
  }
}
