import 'package:deepdame/pages/Connect.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:google_fonts/google_fonts.dart';

class Landing extends StatelessWidget {
  final bool isConnected;
  const Landing(this.isConnected , {super.key});

  @override
  Widget build(BuildContext context) => isConnected ? build_onConnection(context) : build_offConnection(context);

  Widget build_offConnection(BuildContext context) {
    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      body: Center(
        child: Column(
          children: [
            Stack(
              children: [
                Column(
                  children: [
                    SizedBox(height: 233.404),
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
                      () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => Connect(true),
                          ),
                        );
                      },
                    ),
                    SizedBox(height: 19),
                    Submitbutton(
                      "Register",
                      Color.fromARGB(255, 123, 152, 166),
                      Color.fromARGB(255, 79, 99, 109),
                      () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => Connect(false),
                          ),
                        );
                      },
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

  Widget build_onConnection(BuildContext context) {
    return Scaffold(
      extendBody: true,
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
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
                Column(
                  children: [
                    SizedBox(height: 50),
                    Text(
                      "Hi, JohnDoe67 !",
                      style: GoogleFonts.lora(
                        fontSize: 25,
                        fontWeight: FontWeight.bold,
                        color: Color.fromARGB(255, 170, 188, 180),
                      ),
                    ),
                  ],
                ),
              ],
            ),

            SvgPicture.asset('assets/vectors/Board.svg'),
            SizedBox(height: 30),
            Submitbutton(
              "Play Online",
              Color.fromARGB(255, 232, 208, 153),
              Color.fromARGB(255, 155, 138, 101),
              () => print("Load Pvp"),
            ),
            SizedBox(height: 10),
            Submitbutton(
              "Play vs Ai",
              Color.fromARGB(255, 216, 157, 143),
              Color.fromARGB(255, 142, 102, 93),
              () => print("Load Pve"),
            ),
          ],
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        height: 50,
        color: Color.fromARGB(200, 235, 229, 222),
      ),
    );
  }
}
