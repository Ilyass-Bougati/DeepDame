// ignore_for_file: no_logic_in_create_state
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

// ignore: must_be_immutable
class Input extends StatefulWidget {
  final TextInputType type;
  final TextEditingController controller;
  final String placeholder;
  final bool Function() verification;
  Future<bool> Function()? onLostFocus;
  String? onLostFocusErrorMessage;

  final String errorMessage;

  Input(
    this.placeholder,
    this.type,
    this.controller,
    this.errorMessage,
    this.verification, {
    super.key,
  });

  Input.onLostFocus(
    this.placeholder,
    this.type,
    this.controller,
    this.errorMessage,
    this.verification,
    this.onLostFocus,
    this.onLostFocusErrorMessage, {
    super.key,
  });

  @override
  State<Input> createState() => _createState(
    placeholder: placeholder,
    type: type,
    controller: controller,
    verification: verification,
    errorMessage: errorMessage,
    onLostFocus: this.onLostFocus,
    onLostFocusErrorMessage: this.onLostFocusErrorMessage,
  );
}

class _createState extends State<Input> {
  final TextInputType type;
  final TextEditingController controller;
  final String placeholder;
  final bool Function() verification;
  Future<bool> Function()? onLostFocus;
  String? onLostFocusErrorMessage;

  //The String passed through the constructor ,
  //it is a constant and should hold the value of the text only
  final String errorMessage;

  //The variable text that will change the state of the input Normal ==> Error.
  String? _errorMessage = null;

  _createState({
    required this.placeholder,
    required this.type,
    required this.controller,
    required this.verification,
    required this.errorMessage,
    this.onLostFocus,
    this.onLostFocusErrorMessage,
  });

  @override
  Widget build(BuildContext context) => onLostFocus == null ? buildFocusUnnecessary(context) : buildFocusNecessary(context);

  Widget buildFocusUnnecessary(BuildContext context) {
    return TextField(
      onChanged: (value) => setState(() {
        if (!verification()) {
          _errorMessage = errorMessage;
        } else {
          _errorMessage = null;
        }
      }),
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
        errorText: _errorMessage,

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

  Widget buildFocusNecessary(BuildContext context) {
    
    return Focus(
      onFocusChange: (focus) async {
        if (!focus && _errorMessage == null) {
          if (await onLostFocus!() == true) {
            setState(() {
              _errorMessage = onLostFocusErrorMessage;
            });
          } else {
            setState(() {
              _errorMessage = null;
            });
          }
        }
      },
      child: TextField(
        onChanged: (value) => setState(() {
          if (!verification()) {
            _errorMessage = errorMessage;
          } else {
            _errorMessage = null;
          }
        }),
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
          errorText: _errorMessage,

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
      ),
    );
  }
}
