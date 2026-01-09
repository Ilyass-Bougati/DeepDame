import 'package:flutter/material.dart';

class Gamepiece extends StatelessWidget {
  final bool own;
  final bool isLight;
  final bool isKing;
  final VoidCallback? onTap;

  const Gamepiece(this.own, this.isLight, this.isKing, {this.onTap, super.key});

  @override
  Widget build(BuildContext context) =>
      own ? _buildOwnPiece() : _buildOtherPiece();

  Widget _buildOwnPiece() {
    return GestureDetector(
      onTap: onTap,
      child: _buildAsset(isLight ? Color(0xFFD08C80) : Color(0xFF7D949E)),
    );
  }

  Widget _buildOtherPiece() {
    return _buildAsset(isLight ? Color(0xFFD08C80) : Color(0xFF7D949E));
  }

  Widget _buildAsset(Color color) {
    return Stack(
      alignment: AlignmentGeometry.center,
      children: [
        Container(
          height: 30,
          width: 30,
          decoration: BoxDecoration(
            color: color,
            borderRadius: BorderRadius.circular(30),
            boxShadow: [
              BoxShadow(
                color: const Color.fromARGB(51, 0, 0, 0), // Shadow color
                spreadRadius: 2, // How much the shadow spreads
                blurRadius: 10, // Softness of the shadow
                offset: const Offset(0, 5), // Changes position (dx, dy)
              ),
            ],
          ),
        ),
        Container(
          height: 30,
          width: 30,
          decoration: BoxDecoration(
            border: Border.all(
              color: !isKing
                  ? const Color.fromARGB(47, 255, 255, 255)
                  : const Color.fromARGB(200, 255, 219, 90),
              width: 2,
            ),
            borderRadius: BorderRadius.circular(30),
          ),
        ),
        Container(
          height: 20,
          width: 20,
          decoration: BoxDecoration(
            border: Border.all(
              color: const Color.fromARGB(50, 0, 0, 0),
              width: 3,
            ),
            borderRadius: BorderRadius.circular(30),
          ),
        ),
      ],
    );
  }
}
