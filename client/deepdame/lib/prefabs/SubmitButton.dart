import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Submitbutton extends StatefulWidget {
  final String? text;
  final Color mainCol;
  final Color shadowCol;
  final void Function() action;

  const Submitbutton(
    String this.text,
    this.mainCol,
    this.shadowCol,
    this.action, {
    super.key,
  });

  @override
  State<Submitbutton> createState() =>
      // ignore: no_logic_in_create_state
      _createState(text as String, mainCol, shadowCol, action);
}

// ignore: camel_case_types
class _createState extends State<Submitbutton> {
  final String? text;
  Color? tmp;
  Color mainCol;
  final Color shadowCol;
  final void Function() action;

  _createState(String this.text, this.mainCol, this.shadowCol, this.action) {
    tmp = mainCol;
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.opaque,
      onTap: action,
      onTapDown: (details) => {
        setState(() {
          mainCol = shadowCol;
        }),
      },
      onTapUp: (details) => {
        setState(() {
          mainCol = tmp as Color;
        }),
      },
      onTapCancel: () => {
        setState(() {
          mainCol = tmp as Color;
        }),
      },
      child: Stack(
        children: [
          Column(
            children: [
              SizedBox(height: 5),
              Container(
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(66),
                  color: shadowCol,
                ),
                child: SizedBox(width: 208, height: 55),
              ),
            ],
          ),
          Container(
            decoration: BoxDecoration(
              color: mainCol,
              borderRadius: BorderRadius.circular(66),
            ),
            child: SizedBox(
              width: 208,
              height: 55,
              child: Center(
                child: Text(
                  text == null ? "" : text.toString(),
                  style: GoogleFonts.nunito(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
