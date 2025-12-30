import 'dart:convert';
import 'package:http/http.dart' as http;

class Utils {
  static String API_URL = "http://192.168.1.188:8080/api";
  static Map<String, String> headers = {"Content-type": "application/json"};

  static Future<http.Response> postRequest(dynamic request , String route ) async {
    return await http.post(
      Uri.parse("$API_URL/$route"),
      headers: headers,
      body: jsonEncode(request),
    );
  }

  static Future<http.Response> api_postRequest(dynamic request , String route , String newApi) async {
    return await http.post(
      Uri.parse("$newApi/$route"),
      headers: headers,
      body: jsonEncode(request),
    );
  }
}
