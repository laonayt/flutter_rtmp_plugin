package com.flutter_rtmp_plugin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.laifeng.sopcastsdk.configuration.AudioConfiguration;
import com.laifeng.sopcastsdk.configuration.CameraConfiguration;
import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.stream.packer.rtmp.RtmpPacker;
import com.laifeng.sopcastsdk.stream.sender.rtmp.RtmpSender;
import com.laifeng.sopcastsdk.ui.CameraLivingView;

public class LivingActivity extends Activity {
    private CameraLivingView mLFLiveView;
    private RtmpSender mRtmpSender;
    private ImageButton closeBtn;
    private ImageButton mRecordBtn;
    private ImageButton switchBtn;
    private TextView statusView;
    private boolean isRecording;
    private VideoConfiguration mVideoConfiguration;
    private int mCurrentBps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_living);

        initViews();
        initLiveView();
    }

    private void initViews() {
        mLFLiveView = (CameraLivingView) findViewById(R.id.liveView);
        closeBtn = (ImageButton) findViewById(R.id.closeBtn);
        mRecordBtn = (ImageButton) findViewById(R.id.btnRecord);
        statusView = (TextView) findViewById(R.id.statusView);
        switchBtn = (ImageButton) findViewById(R.id.camera_switch_button);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLFLiveView.stop();
                mLFLiveView.release();
                finish();
            }
        });
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLFLiveView.switchCamera();
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = !isRecording;
                if(isRecording) {
                    mRtmpSender.connect();
                    mRecordBtn.setBackgroundResource(R.mipmap.pause_live);
                    mLFLiveView.start();
                } else {
                    statusView.setText("未连接");
                    mRecordBtn.setBackgroundResource(R.mipmap.start_live);
                    mLFLiveView.stop();
                }
            }
        });
    }

    private void initLiveView() {
        mLFLiveView.init();

        //摄像头
        CameraConfiguration.Builder cameraBuilder = new CameraConfiguration.Builder();
        cameraBuilder.setOrientation(CameraConfiguration.Orientation.PORTRAIT).setFacing(CameraConfiguration.Facing.BACK);
        CameraConfiguration cameraConfiguration = cameraBuilder.build();
        mLFLiveView.setCameraConfiguration(cameraConfiguration);

        //视频质量
        VideoConfiguration.Builder videoBuilder = new VideoConfiguration.Builder();
        videoBuilder.setSize(360, 640);
        mVideoConfiguration = videoBuilder.build();
        mLFLiveView.setVideoConfiguration(mVideoConfiguration);

        //初始化打包器
        RtmpPacker packer = new RtmpPacker();
        packer.initAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mLFLiveView.setPacker(packer);

        //设置发送器
        String url = getIntent().getStringExtra("url");
        mRtmpSender = new RtmpSender();
        mRtmpSender.setAddress(url);
//        mRtmpSender.setVideoParams(360, 640);
//        mRtmpSender.setAudioParams(AudioConfiguration.DEFAULT_FREQUENCY, 16, false);
        mRtmpSender.setSenderListener(mSenderListener);
        mLFLiveView.setSender(mRtmpSender);

        mLFLiveView.setLivingStartListener(new CameraLivingView.LivingStartListener() {
            @Override
            public void startError(int error) {
                //直播失败
//                Toast.makeText(LivingActivity.this, "开启失败", Toast.LENGTH_SHORT).show();
                mLFLiveView.stop();
            }

            @Override
            public void startSuccess() {
                //直播成功
//                Toast.makeText(LivingActivity.this, "开启成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private RtmpSender.OnSenderListener mSenderListener = new RtmpSender.OnSenderListener() {
        @Override
        public void onConnecting() {
            statusView.setText("连接中...");
        }

        @Override
        public void onConnected() {
            statusView.setText("已连接");
//            mLFLiveView.start();
            mCurrentBps = mVideoConfiguration.maxBps;
        }

        @Override
        public void onDisConnected() {
            statusView.setText("未连接");
            mRecordBtn.setBackgroundResource(R.mipmap.start_live);
            mLFLiveView.stop();
            isRecording = false;
        }

        @Override
        public void onPublishFail() {
            statusView.setText("推流失败");
            mRecordBtn.setBackgroundResource(R.mipmap.start_live);
            isRecording = false;
        }

        @Override
        public void onNetGood() {
//            if (mCurrentBps + 50 <= mVideoConfiguration.maxBps){
//                SopCastLog.d(TAG, "BPS_CHANGE good up 50");
//                int bps = mCurrentBps + 50;
//                if(mLFLiveView != null) {
//                    boolean result = mLFLiveView.setVideoBps(bps);
//                    if(result) {
//                        mCurrentBps = bps;
//                    }
//                }
//            } else {
//                SopCastLog.d(TAG, "BPS_CHANGE good good good");
//            }
//            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
        }

        @Override
        public void onNetBad() {
//            if (mCurrentBps - 100 >= mVideoConfiguration.minBps){
//                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
//                int bps = mCurrentBps - 100;
//                if(mLFLiveView != null) {
//                    boolean result = mLFLiveView.setVideoBps(bps);
//                    if(result) {
//                        mCurrentBps = bps;
//                    }
//                }
//            } else {
//                SopCastLog.d(TAG, "BPS_CHANGE bad down 100");
//            }
//            SopCastLog.d(TAG, "Current Bps: " + mCurrentBps);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mLFLiveView.pause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLFLiveView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLFLiveView.stop();
        mLFLiveView.release();
    }
}
