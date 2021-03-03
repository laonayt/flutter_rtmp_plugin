## 前言：
Flutter的插件分为两种：Package和Plugin。
Package是纯Dart的，主要用在组件展示。类似：日历、下拉刷新等。
Plugin是通过Flutter的channel调用原生，原生来实现功能；主要用在功能性上。类似：拍照、录音等。
rtmp推流为功能型的，所以此文讲的推流插件是Plugin类型。

## 环境：
Mac、AS、Xcode、Flutter1.12
##项目地址：[flutter_rtmp_plugin](https://github.com/laonayt/flutter_rtmp_plugin)

## 1、创建Plugin工程
1、AS中new flutter project 选择plugin选项，按提示语一路next，此处不再赘述。
![1.png](https://upload-images.jianshu.io/upload_images/2166188-85f2a06d993a64c7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2、创建完毕，打开工程目录如下，主要修改lib、ios、android三个目录。
![2.png](https://upload-images.jianshu.io/upload_images/2166188-f700a11da9d6e5b1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 2、Flutter端代码书写
```
class FlutterRtmpPlugin {
//1、创建channel
  static const MethodChannel _channel =
      const MethodChannel('flutter_rtmp_plugin');
//2、开始直播的api，参数为推流地址
  static startLive(String url) async {
    await _channel.invokeMethod('startLive', {"url" : url});
  }

}
```
在外部的使用方法：
var url = "rtmp://192.168.101.240/rtmplive/test";
FlutterRtmpPlugin.startLive(url);

## 3、 iOS端代码书写
1、ios目录如下
![image.png](https://upload-images.jianshu.io/upload_images/2166188-eab344758ffa703e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2、ios端的推流使用的是[LFLiveKit](https://github.com/LaiFengiOS/LFLiveKit)
，首先打开ios目录下后缀为.podspec的文件。
添加依赖
```
s.dependency 'LFLiveKit'
```
3、FlutterRtmpPlugin.m文件
实现逻辑是：ios接收到名为‘ flutter_rtmp_plugin’的channel，回调‘ startLive’这个方法，参数为‘ url’；ios则调起推流页面，推流到url。
```
@interface FlutterRtmpPlugin ()
@property(strong, nonatomic) UIViewController *viewController;
@end

@implementation FlutterRtmpPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_rtmp_plugin"
            binaryMessenger:[registrar messenger]];

  UIViewController *viewController =
    [UIApplication sharedApplication].delegate.window.rootViewController;

  FlutterRtmpPlugin* instance = [[FlutterRtmpPlugin alloc] initWithViewController:viewController];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (instancetype)initWithViewController:(UIViewController *)viewController {
  self = [super init];
  if (self) {
    self.viewController = viewController;
  }
  return self;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([call.method isEqualToString:@"startLive"]) {
      NSDictionary * dict = call.arguments;
      NSLog(@"流地址是 %@",dict[@"url"]);

      LFViewController *liveVC = [[LFViewController alloc] init];
      liveVC.liveUrl = dict[@"url"];
      liveVC.modalPresentationStyle = UIModalPresentationFullScreen;
      [self.viewController presentViewController:liveVC animated:YES completion:nil];
  }
  else {
    result(FlutterMethodNotImplemented);
  }
}
```
LFViewController就是纯ios代码了。你可以在Assets文件夹下添加资源文件、也可以使用xib搭建UI。

## 4、 Android端代码书写
1、android目录如下
![5.png](https://upload-images.jianshu.io/upload_images/2166188-fb53f79d514aae3f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
2、android端的推流使用的是[SopCastComponent](https://github.com/LaiFeng-Android/SopCastComponent)
在plugin模块的gradle中添加依赖
![6.png](https://upload-images.jianshu.io/upload_images/2166188-5b0b2911a6bd25d2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
```
dependencies {
    implementation 'androidx.constraintlayout:constraintlayout:+'
    //LFLive
    implementation 'com.laifeng:sopcast-sdk:1.0.4'
}
```


3、FlutterRtmpPlugin类
注意：flutter1.12版本的回调方法如下
```
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    FlutterRtmpPlugin plugin = new FlutterRtmpPlugin();
    plugin.context = flutterPluginBinding.getApplicationContext();
    final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_rtmp_plugin");
    channel.setMethodCallHandler(plugin);
  }
```
flutter1.12版本之前则在registerWith方法中回调
```
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_rtmp_plugin");
    channel.setMethodCallHandler(new FlutterRtmpPlugin());
  }
```
写的时候因为这个，总是回调不到，走了不少弯路。

拿到app的当前context或activity之后就可以进行页面跳转了，逻辑和ios一样
```
@Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if(call.method.equals("startLive")){
      Intent intent = new Intent(context,LivingActivity.class);
      String url = call.argument("url");
      intent.putExtra("url",url);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
      context.startActivity(intent);

    } else {
      result.notImplemented();
    }
  }
```
LivingActivity 就是纯android原生代码了，在这里注意下，flutter调起的页面自带一个导航栏，
在LivingActivity中添加进行去除
```
requestWindowFeature(Window.FEATURE_NO_TITLE);
```


