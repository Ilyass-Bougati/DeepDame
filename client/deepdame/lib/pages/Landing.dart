import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:google_fonts/google_fonts.dart';

class Landing extends StatelessWidget {
  const Landing({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          children: [
            Stack(
              children: [
                Column(
                  children: [
                    SizedBox(
                      height: 233.404,
                    ),
                    Stack(
                      alignment: AlignmentDirectional.center,
                      children: [
                        Divider(
                          height: 20,
                          thickness: 2,
                          indent: 20,
                          endIndent: 20,
                          color: Color.fromARGB(255, 119, 133, 127),
                        ),
                        Column(
                          children: [
                            Text(
                              "Deep Dame",
                              style: GoogleFonts.lora(
                                fontSize: 50,
                                fontWeight: FontWeight.bold,
                                color: Color.fromARGB(255, 170, 188, 180),
                              ),
                            ),
                            SizedBox(height: 50),
                          ],
                        ),
                      ],
                    ),
                    SizedBox(height: 69.5),
                    Submitbutton(
                      "Log in",
                      Color.fromARGB(255, 170, 188, 180),
                      Color.fromARGB(255, 119, 133, 127),
                      () => print("Action example"),
                    ),
                    SizedBox(height: 19),
                    Submitbutton(
                      "Register",
                      Color.fromARGB(255, 123, 152, 166),
                      Color.fromARGB(255, 79, 99, 109),
                      () => print("Action example"),
                    ),
                  ],
                ),
                Column(
                  children: [
                    SizedBox(
                      height: MediaQuery.of(context).size.height - 160.404,
                    ), //Trial and error again :')
                    SvgPicture.asset(
                      "assets/vectors/CrossHatchFade.svg",
                      width: MediaQuery.of(context).size.width,
                      fit: BoxFit.fitWidth,
                    ),
                  ],
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
