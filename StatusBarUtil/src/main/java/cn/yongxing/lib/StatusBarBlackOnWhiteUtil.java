package cn.yongxing.lib;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author jiangyongxing
 * @date 2019/5/6
 * 描述：设置我们的状态栏为白底黑字的样式
 */

public class StatusBarBlackOnWhiteUtil {

    /**
     * MIUI9
     */
    private static final int MIUI_TYPE_9 = 9;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColorAndFontColor(Activity activity) {
        setStatusBarColorAndFontColor(activity, Color.WHITE, true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColorAndFontColor(Activity activity, int statusBarBackGroundColor) {
        setStatusBarColorAndFontColor(activity, statusBarBackGroundColor == 0 ? Color.WHITE : statusBarBackGroundColor, true);
    }

    /**
     * 移除白底黑字的样式
     *
     * @param activity
     */
    public static void removeStatusBarBlackOnWhite(Activity activity) {
        removeStatusBarBlackOnWhite(activity, 0);
    }

    /**
     * 移除白底黑字的样式
     *
     * @param activity                 需要移除的Activity
     * @param statusBarBackGroundColor 移除以后状态栏显示的颜色
     */
    public static void removeStatusBarBlackOnWhite(Activity activity, int statusBarBackGroundColor) {
        setStatusBarColorAndFontColor(activity,
                statusBarBackGroundColor == 0 ? getColorPrimary(activity) : statusBarBackGroundColor,
                false);
    }

    /**
     * 获取主题状态栏颜色，如果API小于21的话，就将返回黑色
     *
     * @param activity
     * @return
     */
    private static int getColorPrimary(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
            return typedValue.data;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * 是否启用白底黑字状态栏
     *
     * @param activity                     需要改变的Activity
     * @param statusBarBackGroundColor     状态栏的背景颜色
     * @param settingStatusBarBlackOnWhite 是否需要设置成白底黑字的样式
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColorAndFontColor(Activity activity, int statusBarBackGroundColor, boolean settingStatusBarBlackOnWhite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (setMIUIStatusBarLightMode(activity.getWindow(), settingStatusBarBlackOnWhite) && (MIUITypeUtils.getMiuiVersion() < MIUI_TYPE_9)) {
                //MIUI  miui9不需要这样子更改了  直接采用原生的就好了
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //5.0
                    activity.getWindow().setStatusBarColor(statusBarBackGroundColor);
                } else {
                    //4.4
                    setStatusBarColorByKITKAT(activity, statusBarBackGroundColor);
                }
            } else if (setFlymeStatusBarLightMode(activity.getWindow(), settingStatusBarBlackOnWhite)) {
                //Flyme
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //5.0
                    activity.getWindow().setStatusBarColor(statusBarBackGroundColor);
                } else {
                    //4.4
                    setStatusBarColorByKITKAT(activity, statusBarBackGroundColor);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBarColorByCommonSystem(activity, statusBarBackGroundColor, settingStatusBarBlackOnWhite);
            }
        }
    }

    private static boolean setMIUIStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag;
                @SuppressLint("PrivateApi") Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    private static boolean setFlymeStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;

    }

    /**
     * 普通系统的设置方法
     *
     * @param activity
     * @param bgColor
     * @param settingStatusBarBlackOnWhite
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    static void setStatusBarColorByCommonSystem(Activity activity, int bgColor, boolean settingStatusBarBlackOnWhite) {
        Window window = activity.getWindow();
        window.setStatusBarColor(bgColor);
        window.getDecorView().setSystemUiVisibility(settingStatusBarBlackOnWhite ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0);
    }

    /**
     * 系统版本号为4.4以上  5.0以下的时候，设置状态栏为白底黑字
     *
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void setStatusBarColorByKITKAT(Activity activity, int colorBg) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(colorBg);
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
        rootView.setPadding(0, getStatusBarHeight(activity), 0, 0);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

}
