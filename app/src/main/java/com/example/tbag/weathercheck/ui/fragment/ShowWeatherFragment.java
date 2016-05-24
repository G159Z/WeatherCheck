package com.example.tbag.weathercheck.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tbag.weathercheck.R;
import com.example.tbag.weathercheck.bluetooth.BTService;
import com.example.tbag.weathercheck.data.HistoryData;
import com.example.tbag.weathercheck.ui.activity.BasicActivityDecorated;
import com.example.tbag.weathercheck.util.helper.CacheHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by tbag on 16/5/17.
 */
public class ShowWeatherFragment extends Fragment {

  @InjectView(R.id.text_toolbar_title)
  TextView textToolbarTitle;
  @InjectView(R.id.icon_toolbar_more)
  TextView iconToolbarMore;
  @InjectView(R.id.text_temperature)
  TextView textTemperature;
  @InjectView(R.id.text_humidity)
  TextView textHumidity;
  @InjectView(R.id.text_pressure)
  TextView textPressure;
  @InjectView(R.id.text_height)
  TextView textHeight;
  @InjectView(R.id.text_rain)
  TextView textRain;
  @InjectView(R.id.text_tip)
  TextView textTip;

  private Context mContext;
  private boolean isGetMessage = false ;
  private String message, showMessage, humidity;// H29T34E
  private int temperature, rain, pressure;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
      , @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View contentView = inflater.inflate(R.layout.fragment_show_weather, container, false);
    ButterKnife.inject(this, contentView);
    initBroadCast();
    return contentView;
  }

  private void initBroadCast() {
    BroadcastReceiver bcr = new BroadcastReceiver() {
      @Override
      public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        if (arg1.getAction().equals(BTService.RecvInBroadcast)) {
          message = arg1.getStringExtra("cmd");
          if (!isGetMessage) {
            judgeMessage();
          } else if (!message.equals(showMessage)) {
            judgeMessage();
          }
        }
      }
    };
    IntentFilter filter = new IntentFilter();
    filter.addAction(BTService.RecvInBroadcast);
    mContext.registerReceiver(bcr, filter);
  }

  private void judgeMessage() {
    if (message.startsWith("H") && message.endsWith("E")) {
      showMessage = message;
      showMessage(message);
      if (!isGetMessage) {
        saveMessage(message);
      }
      isGetMessage = true;
    }
  }

  private void saveMessage(String message) {
    boolean isSaved = false;
    CalendarDay calendarDay = CalendarDay.from(Calendar.getInstance());
    ArrayList<HistoryData> list = CacheHelper.getSearchCache();
    if (list != null) {
      for (HistoryData data : list) {
        if (data.calendarDay.equals(calendarDay)) {
          isSaved = true;
          break;
        }
      }
    }
    if (!isSaved) {
      list.add(new HistoryData(calendarDay, message));
      CacheHelper.saveSearchCache(list);
    }
  }

  private void showMessage(String message) {
    humidity = message.substring(1, message.indexOf("R"));
    rain = Integer.valueOf(message.substring(message.indexOf("R") + 1, message.indexOf("P")));
    pressure = Integer.valueOf(message.substring(message.indexOf("P") + 1, message.indexOf("T")));
    temperature = Integer.valueOf(message.substring(message.indexOf("T") + 1, message.indexOf("E")));
    double f = 32 + temperature * 1.8;
    DecimalFormat df = new DecimalFormat("######0.0");
    String s = df.format(f);
    textTemperature.setText("摄氏温度" + temperature + "C  华式温度" + s + "F");
    textHumidity.setText("相对湿度为" + humidity + "%");
    textPressure.setText("大气压为" + pressure + "百帕");
    textHeight.setText("海拔高度为" + (pressure - 1013) * 9 + "米");
    showTip(temperature, rain);
  }

  private void showTip(int temperature, int rain) {
    String tip = "tip: ";
    if (temperature > 28) {
      tip += "天气炎热，适宜着夏季服装";
    } else if (temperature > 23) {
      tip += "天气较热，适宜着夏季服装，年老体弱者宜着长袖衬衫和单裤。";
    } else if (temperature > 18) {
      tip += "天气稍热，适宜着春秋过渡装。体弱者请适当增减衣服。";
    } else if (temperature > 15) {
      tip += "温度适中，建议着薄型春秋过渡装。年老体弱者宜着套装、夹克衫等。";
    } else if (temperature > 10) {
      tip += "天气较凉爽，建议着厚型春秋服装。年老体弱者宜着夹衣或风衣加羊毛衫。";
    } else if (temperature > 5) {
      tip += "气温较低，建议着厚外套加毛衣等春秋服装。年老体弱者宜着大衣、呢外套加羊毛衫。";
    } else if (temperature > 0) {
      tip += "温度很低，建议着棉衣、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣或冬大衣。";
    } else {
      tip += "温度极低，建议着厚羽绒服、毛皮大衣加厚毛衣等隆冬服装。年老体弱者尤其要注意保暖防冻。";
    }
    if (rain < 600) {
      textRain.setText("大雨");
      tip += "有大雨请注意带好雨伞，关好门窗。";
    } else if (rain < 950) {
      textRain.setText("小雨");
      tip += "有小雨请注意带好雨伞，防止感冒";
    } else {
      textRain.setText("无雨");
      tip += "天气晴朗，请欣赏美好天气。";
    }
    textTip.setText(tip);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.reset(this);
  }

  @OnClick(R.id.icon_toolbar_more)
  public void onClick() {
    startActivity(new Intent(mContext,BasicActivityDecorated.class));
  }
}
