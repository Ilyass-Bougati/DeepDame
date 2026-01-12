import 'package:audioplayers/audioplayers.dart';
import 'package:deepdame/pages/Preferences.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';

class NavbarButton extends StatelessWidget {
  final String label;
  final IconData icon;
  final VoidCallback onTap;
  final bool isSelected;

  const NavbarButton({
    super.key,
    required this.label,
    required this.icon,
    required this.onTap,
    this.isSelected = false,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () async {
        onTap();
        if (Preferences.vibrationActive) {
          await HapticFeedback.selectionClick();
        }
        await player.play(AssetSource('assets/sfx/sfx_1.mp3'));
      },
      borderRadius: BorderRadius.circular(40),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 0, vertical: 0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            SizedBox(
              height: 39,
              width: 50,
              child: Icon(
                icon,
                color: isSelected
                    ? Color.fromARGB(255, 170, 188, 180)
                    : Colors.black,
              ),
            ),
            Text(
              label,
              style: GoogleFonts.nunito(
                fontSize: 12,
                fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
                color: isSelected
                    ? Color.fromARGB(255, 170, 188, 180)
                    : Colors.black,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
