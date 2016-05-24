package com.example.tbag.weathercheck.util.helper;

import android.text.TextUtils;

import com.example.tbag.weathercheck.data.HistoryData;
import com.example.tbag.weathercheck.util.config.SaveMessageUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


/**
 * Created by suanmiao on 15/3/11.
 */
public class CacheHelper {
  private CacheHelper() {}

  private static Gson mGson = new Gson();

  public static ArrayList<HistoryData> getSearchCache() {
    String cacheString = SaveMessageUtil.getHistoryCache();
    if (!TextUtils.isEmpty(cacheString)) {
      return mGson.fromJson(cacheString, new TypeToken<ArrayList<HistoryData>>() {}.getType());
    } else {
      return new ArrayList<>();
    }
  }

  public static void saveSearchCache(ArrayList<HistoryData> holder) {
    String cacheString = mGson.toJson(holder);
    SaveMessageUtil.setHistoryCache(cacheString);
  }
}
