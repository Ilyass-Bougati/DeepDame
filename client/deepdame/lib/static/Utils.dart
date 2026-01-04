import 'package:cookie_jar/cookie_jar.dart';
import 'package:deepdame/models/User.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

PersistCookieJar? persistCookieJar;
final dio = Dio();

class Utils {
  static Widget? navbar;
  static User? userDetails;
  static String API_URL = "http://192.168.1.26:8080/api/v1";

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
}
