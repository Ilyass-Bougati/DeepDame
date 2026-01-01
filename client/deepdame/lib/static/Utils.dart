import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:http/http.dart' as http;

var persistCookieJar;
final dio = Dio();

class Utils {
  static String API_URL = "http://192.168.1.188:8080/api";
  static Map<String, String> headers = {"Content-type": "application/json"};

  static Future<http.Response> postRequest(
    dynamic request,
    String route,
  ) async {
    return await http.post(
      Uri.parse("$API_URL/$route"),
      headers: headers,
      body: jsonEncode(request),
    );
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
      String exceptionStr = "";
      print("Error: ${e.response?.statusCode} - ${e.message}");
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

      throw Exception(exceptionStr);
    }
  }
}
