import 'dart:convert';
import 'dart:io';
import 'package:deepdame/dtos/UserDto.dart';
import 'package:deepdame/pages/Connect.dart';
import 'package:deepdame/pages/Game.dart';
import 'package:deepdame/pages/General.dart';
import 'package:deepdame/prefabs/Input.dart';
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
    TextEditingController _controller = TextEditingController();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      showDialog(
        context: context,
        barrierDismissible: false,
        builder: (context) => AlertDialog(
          content: Column(
            mainAxisSize: MainAxisSize.min,
            spacing: 30,
            children: [
              Input("Api", TextInputType.text, _controller, "", () => true),
              Submitbutton(
                "Submit",
                Color.fromARGB(255, 170, 188, 180),
                Color.fromARGB(255, 119, 133, 127),
                () {
                  if (_controller.text != "") {
                    Utils.API = _controller.text;
                    Utils.API_URL = "https://${Utils.API}/api/v1";
                  }
                },
              ),

              Submitbutton(
                "Load landing",
                Color.fromARGB(255, 170, 188, 180),
                Color.fromARGB(255, 119, 133, 127),
                () async {
                  Utils.currentGame = ValueNotifier(null);

                  bool isConnected = false;
                  try {
                    var resp =
                        await Utils.api_getRequest(
                          "user/",
                          Utils.API_URL,
                        ).onError((e, stackTrace) {
                          isConnected = false;
                          throw Exception();
                        });
                    isConnected = true;
                    UserDTO dto = UserDTO.fromJson(resp);
                    Utils.userDetails = dto.toUser();

                    List<Cookie> cookies = await persistCookieJar!
                        .loadForRequest(Uri.parse('https://${Utils.API}'));

                    Cookie? accessToken = cookies.firstWhere(
                      (c) => c.name == 'access_token',
                      orElse: () => Cookie("access_token", ""),
                    );

                    if (accessToken.value.isEmpty) {
                      Utils.userDetails = null;
                      isConnected = false;
                      throw Exception();
                    }

                    ws_appBanned =
                        '/topic/application-ban/${Utils.userDetails!.id}';
                    ws_chatBanned = '/topic/chat-ban/${Utils.userDetails!.id}';

                    Utils.client = StompClient(
                      config: StompConfig.sockJS(
                        url: 'https://${Utils.API}/ws',
                        webSocketConnectHeaders: {
                          'Host': Utils.API,
                          'Cookie': '${accessToken.name}=${accessToken.value}',
                        },

                        onConnect: (StompFrame frame) {
                          //Subscribing to general chat
                          Utils.client.subscribe(
                            destination: '/topic/general-chat',
                            callback: (StompFrame frame) {
                              if (frame.body != null) {
                                print('Received: ${frame.body!}');
                                Utils.onGeneralChatMessage?.call(frame.body);
                              }
                            },
                          );

                          //Subscribing to the "game created"
                          Utils.client.subscribe(
                            destination: ws_gameCreated,
                            callback: (StompFrame frame) {
                              if (frame.body != null) {
                                print('GAME CREATED: ${frame.body!}');
                                Game.currentGameId = jsonDecode(
                                  frame.body!,
                                )['gameId'];
                                Utils.currentGame!.value = Game(true);
                              }
                            },
                          );

                          //Subscribing to the "game joined"
                          Utils.client.subscribe(
                            destination: ws_gameJoined,
                            callback: (StompFrame frame) {
                              if (frame.body != null) {
                                print('GAME JOINED: ${frame.body!}');

                                Game.opponent = jsonDecode(
                                  frame.body!,
                                )['opponentName'];
                                print(jsonDecode(frame.body!)['opponentName']);
                                Game.currentGameId = jsonDecode(
                                  frame.body!,
                                )['gameId'];
                                Utils.currentGame!.value = Game(false);
                              }
                            },
                          );

                          // Subscribing to chat ban
                          Utils.client.subscribe(
                            destination: ws_chatBanned,
                            callback: (StompFrame frame) {
                              if (frame.body != null) {
                                Utils.client.deactivate();
                                Utils.client.activate();
                              }
                            },
                          );

                          // Subscribing to app ban
                          Utils.client.subscribe(
                            destination: ws_appBanned,
                            callback: (StompFrame frame) {
                              if (frame.body != null) {
                                Utils.client.deactivate();
                                Utils.client.activate();
                              }
                            },
                          );

                          // Subscribing to error queue
                          Utils.client.subscribe(
                            destination: ws_serverSideError,
                            callback: (StompFrame frame) {
                              if (frame.body != null) {
                                print('Server side error : ${frame.body!}');
                              }
                            },
                          );
                        },

                        onDebugMessage: (String msg) {
                          print('STOMP DEBUG: $msg');
                        },
                        onWebSocketError: (dynamic error) async {
                          await reloadWsConnection();
                          print('WebSocket Error: $error');
                        },
                        onStompError: (StompFrame frame) {
                          print('Stomp Error: ${frame.body}');
                        },

                        // onWebSocketError: (dynamic error) async => await reloadWsConnection(),
                        onDisconnect: (frame) => print('Disconnected'),
                      ),
                    );
                    Utils.client.activate();
                    Utils.onGeneralChatMessage =
                        General.initOnMessageFunction();
                  } catch (e) {}
                  WidgetsBinding.instance.addPostFrameCallback((_) {
                    // FIXME: This is temporary for debugging purposes !
                    // Navigator.pop(context);
                    // Navigator.pushReplacement(
                    //   context,
                    //   PageRouteBuilder(
                    //     pageBuilder: (context, a1, a2) => isConnected
                    //         ? build_onConnection(context)
                    //         : build_offConnection(context),
                    //     transitionDuration: Duration.zero,
                    //     reverseTransitionDuration: Duration.zero,
                    //   ),
                    // );
                  });

                  Navigator.pop(context);
                  WidgetsBinding.instance.addPostFrameCallback((_) {
                    Navigator.pushReplacement(
                      context,
                      PageRouteBuilder(
                        pageBuilder: (context, a1, a2) =>
                            Utils.userDetails != null
                            ? build_onConnection(context)
                            : build_offConnection(context),
                        transitionDuration: Duration.zero,
                        reverseTransitionDuration: Duration.zero,
                      ),
                    );
                  });
                },
              ),
            ],
          ),
        ),
      );
    });
  }

  Future<void> reloadWsConnection() async {
    await Utils.refreshToken();
    Utils.client.deactivate();
    Utils.client.activate();
  }

  Widget build_temporarly(BuildContext context) {
    initLandingPage(context);
    WidgetsBinding.instance.addPostFrameCallback((_) {
      // FIXME: This is temporary for debugging purposes !
      // Utils.showLoadingDialog(context);
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
                    SizedBox(height: 19),
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
        child: Container(
          padding: EdgeInsets.symmetric(horizontal: 20),
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
                      Center(
                        child: Row(
                          children: [
                            Expanded(
                              child: Center(
                                child: Text(
                                  overflow: TextOverflow.ellipsis,
                                  maxLines: 1,
                                  "Hi, ${Utils.userDetails!.username} !",
                                  style: GoogleFonts.lora(
                                    fontSize: 25,
                                    fontWeight: FontWeight.bold,
                                    color: Color.fromARGB(255, 170, 188, 180),
                                  ),
                                ),
                              ),
                            ),
                          ],
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
                () {
                  createGame('PVP', context);
                  print("Load Pvp");
                },
              ),
              SizedBox(height: 10),
              Submitbutton(
                "Play vs Ai",
                Color.fromARGB(255, 216, 157, 143),
                Color.fromARGB(255, 142, 102, 93),
                () {
                  Game.opponent = 'Ai';
                  createGame('PVE', context);
                },
              ),
            ],
          ),
        ),
      ),
      bottomNavigationBar: Utils.getNavbar(context, 0),
    );
  }

  void createGame(String mode, BuildContext context) {
    switch (mode) {
      case 'PVE':
        Utils.client.send(
          headers: {'content-type': 'application/json'},
          destination: "/app/game/create",
          body: jsonEncode(mode),
        );
        break;

      case 'PVP':
        Utils.client.send(
          headers: {'content-type': 'application/json'},
          destination: "/app/game/matchmaking",
        );
        break;
    }
    Utils.currentGame!.addListener(() {
      Navigator.pushReplacement(
        context,
        PageRouteBuilder(
          pageBuilder: (context, a1, a2) => Utils.currentGame!.value!,
          transitionDuration: Duration.zero,
          reverseTransitionDuration: Duration.zero,
        ),
      );
    });
  }
}
