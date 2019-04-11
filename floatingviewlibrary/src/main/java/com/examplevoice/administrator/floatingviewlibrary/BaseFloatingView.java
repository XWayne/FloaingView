package com.examplevoice.administrator.floatingviewlibrary;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by Administrator on 2019/4/10.
 */

/**
 * 负责滑动封装
 */
public abstract class BaseFloatingView extends FrameLayout implements GestureDetector.OnGestureListener{

    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;
    private GestureDetector mGestureDetector;
    protected Context mContext;
    private float mLastX;
    private float mLastY;

    public BaseFloatingView(Context context){
        super(context);
        this.mContext = context.getApplicationContext();
        mGestureDetector = new GestureDetector(context,this);
    }
    public BaseFloatingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BaseFloatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context.getApplicationContext();
        mGestureDetector = new GestureDetector(context,this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
//            Log.d("John","ACTION_OUTSIDE:"+System.currentTimeMillis());
//        }
        return mGestureDetector.onTouchEvent(event); //默认不消费事件
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mLastX = e.getRawX();
        mLastY = e.getRawY();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float nowX = e2.getRawX();
        float nowY = e2.getRawY();

        float deltaX = nowX - mLastX;
        float deltaY = nowY - mLastY;
        if (mWindowManager!=null && mLayoutParams!=null){
            mLayoutParams.x += deltaX;
            mLayoutParams.y += deltaY;

            mWindowManager.updateViewLayout(this,mLayoutParams);
        }

        mLastX = nowX;
        mLastY = nowY;
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        onSingleClick();
        return false;
    }
    @Override
    public void onShowPress(MotionEvent e) {
    }



    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    protected void showView(){
        if (permissionGranted()){
            if (mWindowManager ==null){
                // 获取WindowManager服务
                mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
            }
            if (mLayoutParams == null){
                // 设置LayoutParam
                mLayoutParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }

                mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                mLayoutParams.format = PixelFormat.TRANSLUCENT;
                mLayoutParams.gravity= Gravity.TOP|Gravity.END;
            }

            // 将悬浮窗控件添加到WindowManager
            this.setVisibility(VISIBLE);
            mWindowManager.addView(this, mLayoutParams);
        }

    }

    protected void hideView(){
        if (mWindowManager!=null){
            mWindowManager.removeViewImmediate(this);
        }
    }

    //检查是否具有悬浮窗权限
    protected boolean permissionGranted(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(mContext);
        }

        return true;
    }

    protected abstract void onSingleClick();

    protected void onLongClick(){} //空实现



}
