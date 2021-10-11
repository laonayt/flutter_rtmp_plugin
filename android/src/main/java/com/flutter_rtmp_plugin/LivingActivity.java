package com.flutter_rtmp_plugin;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.WatermarkFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;

public class LivingActivity extends AppCompatActivity {
    private StreamLiveCameraView mLiveCameraView;
    private StreamAVOption streamAVOption;
    private ImageButton closeBtn;
    private ImageButton mRecordBtn;
    private ImageButton switchBtn;
    private TextView statusView;
    private boolean isRecording;
    private int mCurrentBps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_living);

        initViews();
        initLiveConfig();
    }

    private void initViews() {
        mLiveCameraView = (StreamLiveCameraView) findViewById(R.id.liveView);
        closeBtn = (ImageButton) findViewById(R.id.closeBtn);
        mRecordBtn = (ImageButton) findViewById(R.id.btnRecord);
        statusView = (TextView) findViewById(R.id.statusView);
        switchBtn = (ImageButton) findViewById(R.id.camera_switch_button);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLiveCameraView.isStreaming()){
                    mLiveCameraView.stopStreaming();
                }
                finish();
            }
        });
        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveCameraView.swapCamera();
            }
        });
        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = !isRecording;
                if(isRecording) {
                    mRecordBtn.setBackgroundResource(R.mipmap.pause_live);

                    if(!mLiveCameraView.isStreaming()){
                        String url = getIntent().getStringExtra("url");
                        mLiveCameraView.startStreaming(url);
                    }
                } else {
                    mRecordBtn.setBackgroundResource(R.mipmap.start_live);

                    if(mLiveCameraView.isStreaming()){
                        mLiveCameraView.stopStreaming();
                    }
                }
            }
        });
    }

    /**
     * 设置推流参数
     */
    public void initLiveConfig() {
        String url = getIntent().getStringExtra("url");
        streamAVOption = new StreamAVOption();
        streamAVOption.streamUrl = url;

        mLiveCameraView.init(this, streamAVOption);
        mLiveCameraView.addStreamStateListener(resConnectionListener);
    }

    RESConnectionListener resConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            //result 0成功  1 失败
            if (result == 0) {
                statusView.setText("已连接");
            }
        }

        @Override
        public void onWriteError(int errno) {
            statusView.setText("推流失败，请尝试重连");
            mRecordBtn.setBackgroundResource(R.mipmap.start_live);
            isRecording = false;
        }

        @Override
        public void onCloseConnectionResult(int result) {
            //result 0成功  1 失败
            statusView.setText("关闭推流连接");
            mRecordBtn.setBackgroundResource(R.mipmap.start_live);
            isRecording = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveCameraView.destroy();
    }

}
