import 'dart:convert';
import 'dart:io';
import 'package:deepdame/dtos/UserDto.dart';
import 'package:deepdame/pages/Connect.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:stomp_dart_client/stomp_dart_client.dart';

class Landing extends StatelessWidget {
  const Landing({super.key});

  @override
  Widget build(BuildContext context) => Utils.userDetails == null
      ? build_temporarly(context)
      : build_onConnection(context);

  Future<void> initLandingPage(BuildContext context) async {
    bool connected = false;
    try {
      var resp = await Utils.api_getRequest("user/", Utils.API_URL).onError((
        e,
        stackTrace,
      ) {
        connected = false;
        throw Exception();
      });
      connected = true;
      UserDTO dto = UserDTO.fromJson(resp);
      Utils.userDetails = dto.toUser();

      List<Cookie> cookies = await persistCookieJar!.loadForRequest(
        Uri.parse('https://ilyass-server.taila311b0.ts.net'),
      );

      Cookie? authCookie = cookies.firstWhere(
        (c) => c.name == 'access_token',
        orElse: () => Cookie('dummy', ''),
      );

      if (authCookie.name == 'dummy') {
        print("No cookies !");
      }

      Utils.client = StompClient(
        config: StompConfig.sockJS(
          url: 'https://ilyass-server.taila311b0.ts.net/ws',
          webSocketConnectHeaders: {
            'Host': 'ilyass-server.taila311b0.ts.net',
            'Cookie': '${authCookie.name}=${authCookie.value}',
          },

          onConnect: (StompFrame frame) {
            Utils.client.subscribe(
              destination: '/topic/general-chat',
              callback: (StompFrame frame) {
                if (frame.body != null) {
                  print('Received: ${frame.body!}');
                  Utils.onGeneralChatLoaded?.call(frame.body);
                }
              },
            );
            //Subscribing to general-chat
          },

          onStompError: (StompFrame frame) {
            print('CRITICAL STOMP ERROR: ${frame.body}');
          },
          onWebSocketError: (dynamic error) {
            print('WEBSOCKET ERROR: $error');
          },

          onUnhandledFrame: (StompFrame frame) {
            print('UNHANDLED FRAME: ${frame.command}');
          },

          onDisconnect: (frame) => print('Disconnected'),
        ),
      );

      Utils.client.activate();
    } catch (e) {}
    Navigator.pop(context);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => connected
              ? build_onConnection(context)
              : build_offConnection(context),
        ),
      );
    });
  }

  Widget build_temporarly(BuildContext context) {
    initLandingPage(context);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Utils.showLoadingDialog(context);
    });

    return Scaffold(backgroundColor: Color.fromARGB(255, 253, 251, 247));
  }

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
                      "Hi, ${Utils.userDetails!.username} !",
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
      bottomNavigationBar: Utils.getNavbar(context, 0),
    );
  }
}
