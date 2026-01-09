import 'dart:convert';

import 'package:deepdame/game-engine/logic/game_engine.dart';
import 'package:deepdame/game-engine/model/board.dart';
import 'package:deepdame/game-engine/model/game_state.dart';
import 'package:deepdame/game-engine/model/move.dart';
import 'package:deepdame/game-engine/model/piece.dart';
import 'package:deepdame/game-engine/model/piece_type.dart';
import 'package:deepdame/game-engine/model/position.dart';
import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/prefabs/GameBoard.dart';
import 'package:deepdame/prefabs/GamePiece.dart';
import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:stomp_dart_client/stomp_dart_client.dart';

class Game extends StatefulWidget {
  static late String? currentGameId;
  static String? opponent;

  const Game({super.key});

  @override
  State<StatefulWidget> createState() => _GameCreateState();
}

class _GameCreateState extends State<Game> {
  late final GameEngine engine;
  late GameState currentState;

  Position? selectedPos;
  List<Move> currentLegalMoves = [];

  @override
  void initState() {
    super.initState();
    engine = GameEngine();
    currentState = GameState.newGame("test-id");

    // Subscribing to the game , showing us every move
    Utils.client.subscribe(
      destination: '/topic/game/${Game.currentGameId}',
      callback: (StompFrame frame) {
        if (frame.body != null) {
          setState(() {
            engine.applyMove(
              currentState,
              Move.fromJson(jsonDecode(frame.body!)),
            );
          });
        }
      },
    );

    // Subscribe to end of Game , to handle game ended scenarios.
    Utils.client.subscribe(
      destination: '/topic/game/${Game.currentGameId}/game-over',
      callback: (StompFrame frame) {
        if (frame.body != null) {
          Game.currentGameId = null;
          print(frame.body);
          showDialog(
            context: context,
            builder: (context) => AlertDialog.adaptive(
              content: Column(
                spacing: 20,
                children: [
                  Text(
                    "${jsonDecode(frame.body!)['winnerColor']} wins !",
                    style: GoogleFonts.lora(
                      fontWeight: FontWeight.bold,
                      fontSize: 25,
                      color: Color(
                        (currentState.currentTurn != PieceType.black)
                            ? 0XFFFDFBF7
                            : 0xFFAABCB4,
                      ),
                    ),
                  ),
                  Submitbutton(
                    "Back",
                    Color(0xFFAABCB4),
                    Color(0xFF77857F),
                    () {
                      Navigator.pop(context);
                      WidgetsBinding.instance.addPostFrameCallback((_) {
                        Navigator.pushReplacement(
                          context,
                          PageRouteBuilder(
                            pageBuilder: (context, a1, a2) => Landing(),
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
            barrierDismissible: false,
          );
        }
      },
    );
  }

  void _onPieceTap(Position pos) {
    Piece? p = currentState.board.getPiece(pos);

    if (p == null || p.type != currentState.currentTurn) {
      return;
    }

    setState(() {
      selectedPos = pos;

      List<Move> allMoves = engine.getLegalMoves(
        currentState.board,
        currentState.currentTurn,
      );

      currentLegalMoves = allMoves.where((m) => m.from == pos).toList();
    });
  }

  void _onMoveTap(Move move) {
    setState(() {
      try {
        Utils.client.send(
          headers: {'content-type': 'application/json'},
          destination: "/app/game/${Game.currentGameId}/move",
          body: jsonEncode(move.toJson()),
        );

        selectedPos = null;
        currentLegalMoves = [];
      } catch (e) {
        print("Move failed: $e");
      }
    });
  }

  List<Widget> _buildGridRows() {
    List<Widget> rows = [];
    final grid = currentState.board.grid;

    for (int r = 0; r < Board.size; r++) {
      List<Widget> rowChildren = [];

      for (int c = 0; c < Board.size; c++) {
        Position currentPos = Position(r, c);
        Piece? piece = grid[r][c];

        Move? validMove;
        try {
          validMove = currentLegalMoves.firstWhere((m) => m.to == currentPos);
        } catch (_) {
          validMove = null;
        }

        Widget cellContent;

        if (validMove != null) {
          cellContent = GestureDetector(
            onTap: () => _onMoveTap(validMove!),
            behavior: HitTestBehavior.opaque,
            child: Container(
              width: 40,
              height: 40,
              alignment: Alignment.center,
              child: Container(
                width: 15,
                height: 15,
                decoration: BoxDecoration(
                  color: const Color.fromARGB(45, 0, 0, 0),
                  shape: BoxShape.circle,
                  boxShadow: const [
                    BoxShadow(
                      color: Colors.black26,
                      blurRadius: 4,
                      offset: Offset(0, 2),
                    ),
                  ],
                ),
              ),
            ),
          );
        } else if (piece != null) {
          bool isBlack = piece.type == PieceType.black;

          cellContent = SizedBox(
            width: 40,
            height: 40,
            child: Gamepiece(
              isBlack,
              !isBlack,
              piece.isKing, // simple light/dark logic for now
              onTap: () => _onPieceTap(currentPos),
            ),
          );
        } else {
          cellContent = const SizedBox(width: 40, height: 40);
        }

        rowChildren.add(cellContent);
      }
      rows.add(Row(mainAxisSize: MainAxisSize.min, children: rowChildren));
    }
    return rows;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 253, 251, 247),
      body: Center(
        child: Container(
          padding: EdgeInsets.symmetric(horizontal: 30),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              FloatingActionButton(
                elevation: 0.1,
                focusColor: Colors.white,
                foregroundColor: Colors.white,
                backgroundColor: Colors.white,
                onPressed: () {
                  Utils.client.send(
                    destination: "/app/game/${Game.currentGameId}/surrender",
                  );
                },
                child: Icon(Icons.flag, color: Colors.red),
              ),
              SizedBox(height: 30),

              Container(
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(25),
                  border: Border.all(
                    color: const Color.fromARGB(78, 0, 0, 0),
                    width: 2,
                  ),
                  color: Color(
                    (currentState.currentTurn == PieceType.black)
                        ? 0XFFFDFBF7
                        : 0xFFAABCB4,
                  ),
                ),
                child: SizedBox(
                  width: double.infinity,
                  child: Row(
                    children: [
                      Text(
                        "Opponent : ",
                        style: GoogleFonts.lora(
                          fontSize: 25,
                          color: Color(
                            (currentState.currentTurn != PieceType.black)
                                ? 0XFFFDFBF7
                                : 0xFFAABCB4,
                          ),
                        ),
                      ),

                      //TODO: replace with opponent name
                      Text(
                        Game.opponent ?? "AI",
                        style: GoogleFonts.lora(
                          fontSize: 25,
                          color: Color(
                            (currentState.currentTurn != PieceType.black)
                                ? 0XFFFDFBF7
                                : 0xFFAABCB4,
                          ),
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 20),

              Stack(
                alignment: Alignment.center,
                children: [
                  Gameboard(),
                  Column(
                    mainAxisSize: MainAxisSize.min,
                    children: _buildGridRows(),
                  ),
                ],
              ),

              const SizedBox(height: 20),
              Container(
                padding: EdgeInsets.all(20),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(25),
                  border: Border.all(
                    color: const Color.fromARGB(78, 0, 0, 0),
                    width: 2,
                  ),
                  color: Color(
                    (currentState.currentTurn != PieceType.black)
                        ? 0XFFFDFBF7
                        : 0xFFAABCB4,
                  ),
                ),
                child: SizedBox(
                  width: double.infinity,
                  child: Row(
                    children: [
                      Text(
                        "You : ",
                        style: GoogleFonts.lora(
                          fontSize: 25,
                          color: Color(
                            (currentState.currentTurn == PieceType.black)
                                ? 0XFFFDFBF7
                                : 0xFFAABCB4,
                          ),
                        ),
                      ),
                      Text(
                        "${Utils.userDetails!.username}",
                        style: GoogleFonts.lora(
                          fontSize: 25,
                          color: Color(
                            (currentState.currentTurn == PieceType.black)
                                ? 0XFFFDFBF7
                                : 0xFFAABCB4,
                          ),
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
