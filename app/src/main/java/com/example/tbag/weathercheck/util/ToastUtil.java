package com.example.tbag.weathercheck.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by tbag on 16/5/18.
 */
public class ToastUtil {
  public static Toast toast;
  public static void showToast(Context context,String text){
    if(toast != null){
      toast.cancel();
    }
    toast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
    toast.show();
  }
}
