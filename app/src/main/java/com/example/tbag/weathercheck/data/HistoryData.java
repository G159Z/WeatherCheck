package com.example.tbag.weathercheck.data;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by tbag on 16/5/18.
 */
public class HistoryData implements Serializable {
  public HistoryData(CalendarDay calendarDay,String message){
    this.calendarDay = calendarDay;
    this.message = message;
  }
  public CalendarDay calendarDay;
  public String message;
}
