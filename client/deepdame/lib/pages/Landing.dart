import 'package:deepdame/prefabs/SubmitButton.dart';
import 'package:flutter/material.dart';

class Landing extends StatelessWidget {
  const Landing({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Column(
              children: [
                Submitbutton(
                  "Log in",
                  Color.fromARGB(255,170,188,180),
                  Color.fromARGB(255,119,133,127),
                  () => print("Action example")
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
