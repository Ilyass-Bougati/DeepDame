import 'package:flutter/material.dart';
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
      onTap: onTap,
      borderRadius: BorderRadius.circular(40), // Optional: rounded clicks
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 0, vertical: 0),
        child: Column(
          mainAxisSize: MainAxisSize.min, // Shrink to fit content
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
                color: isSelected ? Color.fromARGB(255, 170, 188, 180) : Colors.black,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
