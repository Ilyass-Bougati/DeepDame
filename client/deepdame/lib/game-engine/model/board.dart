import 'piece.dart';
import 'piece_type.dart';
import 'position.dart';

class Board {
  static const int size = 8;
  
  late final List<List<Piece?>> grid;

  Board() {
    grid = List.generate(
      size, 
      (_) => List<Piece?>.filled(size, null),
    );
    _initializeBoard();
  }

  Board.fromGrid(this.grid);

  void _initializeBoard() {
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) {
        if ((row + col) % 2 != 0) {
          if (row < 3) {
            grid[row][col] = Piece.regular(PieceType.white);
          } else if (row > 4) {
            grid[row][col] = Piece.regular(PieceType.black);
          }
        }
      }
    }
  }

  bool isValidBounds(Position p) {
    return p.row >= 0 && p.row < size && p.col >= 0 && p.col < size;
  }

  Piece? getPiece(Position position) {
    if (!isValidBounds(position)) return null;
    return grid[position.row][position.col];
  }

  void setPiece(Position position, Piece? piece) {
    if (isValidBounds(position)) {
      grid[position.row][position.col] = piece;
    }
  }

  void removePiece(Position position) {
    setPiece(position, null);
  }

  List<List<Piece?>> getGrid() {
    return grid;
  }

  // Debug printing
  void printBoard() {
    for (int i = 0; i < size; i++) {
      var lineBuffer = StringBuffer();
      for (int j = 0; j < size; j++) {
        Piece? p = grid[i][j];
        if (p == null) {
          lineBuffer.write(". ");
        } else if (p.type == PieceType.black) {
          lineBuffer.write(p.isKing ? "B " : "b ");
        } else {
          lineBuffer.write(p.isKing ? "W " : "w ");
        }
      }
      print(lineBuffer.toString());
    }
  }
}