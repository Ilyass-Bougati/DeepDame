import 'package:deepdame/game-engine/logic/game_engine.dart';
import 'package:deepdame/game-engine/model/board.dart';
import 'package:deepdame/game-engine/model/game_state.dart';
import 'package:deepdame/game-engine/model/move.dart';
import 'package:deepdame/game-engine/model/piece.dart';
import 'package:deepdame/game-engine/model/piece_type.dart';
import 'package:deepdame/game-engine/model/position.dart';
import 'package:deepdame/prefabs/GameBoard.dart';
import 'package:deepdame/prefabs/GamePiece.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Game extends StatefulWidget {
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
  }


  void _onPieceTap(Position pos) {
    // 1. Get the piece at this position
    Piece? p = currentState.board.getPiece(pos);

    // 2. Validation: Must be a piece, and must be YOUR turn
    if (p == null || p.type != currentState.currentTurn) {
      return;
    }

    setState(() {
      selectedPos = pos;
      
      // 3. Get ALL legal moves for the current player from the engine
      // (The engine handles forced jumps logic here)
      List<Move> allMoves = engine.getLegalMoves(currentState.board, currentState.currentTurn);

      // 4. Filter list to show ONLY moves starting from the selected piece
      currentLegalMoves = allMoves.where((m) => m.from == pos).toList();
      
      // Debug print to verify
      print("Selected: $pos. Available moves: ${currentLegalMoves.length}");
    });
  }

  void _onMoveTap(Move move) {
    setState(() {
      try {
        // 1. Apply the move
        currentState = engine.applyMove(currentState, move);
        
        // 2. Clear selection
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
                  color: const Color.fromARGB(204, 0, 0, 0),
                  shape: BoxShape.circle,
                  boxShadow: const [
                     BoxShadow(color: Colors.black26, blurRadius: 4, offset: Offset(0, 2))
                  ]
                ),
              ),
            ),
          );
        }
        else if (piece != null) {
          bool isBlack = piece.type == PieceType.black;
          
          cellContent = SizedBox(
            width: 40,
            height: 40,
            child: Gamepiece(
              isBlack, 
              !isBlack, // simple light/dark logic for now
              onTap: () => _onPieceTap(currentPos),
            ),
          );
        }
        else {
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
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              "Turn: ${currentState.currentTurn == PieceType.black ? "Black" : "White"}",
              style: GoogleFonts.lora(fontSize: 20, fontWeight: FontWeight.bold),
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
            FloatingActionButton.small(
              child: const Icon(Icons.refresh),
              onPressed: () {
                setState(() {
                  currentState = GameState.newGame("new-id");
                  selectedPos = null;
                  currentLegalMoves = [];
                });
              },
            )
          ],
        ),
      ),
    );
  }
}