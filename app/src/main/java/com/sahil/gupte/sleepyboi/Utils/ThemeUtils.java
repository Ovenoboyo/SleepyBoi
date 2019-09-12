package com.sahil.gupte.sleepyboi.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toolbar;

import com.sahil.gupte.sleepyboi.R;


public class ThemeUtils {
    public final static int BLUE = 0, ORANGE = 1, YELLOW = 2, GREEN = 3, CYAN = 4, PINK = 5;
    public final static int BLUEDARK = 10, ORANGEDARK = 11, YELLOWDARK = 12, GREENDARK = 13, CYANDARK = 14, PINKDARK = 15;
    private static final String TAG = "ThemeUtils" ;
    private static int cTheme;

    public static void PutDark(boolean dark, Context mContext) {
        SharedPreferences pref = mContext.getSharedPreferences("Theme", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("dark", dark);
        editor.apply();
    }

    public static void changeToTheme(int theme, Context mContext)
    {
        cTheme = theme;
        SharedPreferences pref = mContext.getSharedPreferences("Theme", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("theme", cTheme);
        editor.apply();
    }

    public static void onActivityCreateSetTheme(Activity activity, Context mContext) {

        SharedPreferences pref = mContext.getSharedPreferences("Theme", 0);
        cTheme = pref.getInt("theme", 14);

        switch (cTheme) {

            case BLUE:
                activity.setTheme(R.style.Theme_app_blue);
                break;

            case BLUEDARK:
                activity.setTheme(R.style.Theme_app_Dark_blue);
                break;

            case ORANGE:
                activity.setTheme(R.style.Theme_app_orange);
                break;

            case ORANGEDARK:
                activity.setTheme(R.style.Theme_app_Dark_orange);
                break;

            case YELLOW:
                activity.setTheme(R.style.Theme_app_yellow);
                break;

            case YELLOWDARK:
                activity.setTheme(R.style.Theme_app_Dark_yellow);
                break;

            case GREEN:
                activity.setTheme(R.style.Theme_app_green);
                break;

            case GREENDARK:
                activity.setTheme(R.style.Theme_app_Dark_green);
                break;

            default:
            case CYAN:
                activity.setTheme(R.style.Theme_app_cyan);
                break;

            case CYANDARK:
                activity.setTheme(R.style.Theme_app_Dark_cyan);
                break;

            case PINK:
                activity.setTheme(R.style.Theme_app_pink);
                break;

            case PINKDARK:
                activity.setTheme(R.style.Theme_app_Dark_pink);
                break;
        }
    }
}
