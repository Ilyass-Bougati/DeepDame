import 'dart:convert';
import 'dart:math';
import 'package:chat_bubbles/bubbles/bubble_normal.dart';
import 'package:deepdame/dtos/MessageDto.dart';
import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/prefabs/SendButton.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class General extends StatefulWidget {
  const General({super.key});

  static List<Map<String, dynamic>> _globalMessageData = [];
  static VoidCallback? _onUiRefreshNeeded;
  static final Map<String, Color> _userColorMap = {};
  static final Random _random = Random();

  @override
  State<StatefulWidget> createState() => _GeneralCreateState();

  static void emptyChatData() {
    _globalMessageData.clear();
    _userColorMap.clear();
    if (_onUiRefreshNeeded != null) _onUiRefreshNeeded!();
  }

  static Function(dynamic) initOnMessageFunction() {
    return (json) {
      var claimMap = jsonDecode(json);
      _globalMessageData.add(claimMap);
      if (_onUiRefreshNeeded != null) _onUiRefreshNeeded!();
    };
  }

  static Color getUserColor(String username) {
    if (!_userColorMap.containsKey(username)) {
      _userColorMap[username] = Color.fromARGB(
        255,
        _random.nextInt(255),
        _random.nextInt(255),
        _random.nextInt(255),
      );
    }
    return _userColorMap[username]!;
  }
}

class _GeneralCreateState extends State<General> {
  int pageIndex = 0;
  bool _isLoadingOld = false; // To show a spinner while loading
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _messageInputController = TextEditingController();

  @override
  void initState() {
    super.initState();

    // Connect the Bridge
    General._onUiRefreshNeeded = () {
      if (mounted) {
        setState(() {});
        if (_scrollController.hasClients &&
            _scrollController.position.pixels >=
                _scrollController.position.maxScrollExtent - 200) {
          _scrollToBottom();
        }
      }
    };

    Utils.onGeneralChatMessage = General.initOnMessageFunction();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _scrollToBottom();
    });
  }

  @override
  void dispose() {
    General._onUiRefreshNeeded = null;
    _scrollController.dispose();
    _messageInputController.dispose();
    super.dispose();
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      Future.delayed(const Duration(milliseconds: 50), () {
        if (_scrollController.hasClients) {
          _scrollController.animateTo(
            _scrollController.position.maxScrollExtent,
            duration: const Duration(milliseconds: 300),
            curve: Curves.easeIn,
          );
        }
      });
    }
  }

  void loadOldMessages() async {
    if (_isLoadingOld) return;

    setState(() {
      _isLoadingOld = true;
    });

    try {
      var response =
          await Utils.api_getRequest(
            "general-chat/${pageIndex++}",
            Utils.API_URL,
          ).onError((e, trace) {
            pageIndex--;
            print("Error loading messages: $e");
            return [];
          });

      if (response is List && response.isNotEmpty) {
        setState(() {
          final List<Map<String, dynamic>> newBatch =
              List<Map<String, dynamic>>.from(response);
          General._globalMessageData.insertAll(0, newBatch);
        });
      }
    } catch (e) {
      print("Exception in loadOldMessages: $e");
    } finally {
      if (mounted) {
        setState(() {
          _isLoadingOld = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // 1. Ensure this is true (it is by default, but good to be explicit)
      resizeToAvoidBottomInset: true,

      backgroundColor: const Color.fromARGB(255, 253, 251, 247),
      appBar: PreferredSize(
        preferredSize: const Size.fromHeight(110),
        child: Container(
          padding: const EdgeInsets.only(top: 60),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                "General chat",
                style: GoogleFonts.lora(
                  color: const Color.fromARGB(255, 170, 188, 180),
                  fontSize: 50,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ),

      body: Column(
        children: [          // above the input field.
          Expanded(
            child: Container(
              margin: const EdgeInsets.symmetric(horizontal: 30, vertical: 20),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(41),
                color: const Color.fromARGB(255, 235, 229, 222),
              ),
              child: Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 9,
                  vertical: 20,
                ),
                child: SizedBox(
                  height: double.infinity,
                  width: double.infinity,
                  child: ListView.builder(
                    controller: _scrollController,
                    itemCount: General._globalMessageData.length + 1,
                    itemBuilder: (context, index) {
                      if (index == 0) {
                        return Padding(
                          padding: const EdgeInsets.only(bottom: 20),
                          child: Center(
                            child: _isLoadingOld
                                ? const SizedBox(
                                    height: 20,
                                    width: 20,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2,
                                    ),
                                  )
                                : TextButton(
                                    onPressed: loadOldMessages,
                                    child: Text(
                                      "Load older messages",
                                      style: GoogleFonts.nunito(
                                        color: Colors.grey[600],
                                      ),
                                    ),
                                  ),
                          ),
                        );
                      }

                      final msgIndex = index - 1;
                      final msgData = General._globalMessageData[msgIndex];

                      final String currentUsername =
                          msgData['user']['username'];
                      final String messageText = msgData['message'];
                      final bool isMe =
                          Utils.userDetails!.username == currentUsername;

                      bool showTail = true;
                      if (msgIndex < General._globalMessageData.length - 1) {
                        final nextUser =
                            General._globalMessageData[msgIndex +
                                1]['user']['username'];
                        if (nextUser == currentUsername) showTail = false;
                      }

                      bool showName = true;
                      if (msgIndex > 0) {
                        final prevUser =
                            General._globalMessageData[msgIndex -
                                1]['user']['username'];
                        if (prevUser == currentUsername) showName = false;
                      }

                      return Column(
                        children: [
                          SizedBox(
                            width: double.infinity,
                            child: Align(
                              alignment: isMe
                                  ? Alignment.centerRight
                                  : Alignment.centerLeft,
                              child: (showName && !isMe)
                                  ? Padding(
                                      padding: const EdgeInsets.only(
                                        left: 10,
                                        top: 10,
                                      ),
                                      child: Text(
                                        "$currentUsername :",
                                        style: GoogleFonts.nunito(
                                          color: General.getUserColor(
                                            currentUsername,
                                          ),
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    )
                                  : const SizedBox(),
                            ),
                          ),
                          BubbleNormal(
                            text: " $messageText",
                            isSender: isMe,
                            color: General.getUserColor(currentUsername),
                            tail: showTail,
                            textStyle: GoogleFonts.nunito(
                              color: Colors.white,
                              fontSize: 16,
                            ),
                          ),
                        ],
                      );
                    },
                  ),
                ),
              ),
            ),
          ),

          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
            color: const Color.fromARGB(255, 253, 251, 247),
            child: SafeArea(
              child: SizedBox(
                width: double.infinity,
                child: Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      width: MediaQuery.of(context).size.width - 80,
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: const Color.fromARGB(255, 170, 188, 180),
                          width: 2.0,
                        ),
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(100),
                      ),
                      child: Input.noBorder(
                        "message",
                        TextInputType.multiline,
                        _messageInputController,
                        "",
                        () => true,
                      ),
                    ),
                    const SizedBox(width: 10),
                    SendButton(
                      const Icon(Icons.send, color: Colors.white),
                      () {
                        if (_messageInputController.text.trim().isEmpty) return;

                        String json = jsonEncode(
                          MessageDTO(_messageInputController.text).toJson(),
                        );
                        Utils.client.send(
                          headers: {'content-type': 'application/json'},
                          destination: "/app/message",
                          body: json,
                        );
                        _messageInputController.text = "";
                      },
                      const Color.fromARGB(255, 170, 188, 180),
                      const Color.fromARGB(255, 108, 121, 115),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: Utils.getNavbar(context, 1),
    );
  }
}
