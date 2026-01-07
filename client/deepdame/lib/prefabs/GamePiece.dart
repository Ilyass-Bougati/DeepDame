import 'package:flutter/material.dart';

class Gamepiece extends StatelessWidget {
  final bool own;
  final bool isLight;

  const Gamepiece(
    this.own,
    this.isLight, {
    super.key,
  }); //Light / Dark pieces

  @override
  Widget build(BuildContext context) =>
      own ? _buildOwnPiece() : _buildOtherPiece();

  Widget _buildOwnPiece() {
    return GestureDetector(
      onTap: () {
        print("Show legal moves");
      },
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
              color: const Color.fromARGB(49, 255, 255, 255),
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
