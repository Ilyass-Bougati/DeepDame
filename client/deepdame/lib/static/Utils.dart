import 'dart:math';

import 'package:cookie_jar/cookie_jar.dart';
import 'package:deepdame/models/User.dart';
import 'package:deepdame/pages/Friends.dart';
import 'package:deepdame/pages/Game.dart';
import 'package:deepdame/pages/General.dart';
import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/pages/Preferences.dart';
import 'package:deepdame/prefabs/NavbarButton.dart';
import 'package:deepdame/requests/EmptyRequest.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:stomp_dart_client/stomp_dart_client.dart';

final Random random = Random();
PersistCookieJar? persistCookieJar;
final dio = Dio();

class Utils {
  static ValueNotifier<Game?>? currentGame ;
  static User? userDetails;
  static String API = "ilyass-server.taila311b0.ts.net";
  static String API_URL = "https://$API/api/v1";

  static late StompClient client;
  static Function(dynamic)? onGeneralChatMessage;

  static String getJavaDate() {
    DateTime now = DateTime.now();

    // 1. Get the default Dart string (e.g., "2026-01-06 14:52:20.763456")
    // Note: toString() uses a space, unlike toIso8601String() which uses 'T'
    String raw = now.toString();

    // 2. Check if we have microseconds (ends with 6 digits) and truncate to 3
    if (raw.length > 23) {
      return raw.substring(0, 23); // Keep "yyyy-MM-dd HH:mm:ss.SSS"
    }

    return raw;
  }

  static Widget getNavbar(BuildContext context, int currentIndex) {
    return BottomAppBar(
      color: const Color.fromARGB(255, 235, 229, 222),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          NavbarButton(
            icon: Icons.home,
            label: "Home",
            isSelected: currentIndex == 0,
            onTap: () {
              if (currentIndex != 0) {
                Navigator.pushReplacement(
                  context,
                  PageRouteBuilder(
                    pageBuilder: (context, a1, a2) => Landing(),
                    transitionDuration: Duration.zero,
                    reverseTransitionDuration: Duration.zero,
                  ),
                );
              }
            },
          ),

          NavbarButton(
            icon: Icons.chat,
            label: "General",
            isSelected: currentIndex == 1,
            onTap: () {
              if (currentIndex != 1) {
                Navigator.pushReplacement(
                  context,
                  PageRouteBuilder(
                    pageBuilder: (context, a1, a2) => General(),
                    transitionDuration: Duration.zero,
                    reverseTransitionDuration: Duration.zero,
                  ),
                );
              }
            },
          ),

          NavbarButton(
            icon: Icons.person,
            label: "Friends",
            isSelected: currentIndex == 2,
            onTap: () {
              if (currentIndex != 2) {
                Navigator.pushReplacement(
                  context,
                  PageRouteBuilder(
                    pageBuilder: (context, a1, a2) => Friends(),
                    transitionDuration: Duration.zero,
                    reverseTransitionDuration: Duration.zero,
                  ),
                );
              }
            },
          ),

          NavbarButton(
            icon: Icons.settings,
            label: "Settings",
            isSelected: currentIndex == 3,
            onTap: () {
              if (currentIndex != 3) {
                Navigator.pushReplacement(
                  context,
                  PageRouteBuilder(
                    pageBuilder: (context, a1, a2) => Preferences(),
                    transitionDuration: Duration.zero,
                    reverseTransitionDuration: Duration.zero,
                  ),
                );
              }
            },
          ),
        ],
      ),
    );
  }

  static void showLoadingDialog(BuildContext context) {
    showDialog(
      context: context,
      barrierDismissible:
          false, //comment to make the popup dismissible for debug purposes.
      builder: (BuildContext context) {
        return AlertDialog(
          contentPadding: EdgeInsets.all(25),
          backgroundColor: Color.fromARGB(255, 253, 251, 247),
          content: Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircularProgressIndicator.adaptive(
                backgroundColor: Color.fromARGB(255, 170, 188, 180),
              ),
              SizedBox(width: 20),
              Text(
                "Connecting ..",
                style: GoogleFonts.nunito(
                  color: Color.fromARGB(255, 170, 188, 180),
                  fontWeight: FontWeight.bold,
                  fontSize: 20,
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  static String errorStringGenerator(DioException e) {
    String exceptionStr = "";
    switch (e.response?.statusCode) {
      case 400:
        exceptionStr += "Error : Bad request.";
        break;

      case 401:
        exceptionStr += "Error : Bad credentials.";
        break;

      case 403:
        exceptionStr += "Error : Forbidden.";
        break;

      case 404:
        exceptionStr += "Error : Not found.";
        break;

      case 408:
        exceptionStr += "Error : Timed out.";
        break;

      case 500:
        exceptionStr += "Error : Server internal error.";
        break;
    }
    return exceptionStr;
  }

  static Future<dynamic> api_postRequest(
    dynamic request,
    String route,
    String newApi,
  ) async {
    Response? resp;
    try {
      resp = await dio.post(
        "$newApi/$route",
        data: request.toJson(),
        options: Options(contentType: Headers.jsonContentType),
      );

      return resp.data;
    } on DioException catch (e) {
      print("Error: ${e.response?.statusCode} - ${e.message}");

      throw Exception(errorStringGenerator(e));
    }
  }

  static Future<dynamic> api_getRequest(String route, String newApi) async {
    Response? resp;
    try {
      resp = await dio.get(
        "$newApi/$route",
        options: Options(contentType: Headers.jsonContentType),
      );

      return resp.data;
    } on DioException catch (e) {
      print("Error: ${e.response?.statusCode} - ${e.message}");

      throw Exception(errorStringGenerator(e));
    }
  }

  static Future<void> refreshToken() async {
    await api_postRequest(EmptyRequest().toJson(), "/auth/refresh" , API_URL);
  }

  static Future<void> clearCookies(Function() fn) async {
    await persistCookieJar!.deleteAll();
    fn();
  }
}
