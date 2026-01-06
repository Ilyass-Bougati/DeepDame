import 'piece_type.dart';

class Piece {
  final PieceType type;
  final bool isKing;

  const Piece(this.type, this.isKing);

  // Static helper to create a regular piece
  static Piece regular(PieceType type) {
    return Piece(type, false);
  }

  // Returns a new Piece that is a King
  Piece promote() {
    return Piece(type, true);
  }
}