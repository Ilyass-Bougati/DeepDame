import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Preferences extends StatefulWidget {
  const Preferences({super.key});

  @override
  State<StatefulWidget> createState() => _preferencesCreateState();
}

class _preferencesCreateState extends State<Preferences> {
  bool _soundActive = false;
  bool _vibrationActive = false;

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
                    child: SizedBox(height: 80, width: 80),
                  ),
                  SizedBox(width: 30),
                  Text(
                    Utils.userDetails!.username!,
                    style: GoogleFonts.lora(
                      decoration: TextDecoration.underline,
                      decorationColor: Color.fromARGB(255, 170, 188, 180),
                      color: Color.fromARGB(255, 170, 188, 180),
                      fontSize: 40,
                      fontWeight: FontWeight.bold,
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
                          value: _soundActive,
                          onChanged: (bool newValue) {
                            setState(() {
                              _soundActive = newValue;
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
                          value: _vibrationActive,
                          onChanged: (bool newValue) {
                            setState(() {
                              _vibrationActive = newValue;
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
                        print("button1 test");
                      },
                    ),
                    Submitbutton(
                      "Log out",
                      Color.fromARGB(255, 216, 157, 143),
                      Color.fromARGB(255, 142, 102, 93),
                      () {
                        print("button2 test");
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
      bottomNavigationBar: Utils.navbar,
    );
  }
}
