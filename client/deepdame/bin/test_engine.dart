import 'dart:io';
import 'dart:math';

import 'package:deepdame/game-engine/logic/game_engine.dart';
import 'package:deepdame/game-engine/model/board.dart';
import 'package:deepdame/game-engine/model/game_state.dart';
import 'package:deepdame/game-engine/model/move.dart';
import 'package:deepdame/game-engine/model/piece.dart';
import 'package:deepdame/game-engine/model/piece_type.dart';
import 'package:deepdame/game-engine/model/position.dart';

void main() {
  final engine = GameEngine();

  GameState state = GameState.newGame("test-session-1"); 

  print("=== CHECKERS ENGINE TEST (DART) ===");
  print("Format moves as: 'r1 c1 r2 c2' (e.g., '5 0 4 1')");

  while (!state.isGameOver) {
    printBoard(state.board);
    print("Turn: ${state.currentTurn.name.toUpperCase()}");

    if (state.currentTurn == PieceType.black) {
      stdout.write("Enter Move: ");
      
      String? input = stdin.readLineSync();

      if (input == null || input.isEmpty) continue;

      try {
        var parts = input.trim().split(RegExp(r'\s+'));
        if (parts.length != 4) throw FormatException("Please enter 4 numbers");

        int r1 = int.parse(parts[0]);
        int c1 = int.parse(parts[1]);
        int r2 = int.parse(parts[2]);
        int c2 = int.parse(parts[3]);

        Move move = Move(Position(r1, c1), Position(r2, c2));
        
        state = engine.applyMove(state, move);

      } catch (e) {
        print("INVALID MOVE: $e");
      }
    } else {
      print("AI (White) is thinking...");
      
      var legalMoves = engine.getLegalMoves(state.board, PieceType.white);

      if (legalMoves.isEmpty) {
        print("AI has no moves! Black Wins!");
        state.finishGame(PieceType.black);
        break;
      }

      Move aiMove = legalMoves[Random().nextInt(legalMoves.length)];
      
      print("AI Moved: (${aiMove.from.row},${aiMove.from.col}) -> (${aiMove.to.row},${aiMove.to.col})");
      
      state = engine.applyMove(state, aiMove);
    }
  }
  
  print("GAME OVER! Winner: ${state.winner}");
}

void printBoard(Board board) {
  print("    0 1 2 3 4 5 6 7");
  for (int r = 0; r < 8; r++) {
    stdout.write("$r |");
    for (int c = 0; c < 8; c++) {
      Piece? p = board.getPiece(Position(r, c));
      if (p == null) {
        stdout.write(" .");
      } else {
        String symbol = (p.type == PieceType.black) ? "b" : "w";
        if (p.isKing) symbol = symbol.toUpperCase();
        stdout.write(" $symbol");
      }
    }
    print("");
  }
}