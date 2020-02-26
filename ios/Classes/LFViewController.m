//
//  LFViewController.m
//  LFDemo
//
//  Created by W E on 2020/2/5.
//  Copyright © 2020 zonekey. All rights reserved.
//

#import "LFViewController.h"
#import "LFLiveKit.h"

typedef enum {
    foront_Camera,
    back_Camera
}Camera_Type;

#define WS(weakSelf)  __weak __typeof(&*self)weakSelf = self;
#define KeyWindow  [UIApplication sharedApplication].keyWindow
#define ScreenW  [UIScreen mainScreen].bounds.size.width
#define ScreenH  [UIScreen mainScreen].bounds.size.height

#define K_iPhoneXStyle ((ScreenW == 375.f && ScreenH == 812.f ? YES : NO) || (ScreenW == 414.f && ScreenH == 896.f ? YES : NO))
#define KTop (K_iPhoneXStyle ? 24.f : 0.f)

@interface LFViewController ()<LFLiveSessionDelegate>
@property (nonatomic, strong) UILabel *stateLabel;
@property (nonatomic, strong) LFLiveSession *session;
@property (nonatomic ,strong) LFLiveStreamInfo *streamInfo;
@property (nonatomic ,assign) Camera_Type cameraType;

@end

@implementation LFViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor blackColor];
    //默认前置摄像头
    self.cameraType = foront_Camera;
    //隐藏状态栏
    [UIApplication sharedApplication].statusBarHidden = YES;
    
    [self.session setRunning:YES];
    
    [self initUI];
}

#pragma mark - UI

- (void)initUI {
    UIButton *backBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 44, 44)];
    backBtn.imageEdgeInsets = UIEdgeInsetsMake(5, 5, 5, 5);
    [backBtn setImage:[UIImage imageNamed:@"back"] forState:UIControlStateNormal];
    [backBtn addTarget:self action:@selector(closeBtnClick) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:backBtn];
    
    UILabel *stateLabel = [[UILabel alloc] initWithFrame:CGRectMake((ScreenW-80)/2, 0, 80, 44)];
    stateLabel.font = [UIFont systemFontOfSize:17];
    stateLabel.textColor = [UIColor whiteColor];
    stateLabel.text = @"未连接";
    stateLabel.textAlignment = NSTextAlignmentCenter;
    [self.view addSubview:stateLabel];
    self.stateLabel = stateLabel;
    
    UIButton *switchBtn = [[UIButton alloc] initWithFrame:CGRectMake(ScreenW-50, 0, 50, 44)];
    switchBtn.imageEdgeInsets = UIEdgeInsetsMake(5, 5, 5, 5);
    [switchBtn setImage:[UIImage imageNamed:@"switch_camera"] forState:UIControlStateNormal];
    [switchBtn addTarget:self action:@selector(switchCameraBtnClick:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:switchBtn];
    
    UIButton *startLiveBtn = [[UIButton alloc] initWithFrame:CGRectMake((ScreenW-80)/2, ScreenH-200, 80, 80)];
    [startLiveBtn setImage:[UIImage imageNamed:@"start_live"] forState:UIControlStateNormal];
    [startLiveBtn setImage:[UIImage imageNamed:@"pause_live"] forState:UIControlStateSelected];
    [startLiveBtn addTarget:self action:@selector(startLive:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:startLiveBtn];
}

#pragma mark - 开始推流

- (void)startLive:(UIButton *)btn {
    btn.selected = !btn.selected;
    if (btn.selected) {
        [self.session startLive:self.streamInfo];
    } else {
        [self.session stopLive];
    }
}

#pragma mark - 关闭

- (void)closeBtnClick {
    [self.session stopLive];
    self.session.beautyFace = NO;
    [self.session setRunning:NO];
    self.session.delegate = nil;
    self.session.preView = nil;

    [UIApplication sharedApplication].statusBarHidden = NO;
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - 切换摄像头

- (void)switchCameraBtnClick:(UIButton *)sender {
    AVCaptureDevicePosition devicePositon = self.session.captureDevicePosition;
    
    self.session.captureDevicePosition = (devicePositon == AVCaptureDevicePositionBack) ? AVCaptureDevicePositionFront : AVCaptureDevicePositionBack;
    
    self.cameraType = (devicePositon == AVCaptureDevicePositionFront) ? back_Camera : foront_Camera;
}

#pragma mark - 懒加载

- (LFLiveSession *)session {
    if (!_session) {
        _session = [[LFLiveSession alloc] initWithAudioConfiguration:[LFLiveAudioConfiguration defaultConfiguration] videoConfiguration:[LFLiveVideoConfiguration defaultConfiguration]];
        
        if (self.cameraType == back_Camera) {
            _session.captureDevicePosition = AVCaptureDevicePositionBack;
        } else {
            _session.captureDevicePosition = AVCaptureDevicePositionFront;
        }
        
        _session.adaptiveBitrate = YES;
        _session.beautyFace = NO;
       
        _session.delegate = self;
        _session.showDebugInfo = NO;
        _session.preView = self.view;
    }
    return _session;
}

- (LFLiveStreamInfo *)streamInfo {
    if (!_streamInfo) {
        _streamInfo = [LFLiveStreamInfo new];
        _streamInfo.url = _liveUrl;
    }
    return _streamInfo;
}

#pragma mark - LFLiveSessionDelegate

- (void)liveSession:(nullable LFLiveSession *)session liveStateDidChange:(LFLiveState)state {
    switch (state) {
        case LFLiveReady:
            _stateLabel.text = @"未连接";
            break;
        case LFLivePending:
            _stateLabel.text = @"连接中...";
            break;
        case LFLiveStart:
            _stateLabel.text = @"已连接";
            break;
        case LFLiveError:
            _stateLabel.text = @"连接错误";
            break;
        case LFLiveStop:
            _stateLabel.text = @"未连接";
            break;
        default:
            break;
    }
}

@end
