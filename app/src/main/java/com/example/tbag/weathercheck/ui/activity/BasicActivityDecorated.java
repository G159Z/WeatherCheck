package com.example.tbag.weathercheck.ui.activity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tbag.weathercheck.R;
import com.example.tbag.weathercheck.data.HistoryData;
import com.example.tbag.weathercheck.ui.decorators.EventDecorator;
import com.example.tbag.weathercheck.ui.decorators.HighlightWeekendsDecorator;
import com.example.tbag.weathercheck.ui.decorators.MySelectorDecorator;
import com.example.tbag.weathercheck.ui.decorators.OneDayDecorator;
import com.example.tbag.weathercheck.util.ToastUtil;
import com.example.tbag.weathercheck.util.helper.CacheHelper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Shows off the most basic usage
 */
public class BasicActivityDecorated extends AppCompatActivity implements OnDateSelectedListener {

  private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

  private ArrayList<CalendarDay> days;
  private Handler mhandler;
  private String message, time;


  @InjectView(R.id.calendarView)
  MaterialCalendarView widget;
  @InjectView(R.id.image_toolbar_back)
  ImageView imageToolbarBack;
  @InjectView(R.id.text_toolbar_title)
  TextView textToolbarTitle;
  @InjectView(R.id.icon_toolbar_more)
  TextView iconToolbarMore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mhandler = new Handler(getMainLooper());
    setContentView(R.layout.activity_basic);
    ButterKnife.inject(this);
    imageToolbarBack.setVisibility(View.VISIBLE);
    imageToolbarBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    textToolbarTitle.setText("时间选择");
    iconToolbarMore.setVisibility(View.GONE);
    widget.setOnDateChangedListener(this);
    widget.setShowOtherDates(MaterialCalendarView.SHOW_ALL);

    Calendar calendar = Calendar.getInstance();
    widget.setSelectedDate(calendar.getTime());

    calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1);
    widget.setMinimumDate(calendar.getTime());

    calendar.set(calendar.get(Calendar.YEAR), Calendar.DECEMBER, 31);
    widget.setMaximumDate(calendar.getTime());

    widget.addDecorators(
        new MySelectorDecorator(this),
        new HighlightWeekendsDecorator(),
        oneDayDecorator
    );

    new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
  }

  @Override
  public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
    //If you change a decorate, you need to invalidate decorators
    oneDayDecorator.setDate(date.getDate());
    if (!days.isEmpty()) {
      if (days.contains(CalendarDay.from(date.getDate()))) {
        CalendarDay day = CalendarDay.from(date.getDate());
        ArrayList<HistoryData> list = CacheHelper.getSearchCache();
        for (HistoryData data : list) {
          if (data.calendarDay.equals(day)) {
            message = data.message;
            time = day.getYear() + "年" + day.getMonth() + "月" + day.getDay() + "号";
            mhandler.postDelayed(new Runnable() {
              @Override
              public void run() {
                Log.e("ss", message);
                startActivity(ShowOldWeatherActivity.newShowOldWeatherIntent(BasicActivityDecorated.this, message, time));
              }
            }, 0);
            break;
          }
        }
      } else {
        ToastUtil.showToast(this, "暂无历史记录");
      }
    } else {
      ToastUtil.showToast(this, "暂无历史记录");
    }
    widget.invalidateDecorators();
  }

  /**
   * Simulate an API call to show how to add decorators
   */
  private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

    @Override
    protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
      ArrayList<CalendarDay> dates = new ArrayList<>();
      ArrayList<HistoryData> list = CacheHelper.getSearchCache();
      for (HistoryData data : list) {
        dates.add(data.calendarDay);
      }
      days = dates;
      return dates;
    }

    @Override
    protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
      super.onPostExecute(calendarDays);

      if (isFinishing()) {
        return;
      }

      widget.addDecorator(new EventDecorator(Color.RED, calendarDays));
    }
  }
}
