import 'dart:convert';

import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class General extends StatefulWidget {
  const General({super.key});
  @override
  State<StatefulWidget> createState() => _GeneralCreateState();
}

class _GeneralCreateState extends State<General> {
  static List<Text> _Messages = [];

  @override
  void initState() {
    super.initState();
    Utils.onGeneralChatLoaded = (json) {
      setState(() {
        var claimMap = jsonDecode(json);
        _Messages.add(
          Text("${claimMap['user']['username']} : ${claimMap['message']}"),
        );
      });
    };
  }

  //TODO: Style the text
  @override
  void dispose() {
    super.dispose();
    Utils.onGeneralChatLoaded = (json) {
      var claimMap = jsonDecode(json);
      _Messages.add(
        Text("${claimMap['user']['username']} : ${claimMap['message']}"),
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

      body: Container(
        margin: EdgeInsets.symmetric(horizontal: 30.5, vertical: 10),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(41),
          color: Color.fromARGB(255, 235, 229, 222),
        ),
        child: Container(padding: EdgeInsets.all(15),
        child: SizedBox(
          height: double.infinity,
          width: double.infinity,
          child: SingleChildScrollView(child: Column(children: _Messages)),)
        ),
      ),
      bottomNavigationBar: Utils.getNavbar(context, 1),
    );
  }
}
