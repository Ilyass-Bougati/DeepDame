import 'package:deepdame/game-engine/logic/game_engine.dart';
import 'package:deepdame/game-engine/model/game_state.dart';
import 'package:deepdame/game-engine/model/move.dart';
import 'package:deepdame/game-engine/model/position.dart';
import 'package:deepdame/prefabs/GameBoard.dart';
import 'package:deepdame/prefabs/GamePiece.dart';
import 'package:flutter/material.dart';

// Draw board --> Once.
// instantiate GameEngine --> Once.
// instantiate pieces --> Every move.

class Game extends StatefulWidget {
  const Game({super.key});

  @override
  State<StatefulWidget> createState() => _GameCreateState();
}

class _GameCreateState extends State<Game> {
  late final GameEngine engine;
  late final GameState currentState;

  List<Row> rows = [];

  void refreshPieces() {
    rows = [];
    //Build the pieces :
    for (String s in currentState.board.toString().split("\n")) {
      List<Widget> row = [];
      for (String ss in s.characters) {
        late Widget toAdd;
        switch (ss) {
          case '.':
            toAdd = SizedBox(width: 40, height: 40);
            break;
          case 'w':
            toAdd = SizedBox(
              width: 40,
              height: 40,
              child: Gamepiece(false, true),
            );
            break;
          case 'b':
            toAdd = SizedBox(
              width: 40,
              height: 40,
              child: Gamepiece(true, false),
            );
            break;
          case ' ':
            continue;
        }
        row.add(toAdd);
      }
      rows.add(Row(mainAxisSize: MainAxisSize.min, children: row));
    }
  }

  @override
  void initState() {
    super.initState();
    engine = GameEngine();
    currentState = GameState.newGame("test-id");

    refreshPieces();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Color.fromARGB(255, 253, 251, 247),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Stack(
              alignment: AlignmentGeometry.center,
              children: [
                Gameboard(),
                Column(children: rows),
              ],
            ),
            FloatingActionButton(
              onPressed: () {
                engine.applyMove(
                  currentState,
                  Move(Position(5, 2), Position(4, 3)),
                );

                setState(() {
                  refreshPieces();
                });
              },
            ),
          ],
        ),
      ),
    );
  }
}
