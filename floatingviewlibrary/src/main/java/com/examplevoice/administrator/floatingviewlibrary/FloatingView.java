package com.examplevoice.administrator.floatingviewlibrary;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import static android.content.Context.WINDOW_SERVICE;

/*悬浮窗 + popupWindow*/
public class FloatingView extends BaseFloatingView implements View.OnClickListener{


    private boolean mPopupViewShowing = false;
    private View mPopupView;
    private OnClickListener mPopupItemListener;

    public FloatingView(Context context){
        super(context);
    }

    public FloatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingView(Context context,@LayoutRes int id){
        super(context);
        View.inflate(context,id,this);
    }

    public void show(OnClickListener clickListener){
        checkPopupView();
        mPopupItemListener = clickListener;
        setItemClickListener();
        showView();
    }

    public void hide(){
        hidePopupWindow();
        hideView();
        mPopupItemListener = null;
        clearItemListener();//清楚监听器，防止内存泄露;
    }

    public void setPopupView(View view){
        mPopupView = view;
    }


    /**
     * popupItem 的ClickHandler
     */
    @Override
    public void onClick(View v) {
        if (mPopupViewShowing){
            hidePopupWindow();
        }
        if (mPopupItemListener != null){
            mPopupItemListener.onClick(v);
        }
    }



    @Override
    protected void onSingleClick() {
        if (mPopupViewShowing){
            hidePopupWindow();
        }else {
            showPopupWindow();
        }
    }


    private OnTouchListener mPopupOutSideListener=new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
                if (mPopupViewShowing){
                    hidePopupWindow();
//                    mTapOutSideTime = System.currentTimeMillis();
                }

            }

            return false;
        }
    };
    private WindowManager.LayoutParams mPopupParams ;

    private void showPopupWindow(){
        if (mPopupView==null || mPopupViewShowing){
            return;
        }
        if (permissionGranted()){
            if (mWindowManager ==null){
                // 获取WindowManager服务
                mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
            }
            if (mPopupParams == null){
                // 设置LayoutParam
                mPopupParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mPopupParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    mPopupParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }

                mPopupParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mPopupParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mPopupParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                mPopupParams.format = PixelFormat.TRANSLUCENT;
                mPopupParams.gravity=Gravity.CENTER;
            }

            mPopupView.setOnTouchListener(mPopupOutSideListener);

            // 将悬浮窗控件添加到WindowManager
            mWindowManager.addView(mPopupView, mPopupParams);

            mPopupViewShowing = true;
            this.setVisibility(INVISIBLE);
        }
    }

    private void hidePopupWindow(){
        if (mPopupView!=null && mPopupViewShowing){
            mPopupView.setOnTouchListener(null);
            mWindowManager.removeViewImmediate(mPopupView);

            mPopupViewShowing = false;
            this.setVisibility(VISIBLE);
        }
    }

    /**
     * 若没有设置popupView，则用默认布局初始化
     */
    private void checkPopupView(){
        if (mPopupView == null){
            throw new NullPointerException("请先调用 setPopupView 初始化popupView");
//            mPopupView = LayoutInflater.from(mContext).inflate(R.layout.view_content_float,null);
        }
    }


    private void setItemClickListener(){
        if (mPopupView!=null){
            if (mPopupView instanceof ViewGroup){
                ViewGroup group = (ViewGroup)mPopupView;
                for (int i =0;i<group.getChildCount();i++){
                    group.getChildAt(i).setOnClickListener(this);
                }
            }else {
                mPopupView.setOnClickListener(this);
            }
        }
    }


    /**
     * 清除监听器，防止内存泄露
     */
    private void  clearItemListener(){
        if (mPopupView!=null){
            if (mPopupView instanceof ViewGroup){
                ViewGroup group = (ViewGroup)mPopupView;
                for (int i =0;i<group.getChildCount();i++){
                    group.getChildAt(i).setOnClickListener(null);
                }
            }else {
                mPopupView.setOnClickListener(null);
            }
        }
    }
}
