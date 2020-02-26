import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_rtmp_plugin/flutter_rtmp_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('RtmpPlugin example app'),
        ),
        body: RaisedButton(
          child: Text("开始直播"),
          onPressed: () {
            var url = "rtmp://192.168.101.240/rtmplive/test";
            FlutterRtmpPlugin.startLive(url);
          },
        ),
      ),
    );
  }
}
