import 'package:flutter/material.dart';

class Input extends StatelessWidget {
  final TextEditingController controller;

  const Input(this.controller, {super.key});

  @override
  Widget build(BuildContext context) {
    return TextField(
      cursorColor: Color.fromARGB(255, 170, 188, 180) ,
      controller: controller,
      decoration: InputDecoration(
        focusColor: Color.fromARGB(255, 170, 188, 180),
        
        enabledBorder: UnderlineInputBorder(
          borderSide: BorderSide(
            color: Color.fromARGB(255, 170, 188, 180),
            strokeAlign: BorderSide.strokeAlignOutside,
            width: 1.0
          ),
        ),
        
        focusedBorder: UnderlineInputBorder(
          borderSide: BorderSide(
            color: Color.fromARGB(255, 170, 188, 180),
            strokeAlign: BorderSide.strokeAlignOutside,
            width: 3.0,
          ),
        ),

        


      ),
    );
  }
}
