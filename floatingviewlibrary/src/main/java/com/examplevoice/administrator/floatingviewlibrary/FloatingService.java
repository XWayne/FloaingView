//package com.examplevoice.administrator.floatingviewlibrary;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
//import com.tencent.rtmp.ITXLivePushListener;
//import com.tencent.rtmp.TXLiveConstants;
//import com.tencent.rtmp.TXLivePushConfig;
//import com.tencent.rtmp.TXLivePusher;
//import com.whaty.collegeN.BaseConfig;
//import com.whaty.collegeN.MyApplication;
//import com.whaty.collegeN.activity.RollResultActivity;
//import com.whaty.collegeN.base.ScreenStatus;
//import com.whaty.collegeN.beans.Member;
//import com.whaty.collegeN.beans.RecordBean;
//import com.whaty.collegeN.beans.UserInfo;
//import com.whaty.collegeN.http.ApiService;
//import com.whaty.collegeN.utils.HttpAgent;
//import com.whaty.collegeN.utils.MyStringRequest;
//import com.whaty.collegeN.websocket.bean.user;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.schedulers.Schedulers;
//
//public class FloatingService extends Service {
//
//    private FloatingView mFloatingView;
//    private MyPopupViewHolder mHolder;
//    private boolean mScreening = false;
//    private boolean mNaming = false;
//
//    private String mRoomId="";
//
//    TXLivePusher mPusher ;
//    TXLivePushConfig mConfig;
//    String mPushUrl= "rtmp://21443.livepush.myqcloud.com/live/21443_d23acc039c58803d45e5a320de18c653?txSecret=f847d402174551139bd3a99d86ae3bcb&txTime=5CB53113";
//    String mDefaultUrl ="http://21443.liveplay.myqcloud.com/live/21443_d23acc039c58803d45e5a320de18c653.flv";
//    String mHDUrl ="http://21443.liveplay.myqcloud.com/live/21443_d23acc039c58803d45e5a320de18c653.flv";
//    String mFHDUrl ="http://21443.liveplay.myqcloud.com/live/21443_d23acc039c58803d45e5a320de18c653.flv";
//
//
//
//    public FloatingService() {
//    }
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        requestUrl();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent,  int flags, int startId) {
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        hideFloatingView();
//        stopPush();
//        mFloatingView = null;
//        mHolder = null;
//        mConfig = null;
//        mPusher = null;
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
//    }
//
//    private void showFloatingView(){
//        if (mFloatingView == null){
//            mFloatingView = new FloatingView(getApplicationContext(), R.layout.view_floating);
//            mHolder = new MyPopupViewHolder(getApplicationContext());
//        }
//        if (!mFloatingView.isShowing()){
//            mFloatingView.setPopupView(mHolder);
//            mFloatingView.show(mItemClickListener);
//        }
//    }
//
//    private boolean isShowing(){
//        return mFloatingView!=null && mFloatingView.isShowing();
//    }
//
//    private void hideFloatingView(){
//        if (mFloatingView != null){
//            if (mFloatingView.isShowing()){
//                mFloatingView.hide();
//                mFloatingView.setPopupView(null);
//            }
//        }
//    }
//
//    private View.OnClickListener mItemClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.img_screen:
//                case R.id.tv_screen:
//                    if (!mScreening){
//                        startPush();
//                    }else {
//                        stopPush();
//                        mHolder.screening(mScreening);
//                    }
//
//                    break;
//                case R.id.img_name:
//                case R.id.tv_name:
//                    mNaming = !mNaming;
////                    if (mCallbackListener!=null){
////                        mCallbackListener.onClick(onClickListener.TYPE_NAMING);
////                    }
//                    requestData();
//                    break;
//            }
//        }
//    };
//
//
//    @Override
//    public IBinder onBind(Intent intent) {
//       return new MyBinder();
//    }
//
//    public class MyBinder extends Binder{
//        public void show(String roomId){
//            FloatingService.this.showFloatingView();
//            FloatingService.this.mRoomId = roomId;
//        }
//        public boolean isShowing(){
//            return FloatingService.this.isShowing();
//        }
//
//        public void hide(){
//            FloatingService.this.hideFloatingView();
//        }
//
//        public void stopPush(){
//            if (mScreening){
//                FloatingService.this.stopPush();
//            }
//
//        }
//
//    }
//
//    static class MyPopupViewHolder extends FrameLayout{
//
//        private ImageView mScreenImg;
////        private ImageView mNameImg;
//        private TextView  mScreenTv;
////        private TextView  mNameTv;
//
//        MyPopupViewHolder(Context context){
//            super(context);
//            LayoutInflater.from(context).inflate(R.layout.view_content_float,this);
//            mScreenImg = findViewById(R.id.img_screen);
////            mNameImg = findViewById(R.id.img_name);
//            mScreenTv = findViewById(R.id.tv_screen);
////            mNameTv = findViewById(R.id.tv_name);
//        }
//
//        void screening(boolean screening){
//            if (screening){
//                Glide.with(getContext()).load(R.drawable.icon_finish).into(mScreenImg);
//                mScreenTv.setText(String.valueOf("结束投屏"));
//            }else {
//                Glide.with(getContext()).load(R.drawable.icon_control).into(mScreenImg);
//                mScreenTv.setText(String.valueOf("投屏"));
//            }
//        }
//
//    }
//
//
//
//    /*----------录屏推流API----------------------------------------------*/
//
//
//
//    private void initPush(){
//        mConfig = new TXLivePushConfig();
//        mConfig.enablePureAudioPush(false);
//        mConfig.enableNearestIP(true);
////        Bitmap bitmap = decodeResource(getResources(),R.mipmap.recording_background_private_horizontal);
////        mConfig.setPauseImg(bitmap);
//        mPusher = new TXLivePusher(getApplicationContext());
//        mConfig.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_RIGHT);
//        mPusher.setRenderRotation(90);
//        mPusher.setConfig(mConfig);
//        mPusher.setMute(true); //静音
//        setFHD(); //
//    }
//
//
//
//    private void startPush(){
//        if (mPusher == null){
//            initPush();
//        }
//        mPusher.setPushListener(mPushListener);
//        mPusher.startPusher(mPushUrl);
//        mPusher.startScreenCapture();
//        Toast.makeText(getApplicationContext(),"录屏准备中...",Toast.LENGTH_LONG).show();
//    }
//
//    private void beginPush(){
//        mScreening = true;
//        mHolder.screening(true);
//        sendCommand(6);//通知学生开始推流
//    }
//
//    private void stopPush(){
//        if (mPusher == null){
//            mScreening = false;
//            return;
//        }
//        mPusher.setPushListener(null);
//        mPusher.stopScreenCapture();
//        mPusher.stopPusher();
//
//        pushFinish();
////        if (mConfig!=null)mConfig.setPauseImg(null);
//    }
//
//    private void pushFinish(){
//        if (mScreening){
//            mScreening = false;
//            mHolder.screening(false);
//            sendCommand(7);//通知学生停止推流
//        }
//    }
//
////    private void setHD(){
////        mPusher.setVideoQuality( TXLiveConstants.VIDEO_QUALITY_HIGH_DEFINITION, false, false);
////        mConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_960_540);
////        mConfig.setVideoBitrate(1200);
////        mPusher.setConfig(mConfig);
////    }
////
////    private void setSD(){
////        mPusher.setVideoQuality( TXLiveConstants.VIDEO_QUALITY_STANDARD_DEFINITION, false, false);
////        mConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_640_360);
////        mConfig.setVideoBitrate(800);
////        mPusher.setConfig(mConfig);
////    }
//
//    private void setFHD(){
//        mPusher.setVideoQuality( TXLiveConstants.VIDEO_QUALITY_SUPER_DEFINITION, false, false);
//        mConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_1280_720);
//        mConfig.setVideoBitrate(2000);
//        mPusher.setConfig(mConfig);
//    }
//
//
//    private ITXLivePushListener mPushListener = new ITXLivePushListener() {
//        @Override
//        public void onPushEvent(int event, Bundle param) {
//            String msg = param.getString(TXLiveConstants.EVT_DESCRIPTION);
//            String pushEventLog = "receive event: " + event + ", " + msg;
//            Log.d("John", pushEventLog);
//
//            //错误还是要明确的报一下
//            if (event < 0) {
//                Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
//                if(event == TXLiveConstants.PUSH_ERR_OPEN_CAMERA_FAIL || event == TXLiveConstants.PUSH_ERR_OPEN_MIC_FAIL){
//                    stopPush();
//                }
//            }
//
//            if (event == TXLiveConstants.PUSH_EVT_PUSH_BEGIN){
//                beginPush();
//            }else if (event == TXLiveConstants.PUSH_ERR_NET_DISCONNECT || event == TXLiveConstants.PUSH_ERR_INVALID_ADDRESS) {
//                Toast.makeText(getApplicationContext(),"录屏失败，请重试",Toast.LENGTH_LONG).show();
//                stopPush();
//            }
//            else if (event == TXLiveConstants.PUSH_WARNING_HW_ACCELERATION_FAIL) {
//                //硬解码失败
//                Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
//                mConfig.setHardwareAcceleration(TXLiveConstants.ENCODE_VIDEO_SOFTWARE);
//                mPusher.setConfig(mConfig);
//            }
//            else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_UNSURPORT) {
//                stopPush();
//            }
//            else if (event == TXLiveConstants.PUSH_ERR_SCREEN_CAPTURE_START_FAILED) {
//                stopPush();
//            } else if (event == TXLiveConstants.PUSH_EVT_CHANGE_RESOLUTION) {
//                Log.d("John", "change resolution to " + param.getInt(TXLiveConstants.EVT_PARAM2) + ", bitrate to" + param.getInt(TXLiveConstants.EVT_PARAM1));
//            } else if (event == TXLiveConstants.PUSH_EVT_CHANGE_BITRATE) {
//                Log.d("John", "change bitrate to" + param.getInt(TXLiveConstants.EVT_PARAM1));
//            } else if (event == TXLiveConstants.PUSH_WARNING_NET_BUSY) {
//                Toast.makeText(getApplicationContext(),"您当前的网络状况不佳，推荐您离 Wi-Fi 近一点.",Toast.LENGTH_LONG).show();
//            }
//        }
//
//
//        @Override
//        public void onNetStatus(Bundle bundle) {
//
//        }
//    };
//    /*-------------------------------------------------------------------*/
//
//    /*----------------------网络请求请求区域，WebSocket?? TODO 封装？？--------------------------------------*/
//
//    private RequestQueue mRequestQueue;
//
//    /**
//     * 老接口，点名
//     */
//    private void requestData() {
//        String url = BaseConfig.WEBSOCKET_URL + "/websocket/room/member/show";
//        HashMap<String, Object> paraMap = new HashMap<>();
//        paraMap.put("classUuid", mRoomId);
//        url = HttpAgent.getUrl1(url, paraMap);
//
//        StringRequest request = new MyStringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        List<Member> listOnline = new ArrayList<>();
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            JSONArray jsonArray = object.getJSONArray("members");
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                user user = HttpAgent.getGson().fromJson(
//                                        jsonArray.getJSONObject(i).toString(),
//                                        user.class);
//                                Member member = new Member();
//                                member.setJid(user.getUsername());
//                                member.setName(user.getName());
//                                member.setOnline(user.isOnline());
//                                if (user.isOnline())
//                                    listOnline.add(member);
//                            }
//                            if (listOnline.size() > 0) {
//                                requesetMember();
//                            } else {
//                                // Toast.makeText(getApplicationContext(), "无在线学生无法点名", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(getApplicationContext(), RollResultActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        });
//        request.setRetryPolicy(new DefaultRetryPolicy(50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        mRequestQueue.add(request);
//    }
//    private void requesetMember() {
//        String url = BaseConfig.WEBSOCKET_URL + "/websocket/app/poll/random";
//        HashMap<String, Object> paraMap = new HashMap<>();
//        UserInfo user = MyApplication.getUser();
//        if (user != null) {
//            paraMap.put("senderUsername", user.getUniqueId());
//        }
//        paraMap.put("classUuid", mRoomId);
//        url = HttpAgent.getUrl1(url, paraMap);
//        StringRequest request = new MyStringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//            }
//        });
//        request.setRetryPolicy(new DefaultRetryPolicy(50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        mRequestQueue.add(request);
//    }
//
//    private void requestUrl(){
////        HashMap<String, String> params = new HashMap<>();
////        params.put("loginToken", MyApplication.getUser().getLoginToken());
//        Log.i("John", "loginToken:" + MyApplication.getUser().getLoginToken());
//        ApiService.INSTANCE.getwhatyApiService().requestUrl(MyApplication.getUser().getLoginToken())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<RecordBean>() {
//                    @Override
//                    public void call(RecordBean recordBean) {
//                        mPushUrl = recordBean.getAddress().getPushurl();
//                        mDefaultUrl = recordBean.getAddress().getFlvplayurl();
//                        mHDUrl = mDefaultUrl;
//                        mFHDUrl = mDefaultUrl;
//
//                        Log.i("John", "mPushUrl:" + mPushUrl);
//                        Log.i("John", "mDefaultUrl:" + mDefaultUrl);
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                });
//    }
//
//
//    //TODO John 动态获取地址 和 发送webSocket 指令
//    private void sendCommand(final int presenceType) {
//
//        if (MyApplication.getWebSocketClient() != null) {
//            if (!MyApplication.getWebSocketClient().getConnection().isOpen()) {
//                Toast.makeText(getApplicationContext(), "课中互动连接不稳定,正在重新连接", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//
//        String url = BaseConfig.WEBSOCKET_URL + "/websocket/app/status/push";
////		String url = BaseConfig.XMPP_URL + "/app/presence/push";
//        HashMap<String, Object> paraMap = new HashMap<>();
//        paraMap.put("username", MyApplication.getUser().getUniqueId());
//        paraMap.put("classUuid", mRoomId);
////        paraMap.put("defaultUrl","");
////        paraMap.put("HDUrl","");
////        paraMap.put("FHDUrl","");
//        paraMap.put("classStatus", presenceType);
//        paraMap.put("pageId", ScreenStatus.LISTPAGE);
//        paraMap.put("contentId", mDefaultUrl);// 临时默认地址
//        paraMap.put("courseId", mHDUrl);// 临时高清地址
//        paraMap.put("resourceId", mFHDUrl);// 临时超清地址
//        url = HttpAgent.getUrl1(url, paraMap);
//        StringRequest request = new MyStringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    public void onResponse(String response) {
//                        Log.e("sendCommand", "指令发送" + response);
//                    }
//                }, new Response.ErrorListener() {
//
//            public void onErrorResponse(VolleyError arg0) {
//                Toast.makeText(FloatingService.this, "请检查网络是否连接", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        request.setRetryPolicy(new DefaultRetryPolicy(50000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        mRequestQueue.add(request);
//    }
//
//
////    //检查是否具有悬浮窗权限
////    private boolean checkPermission(){
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            if (Settings.canDrawOverlays(getApplicationContext())){
////                return true;
////            }else {
//////                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//////                        Uri.parse("package:" + getPackageName())));
////                return false;
////            }
////        }
////
////        return true;
////    }
//
//}
