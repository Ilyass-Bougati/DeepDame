// import 'dart:ffi';

import 'package:deepdame/prefabs/Input.dart';
import 'package:flutter/material.dart';

class Connect extends StatelessWidget {
  // final Bool type;

  // const Connect(this.type, {super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [Input(TextEditingController())],
      ),
    );
  }
}
