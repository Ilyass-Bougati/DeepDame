import 'dart:convert';
import 'package:chat_bubbles/bubbles/bubble_normal.dart';
import 'package:deepdame/dtos/MessageDto.dart';
import 'package:deepdame/prefabs/Input.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class General extends StatefulWidget {
  const General({super.key});
  @override
  State<StatefulWidget> createState() => _GeneralCreateState();
}

class _GeneralCreateState extends State<General> {
  static List<Widget> _Messages = [SizedBox(height: 40)];
  static Map<String, Color> _userColorMap = {};
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _messageInputController = TextEditingController();

  Color addNewUserToMap(String username) {
    _userColorMap.addAll({
      username: Color.fromARGB(
        255,
        random.nextInt(255),
        random.nextInt(255),
        random.nextInt(255),
      ),
    });

    return _userColorMap[username]!;
  }

  @override
  void initState() {
    super.initState();
    Utils.onGeneralChatLoaded = (json) {
      setState(() {
        var claimMap = jsonDecode(json);
        _Messages.insert(
          _Messages.length - 1,
          Column(
            children: [
              SizedBox(
                width: double.infinity,
                child: Align(
                  alignment:
                      Utils.userDetails!.username ==
                          claimMap['user']['username']
                      ? AlignmentGeometry.centerRight
                      : Alignment.centerLeft,
                  child: Text(
                    "${claimMap['user']['username']} :",
                    style: GoogleFonts.nunito(
                      color:
                          _userColorMap.containsKey(
                            claimMap['user']['username'],
                          )
                          ? _userColorMap[claimMap['user']['username']]
                          : addNewUserToMap(claimMap['user']['username']),
                    ),
                  ),
                ),
              ),
              BubbleNormal(
                text: " ${claimMap['message']}",
                isSender:
                    Utils.userDetails!.username == claimMap['user']['username'],
                color: _userColorMap[claimMap['user']['username']]!,
                tail: true,
                textStyle: GoogleFonts.nunito(
                  color: Colors.white,
                  fontSize: 16,
                ),
              ),
            ],
          ),
        );
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: Duration(milliseconds: 300),
          curve: Curves.easeIn,
        );
      });
    };
    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (_scrollController.hasClients) {
        _scrollController.jumpTo(_scrollController.position.maxScrollExtent);
      }
    });
  }

  @override
  void dispose() {
    super.dispose();
    Utils.onGeneralChatLoaded = (json) {
      var claimMap = jsonDecode(json);
      _Messages.insert(
        _Messages.length - 1,
        Row(
          mainAxisSize: MainAxisSize.max,
          children: [
            Text(
              "${claimMap['user']['username']} :",
              style: GoogleFonts.nunito(
                color: _userColorMap.containsKey(claimMap['user']['username'])
                    ? _userColorMap[claimMap['user']['username']]
                    : addNewUserToMap(claimMap['user']['username']),
              ),
            ),
            Expanded(
              child: Text(
                softWrap: true,
                " ${claimMap['message']}",
                style: GoogleFonts.nunito(
                  color: _userColorMap[claimMap['user']['username']],
                ),
              ),
            ),
          ],
        ),
      );
    };
  }

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
                "General chat",
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

      body: Stack(
        children: [
          Container(
            margin: EdgeInsets.symmetric(horizontal: 30, vertical: 20),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(41),
              color: Color.fromARGB(255, 235, 229, 222),
            ),
            child: Container(
              padding: EdgeInsets.symmetric(horizontal: 9, vertical: 20),
              child: SizedBox(
                height: double.infinity,
                width: double.infinity,
                child: SingleChildScrollView(
                  controller: _scrollController,
                  child: Column(children: _Messages),
                ),
              ),
            ),
          ),
          Align(
            alignment: AlignmentGeometry.bottomCenter,
            child: Container(
              padding: EdgeInsets.symmetric(horizontal: 10),
              decoration: BoxDecoration(
                color: Color.fromARGB(255, 253, 251, 247),
              ),
              child: SizedBox(
                width: double.infinity,
                child: Row(
                  children: [
                    SizedBox(
                      width: MediaQuery.of(context).size.width - 80,
                      child: Input(
                        "message",
                        TextInputType.multiline,
                        _messageInputController,
                        "",
                        () => true,
                      ),
                    ),
                    SizedBox(width: 10),
                    //TODO: Make your own simple send button prefab
                    InkWell(
                      onTap: () {
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
                      borderRadius: BorderRadius.circular(50),
                      child: SizedBox(
                        height: 50,
                        width: 50,
                        child: Icon(Icons.send),
                      ),
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
