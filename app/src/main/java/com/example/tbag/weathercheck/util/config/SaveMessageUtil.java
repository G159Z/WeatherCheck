package com.example.tbag.weathercheck.util.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.tbag.weathercheck.component.WeatherCheckApplication;

/**
 * Created by tbag on 16/5/18.
 */
public class SaveMessageUtil {
  private static final int SHAREDPREF_OPEN_MODE = Context.MODE_MULTI_PROCESS;

  private static final String SHAREDPREF_APP_STATE = "app_state";
  
  private static final String KEY_HISTORY = "history";
  
  public static SharedPreferences getAppStateSharedpref() {
    return WeatherCheckApplication.getContext().getSharedPreferences(SHAREDPREF_APP_STATE,
        SHAREDPREF_OPEN_MODE);
  }

  public static String getHistoryCache() {
    return getAppStateSharedpref().getString(KEY_HISTORY, "");
  }

  public static boolean setHistoryCache(String cache) {
    SharedPreferences.Editor editor = getAppStateSharedpref().edit();
    editor.putString(KEY_HISTORY, cache);
    return editor.commit();
  }
}
