/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.examplevoice.administrator.floatingviewlibrary.floating_permission;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;


import com.examplevoice.administrator.floatingviewlibrary.floating_permission.rom.HuaweiUtils;
import com.examplevoice.administrator.floatingviewlibrary.floating_permission.rom.MeizuUtils;
import com.examplevoice.administrator.floatingviewlibrary.floating_permission.rom.MiuiUtils;
import com.examplevoice.administrator.floatingviewlibrary.floating_permission.rom.OppoUtils;
import com.examplevoice.administrator.floatingviewlibrary.floating_permission.rom.QikuUtils;
import com.examplevoice.administrator.floatingviewlibrary.floating_permission.rom.RomUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * @author zhaozp
 * @since 2016-10-17
 */

public class FloatPermissionUtil {


    public static boolean checkPermission(Context context) {
        //6.0 版本之后由于 google 增加了对悬浮窗权限的管理，所以方式就统一了
        if (Build.VERSION.SDK_INT < 19){
            return  true;
        }else if (Build.VERSION.SDK_INT >=19 && Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()
                    || RomUtils.checkIsMeizuRom()
                    || RomUtils.checkIsHuaweiRom()
                    || RomUtils.checkIs360Rom()
                    || RomUtils.checkIsOppoRom()) {// 特殊机型
                return opPermissionCheck(context, 24);
            } else {// 其他机型
               return true;
            }

        }else if (Build.VERSION.SDK_INT >= 23){
            return  highVersionPermissionCheck(context);
        }

        return true;
    }

    public static void requestPermission(Context context) {
        if (Build.VERSION.SDK_INT < 23) {
//            if (RomUtils.checkIsMiuiRom()) {
//                miuiROMPermissionApply(context);
//            } else if (RomUtils.checkIsMeizuRom()) {
//                meizuROMPermissionApply(context);
//            } else if (RomUtils.checkIsHuaweiRom()) {
//                huaweiROMPermissionApply(context);
//            } else if (RomUtils.checkIs360Rom()) {
//                ROM360PermissionApply(context);
//            } else if (RomUtils.checkIsOppoRom()) {
//                oppoROMPermissionApply(context);
//            }
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
        } else {
            highVersionPermissionRequest(context);
        }
    }



    /**
     * [19-23]之间版本通过[AppOpsManager]的权限判断
     *
     * @return [ true, 有权限 ][ false, 无权限 ]
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean opPermissionCheck(Context context, int op) {
        try {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
            return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Android 6.0 版本及之后的权限判断
     *
     * @return [ true, 有权限 ][ false, 无权限 ]
     */
    private static boolean highVersionPermissionCheck(Context context) {
        if (RomUtils.checkIsMeizuRom()) {// 魅族6.0的系统单独适配
            return opPermissionCheck(context, 24);
        }

        return Build.VERSION.SDK_INT>=23 && Settings.canDrawOverlays(context);

    }






    private static void ROM360PermissionApply(final Context context) {
        QikuUtils.applyPermission(context);
    }

    private static void huaweiROMPermissionApply(final Context context) {
        HuaweiUtils.applyPermission(context);
    }

    private static void meizuROMPermissionApply(final Context context) {
        MeizuUtils.applyPermission(context);
    }

    private static void miuiROMPermissionApply(final Context context) {
        MiuiUtils.applyMiuiPermission(context);
    }

    private static void oppoROMPermissionApply(final Context context) {
        OppoUtils.applyOppoPermission(context);
    }

    /**
     * 通用 rom 权限申请
     */
    private static void highVersionPermissionRequest(final Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            context.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName())));
        }
    }

    private static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

        Intent intent = new Intent(field.get(null).toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }


}
