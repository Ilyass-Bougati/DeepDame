import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';

class General extends StatefulWidget {
  const General({super.key});

  @override
  State<StatefulWidget> createState() => _GeneralCreateState();
}

class _GeneralCreateState extends State<General> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(bottomNavigationBar: Utils.navbar);
  }
}
