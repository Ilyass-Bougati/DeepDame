import 'package:http/http.dart' as http;

class Utils {
  

  // Http specific methods
  static Future<http.Response> get(String url){
    return http.get(Uri.parse(url));
  }

  static Future<http.Response> post(String url , Map body ){
    return http.post(Uri.parse(url) , body: body);
  }


  
}