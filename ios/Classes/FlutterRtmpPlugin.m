#import "FlutterRtmpPlugin.h"
#import "LFViewController.h"

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
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([call.method isEqualToString:@"startLive"]) {
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

@end
