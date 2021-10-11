import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_rtmp_plugin/flutter_rtmp_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_rtmp_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('startLive', () async {
    expect(await FlutterRtmpPlugin.startLive, '42');
  });
}
