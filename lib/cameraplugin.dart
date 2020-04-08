import 'dart:async';

import 'package:flutter/services.dart';

class Cameraplugin {
  static const MethodChannel _channel =
      const MethodChannel('camera_plugin');
  static Future<List<String>> takePhoto({bool createChooser: false}) {
    return _channel
        .invokeMethod('takePhoto')
        .then((data) =>  List<String>.from(data));
  }
}
