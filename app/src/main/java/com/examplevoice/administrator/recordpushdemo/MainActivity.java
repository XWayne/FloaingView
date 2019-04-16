package com.examplevoice.administrator.recordpushdemo;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.examplevoice.administrator.floatingviewlibrary.FloatingView;
import com.tencent.rtmp.ITXLivePushListener;
import com.tencent.rtmp.TXLiveBase;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;

public class MainActivity extends AppCompatActivity implements ITXLivePushListener,View.OnClickListener{

    TXLivePusher mPusher ;
    TXLivePushConfig mConfig;
    String mPushUrl= "rtmp://46310.livepush.myqcloud.com/live/444444?txSecret=de2a1d318fea502d3a70f5545cece2b9&txTime=5CAF647F";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConfig = new TXLivePushConfig();
        mConfig.enablePureAudioPush(false);
        mConfig.enableNearestIP(true);
        Bitmap bitmap = decodeResource(getResources(),R.mipmap.recording_background_private_horizontal);
        mConfig.setPauseImg(bitmap);
        mPusher = new TXLivePusher(MainActivity.this.getApplicationContext());
        mConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
        mPusher.setRenderRotation(90);
        mPusher.setConfig(mConfig);
        onHDClick(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPusher.stopScreenCapture();
        mPusher.setPushListener(null);
        mPusher.stopPusher();
    }

    //标清
    public void onSDClick(View v){
        mPusher.setVideoQuality( TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION, false, false);
        mConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_640_360);
//        mConfig.setVideoBitrate(800);
        mPusher.setConfig(mConfig);
    }

    //高清
    public void onHDClick(View v){
        mPusher.setVideoQuality( TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
        mConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_960_540);
        mConfig.setVideoBitrate(1200);
        mPusher.setConfig(mConfig);
    }

    //超清
    public void onFHDClick(View v){
        mPusher.setVideoQuality( TXLiveConstants.VIDEO_QUALITY_SUPER_DEFINITION, false, false);
        mConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_1280_720);
        mConfig.setVideoBitrate(2000);
        mPusher.setConfig(mConfig);
    }

    //开始推流
    public void onStartClick(View v){
        mPusher.startPusher(mPushUrl);
        mPusher.startScreenCapture();
    }

    //静音
    public void onMuteClick(View v){
        mPusher.setMute(true);
    }

    //暂停、继续
    private boolean mPausing = false;
    public void onPauseClick(View v){
        if (mPausing){
            mPusher.resumePusher();
            ((TextView)v).setText( String.valueOf("PAUSE") );
        }else {
            mPusher.pausePusher();
            ((TextView)v).setText( String.valueOf("RESUME") );
        }
        mPausing = !mPausing;
    }

    //停止推流
    public void onStopClick(View v){
        mPusher.stopScreenCapture();
        mPusher.setPushListener(null);
        mPusher.stopPusher();
        if (mConfig!=null)mConfig.setPauseImg(null);
    }

    private FloatingView mFloatingView;
    private boolean show = false;
    public void floatingCheck(View v){
        if (mFloatingView == null){
            mFloatingView = new FloatingView(getApplicationContext(),R.layout.view_floating);
            mFloatingView.setPopupView(LayoutInflater.from(this).inflate(R.layout.view_content_float,null));
        }
        if (!checkPermission()){
            Toast.makeText(this,"未授予权限",Toast.LENGTH_SHORT).show();
            return;
        }
        Button btn = (Button)v;

        if (show){
            mFloatingView.hide();
            btn.setText("show");
        }else {
            mFloatingView.show(this);
            btn.setText("hide");
        }
        show = !show;
    }

    //检查是否具有悬浮窗权限
    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(MainActivity.this)){
                return true;
            }else {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName())));
                return false;
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPushEvent(int i, Bundle bundle) {
        Log.i("John","onPushEvent:"+i);
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }
}

