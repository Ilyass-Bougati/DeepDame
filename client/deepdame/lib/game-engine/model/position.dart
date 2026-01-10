class Position {
  final int row;
  final int col;

  const Position(this.row, this.col);

  factory Position.fromJson(Map<String, dynamic> json) {
    return Position(json['row'], json['col']);
  }

  Map<String, dynamic> toJson() {
    return {
      'row': row,
      'col': col,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Position &&
          runtimeType == other.runtimeType &&
          row == other.row &&
          col == other.col;

  @override
  int get hashCode => row.hashCode ^ col.hashCode;

  @override
  String toString() => 'Position($row, $col)';
}