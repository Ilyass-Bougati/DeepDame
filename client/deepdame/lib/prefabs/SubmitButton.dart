import 'package:flutter/material.dart';

class Submitbutton extends StatelessWidget {
  String? text;
  Color mainCol;
  Color shadowCol;
  void Function() action;

  Submitbutton(
    String this.text,
    this.mainCol,
    this.shadowCol,
    this.action, {
    super.key,
  });

// TODO : add fonts into the project
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: action,
      child: Container(
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
                child: Center(child: Text(
                  text == null ? "" : text.toString(),
                  style: TextStyle(color: Colors.white, fontSize: 20 , fontWeight: FontWeight.bold),
                ),)
              ),
            ),
          ],
        ),
      ),
    );
  }
}
