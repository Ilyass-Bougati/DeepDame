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
    try {
      print(request.toJson());
      return (await dio.post(
        "$newApi/$route",
        data: request.toJson(),
        options: Options(contentType: Headers.jsonContentType),
      )).data;
    } on DioException catch (e) {
      print("Error: ${e.response?.statusCode} - ${e.message}");
      rethrow;
    }
  }
}
