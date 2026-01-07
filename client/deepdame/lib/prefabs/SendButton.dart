import 'package:flutter/material.dart';

class SendButton extends StatefulWidget {
  final Function() onTap;
  final Widget child;
  final Color color;
  final Color pressedColor;

  const SendButton(this.child, this.onTap, this.color, this.pressedColor);
  @override
  State<SendButton> createState() =>
      _SendButtonCreateState(child, onTap, color, pressedColor);
}

class _SendButtonCreateState extends State<SendButton> {
  final Function() onTap;
  final Widget child;
  final Color color;
  final Color pressedColor;

  late Color? _color = color;

  _SendButtonCreateState(this.child, this.onTap, this.color, this.pressedColor);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      onTapDown: (details) => {
        setState(() {
          _color = pressedColor;
        }),
      },
      onTapUp: (details) => {
        setState(() {
          _color = color;
        }),
      },
      onTapCancel: () => {
        setState(() {
          _color = color;
        }),
      },
      child: Container(
        width: 50,
        height: 50,
        decoration: BoxDecoration(
          color: _color,
          borderRadius: BorderRadius.circular(50),
        ),
        child: child,
      ),
    );
  }
}
