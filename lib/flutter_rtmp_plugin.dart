import 'dart:async';

import 'package:flutter/services.dart';

class FlutterRtmpPlugin {
  static const MethodChannel _channel =
      const MethodChannel('flutter_rtmp_plugin');

  static startLive(String url) async {
    await _channel.invokeMethod('startLive', {"url" : url});
  }

}
