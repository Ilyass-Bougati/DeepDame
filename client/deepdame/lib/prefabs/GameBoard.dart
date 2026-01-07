import 'package:flutter/material.dart';

class Gameboard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    //build the board -> return it.
    List<Widget> rows = [];

    for (int j = 0; j < 8; j++) {
      List<Widget> row = [];
      for (int i = 0; i < 8; i++) {
        row.add(
          BoardSquare(i % 2 == 0 ? rows.length % 2 == 0 : rows.length % 2 != 0),
        );
      }
      rows.add(Row(mainAxisSize: MainAxisSize.min, children: row));
    }
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(5),
        border: Border.all(width: 5, color: Color.fromARGB(255, 122, 136, 106)),
      ),
      child: Column(children: rows),
    );
  }
}

class BoardSquare extends StatelessWidget {
  final bool isDark;
  const BoardSquare(this.isDark, {super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 40,
      width: 40,
      decoration: BoxDecoration(
        color: isDark ? Color(0xFFE8DCCA) : Color(0xFF9CAF88),
      ),
    );
  }
}
