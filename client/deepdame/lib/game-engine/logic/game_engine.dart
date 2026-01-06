import '../model/board.dart';
import '../model/game_state.dart';
import '../model/move.dart';
import '../model/piece.dart';
import '../model/piece_type.dart';
import '../model/position.dart';
import 'move_validator.dart';

class GameEngine {
  final MoveValidator validator;

  GameEngine() : validator = MoveValidator();

  GameState applyMove(GameState state, Move move) {
    Board board = state.board;
    PieceType player = state.currentTurn;

    if (!validator.isValidMove(board, move, player)) {
      throw ArgumentError("Invalid move");
    }

    Piece piece = board.getPiece(move.from)!;
    
    board.removePiece(move.from);
    board.setPiece(move.to, piece);

    if (move.isJump) {
      Position? jumpedPos = move.jumpedPosition;
      if (jumpedPos != null) {
        board.removePiece(jumpedPos);
      }
    }

    _checkPromotion(board, piece, move.to);
    _checkGameOver(state);

    if (!state.isGameOver) {
      state.switchTurn();
    }

    return state;
  }

  List<Move> getLegalMoves(Board board, PieceType player) {
    return validator.getLegalMoves(board, player);
  }

  void _checkPromotion(Board board, Piece piece, Position position) {
    
    if (piece.type == PieceType.black && position.row == 0) {
      Piece kingPiece = board.getPiece(position)!.promote();
      board.setPiece(position, kingPiece);
    } 
    else if (piece.type == PieceType.white && position.row == Board.size - 1) {
      Piece kingPiece = board.getPiece(position)!.promote();
      board.setPiece(position, kingPiece);
    }
  }

  void _checkGameOver(GameState state) {
    PieceType nextTurn = (state.currentTurn == PieceType.black) 
        ? PieceType.white 
        : PieceType.black;
    
    List<Move> nextMoves = validator.getLegalMoves(state.board, nextTurn);

    if (nextMoves.isEmpty) {
      state.finishGame(state.currentTurn);
    }
  }
}