import 'dart:io';

import 'package:cookie_jar/cookie_jar.dart';
import 'package:deepdame/pages/Landing.dart';
import 'package:deepdame/static/Utils.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';

bool? connected;

Future<void> initCookies() async {
  try {
    final appDocDir = await getApplicationDocumentsDirectory();
    final String cookiePath = "${appDocDir.path}/.cookies/";

    await Directory(cookiePath).create(recursive: true);

    persistCookieJar = PersistCookieJar(
      ignoreExpires: false,
      storage: FileStorage(cookiePath),
    );

    dio.interceptors.add(CookieManager(persistCookieJar!));
  } catch (e) {
    print(e);
  }

  runApp(const MyApp());
}


void main() {
  WidgetsFlutterBinding.ensureInitialized();
  initCookies();
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: Color.fromARGB(255, 119, 133, 127),
        ),
      ),
      debugShowCheckedModeBanner: false,
      title: 'Deep Dame',
      home: Landing(),
    );
  }
}
