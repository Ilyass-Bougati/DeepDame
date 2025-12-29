import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class Input extends StatelessWidget {
  final TextInputType type;
  final TextEditingController controller;
  final String placeholder;
  final void Function() verification;

  const Input(this.placeholder, this.type, this.controller, this.verification, {super.key});

  @override
  Widget build(BuildContext context) {
    return TextField(
      onChanged: (value) => verification(),
      obscureText: type == TextInputType.visiblePassword ? true : false,
      keyboardType: type,
      cursorColor: Color.fromARGB(255, 170, 188, 180),
      controller: controller,

      style: GoogleFonts.nunito(
        fontWeight: FontWeight.bold,
        fontSize: 17.5,
        color: Color.fromARGB(255, 123, 152, 166),
      ),

      decoration: InputDecoration(
        focusColor: Color.fromARGB(255, 170, 188, 180),

        hintText: placeholder,
        hintStyle: GoogleFonts.nunito(
          fontWeight: FontWeight.bold,
          fontSize: 17.5,
          color: Color.fromARGB(200, 123, 152, 166),
        ),

        enabledBorder: UnderlineInputBorder(
          borderSide: BorderSide(
            color: Color.fromARGB(255, 170, 188, 180),
            strokeAlign: BorderSide.strokeAlignOutside,
            width: 2.0,
          ),
        ),

        focusedBorder: UnderlineInputBorder(
          borderSide: BorderSide(
            color: Color.fromARGB(255, 123, 152, 166),
            strokeAlign: BorderSide.strokeAlignOutside,
            width: 3.0,
          ),
        ),
      ),
    );
  }
}
