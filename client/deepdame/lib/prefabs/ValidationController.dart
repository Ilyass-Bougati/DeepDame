import 'package:flutter/material.dart';

class ValidationController {
  final TextEditingController _controller = TextEditingController();
  bool _state = false;

  TextEditingController getController() {
    return _controller;
  }

  bool getState() {
    return _state;
  }

  void setState(bool state) {
    _state = state;
  }
}
