import 'position.dart';

class Move {
  final Position from;
  final Position to;

  const Move(this.from, this.to);

  factory Move.fromJson(Map<String, dynamic> json) {
    return Move(
      Position.fromJson(json['from']),
      Position.fromJson(json['to']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'from': from.toJson(),
      'to': to.toJson(),
      // 'jump' and 'jumpedPosition' are excluded here
    };
  }

  bool get isJump {
    return (from.row - to.row).abs() == 2;
  }

  // Returns the coordinate of the piece being jumped over, or null if not a jump
  Position? get jumpedPosition {
    if (!isJump) return null;

    int midRow = (from.row + to.row) ~/ 2;
    int midCol = (from.col + to.col) ~/ 2;

    return Position(midRow, midCol);
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Move &&
          runtimeType == other.runtimeType &&
          from == other.from &&
          to == other.to;

  @override
  int get hashCode => from.hashCode ^ to.hashCode;
}
