package com.example.tbag.weathercheck.component;

import android.app.Application;
import android.content.Context;

/**
 * Created by tbag on 16/5/18.
 */
public class WeatherCheckApplication extends Application {
  public static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
  }

  public static Context getContext(){
    return  context;
  }
  
}
