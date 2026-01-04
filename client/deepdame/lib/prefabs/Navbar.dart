import 'package:deepdame/pages/Friends.dart';
import 'package:deepdame/pages/General.dart';
import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/pages/Preferences.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:google_fonts/google_fonts.dart';

// ignore: must_be_immutable
class Navbar extends StatelessWidget {
  Navbar({super.key});

  int selectedIndex = -1;
  //TODO:Add opaque overlay on the current page's button
  //FIXME:REMOVE THIS OVERCOMPLEXIFIED , UNOPTIMIZED , EYE-BURNING SHIT
  final List<MapEntry<NavbarButton, bool>> buttons = const [
    MapEntry(
      NavbarButton('assets/vectors/icons/home.svg', "Home", Landing.new),
      false,
    ),
    MapEntry(
      NavbarButton('assets/vectors/icons/chat.svg', "General", General.new),
      false,
    ),
    MapEntry(
      NavbarButton('assets/vectors/icons/friends.svg', "Friends", Friends.new),
      false,
    ),
    MapEntry(
      NavbarButton(
        'assets/vectors/icons/preferences.svg',
        "Settings",
        Preferences.new,
      ),
      false,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.symmetric(horizontal: 20, vertical: 19),
      height: MediaQuery.of(context).size.height / 9,
      color: Color.fromARGB(200, 235, 229, 222),
      child: GestureDetector(
        child: Container(
          padding: EdgeInsets.only(left: 10, right: 10),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              buttons[0].key,
              buttons[1].key,
              buttons[2].key,
              buttons[3].key,
            ],
          ),
        ),
      ),
    );
  }
}

class NavbarButton extends StatefulWidget {
  final Widget Function() page;
  final String iconAssetName;
  final String buttonText;
  const NavbarButton(
    this.iconAssetName,
    this.buttonText,
    this.page, {
    super.key,
  });

  @override
  State<StatefulWidget> createState() =>
      // ignore: no_logic_in_create_state
      NavbarButtonCreateState(iconAssetName, buttonText, page);

  void resetColor() {}
}

class NavbarButtonCreateState extends State<NavbarButton> {
  final Widget Function() page;
  final String iconAssetName;
  final String buttonText;
  late final SvgPicture icon;
  late SvgPicture _icon;

  NavbarButtonCreateState(this.iconAssetName, this.buttonText, this.page) {
    icon = SvgPicture.asset(iconAssetName);
    _icon = icon;
  }
  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        Navigator.pushReplacement(
          context,
          PageRouteBuilder(
            pageBuilder: (context, animation1, animation2) => page(),
            transitionDuration: Duration.zero,
            reverseTransitionDuration: Duration.zero,
          )
        );
      },
      child: SizedBox(
        width: 70,
        child: Column(
          children: [
            SizedBox(height: 35, width: 35, child: _icon),
            Text(
              buttonText,
              style: GoogleFonts.nunito(fontWeight: FontWeight.bold),
            ),
          ],
        ),
      ),
    );
  }
}
