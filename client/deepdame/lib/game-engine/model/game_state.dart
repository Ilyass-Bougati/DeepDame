import 'board.dart';
import 'piece_type.dart';

class GameState {
  
  final String id;
  
  final Board board;
  
  PieceType _currentTurn;
  bool _isGameOver;
  PieceType? _winner;

  GameState.newGame(this.id)
      : board = Board(),
        _currentTurn = PieceType.black,
        _isGameOver = false,
        _winner = null;

  GameState.load({
    required this.id,
    required this.board,
    required PieceType currentTurn,
    required bool isGameOver,
    PieceType? winner,
  })  : _currentTurn = currentTurn,
        _isGameOver = isGameOver,
        _winner = winner;

  void switchTurn() {
    if (_isGameOver) return;
    _currentTurn = (_currentTurn == PieceType.black) 
        ? PieceType.white 
        : PieceType.black;
  }

  void finishGame(PieceType winner) {
    _isGameOver = true;
    _winner = winner;
  }

  PieceType get currentTurn => _currentTurn;
  bool get isGameOver => _isGameOver;
  PieceType? get winner => _winner;
}