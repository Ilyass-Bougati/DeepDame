import '../model/board.dart';
import '../model/move.dart';
import '../model/piece.dart';
import '../model/piece_type.dart';
import '../model/position.dart';

class MoveValidator {
  
  bool isValidMove(Board board, Move? move, PieceType player) {
    if (move == null) return false;
    
    List<Move> moveList = getLegalMoves(board, player);
    return moveList.contains(move);
  }

  List<Move> getLegalMoves(Board board, PieceType player) {
    List<Move> jumps = [];
    List<Move> slides = [];

    for (int row = 0; row < Board.size; row++) {
      for (int col = 0; col < Board.size; col++) {
        Position pos = Position(row, col);
        Piece? piece = board.getPiece(pos);
        
        if (piece != null && piece.type == player) {
          _findMovesForPiece(board, pos, piece, jumps, slides);
        }
      }
    }

    if (jumps.isNotEmpty) {
      return jumps;
    }
    return slides;
  }

  List<List<int>> _getDirections(Piece piece) {
    if (piece.isKing) {
      return [
        [-1, -1], [-1, 1], 
        [1, -1], [1, 1]
      ];
    }
    if (piece.type == PieceType.white) {
      return [
        [1, -1], [1, 1]
      ];
    } else {
      return [
        [-1, -1], [-1, 1]
      ];
    }
  }

  void _findMovesForPiece(
    Board board, 
    Position pos, 
    Piece piece, 
    List<Move> jumps, 
    List<Move> slides
  ) {
    List<List<int>> directions = _getDirections(piece);

    for (List<int> direction in directions) {
      int dRow = direction[0];
      int dCol = direction[1];

      Position next = Position(pos.row + dRow, pos.col + dCol);
      Position landing = Position(pos.row + (2 * dRow), pos.col + (2 * dCol));

      Piece? middlePiece = board.getPiece(next);
      Piece? landingPiece = board.getPiece(landing);

      if (middlePiece != null && middlePiece.type != piece.type) {
        if (board.isValidBounds(landing) && landingPiece == null) {
          jumps.add(Move(pos, landing));
        }
      }

      if (board.isValidBounds(next) && board.getPiece(next) == null) {
        slides.add(Move(pos, next));
      }
    }
  }
}