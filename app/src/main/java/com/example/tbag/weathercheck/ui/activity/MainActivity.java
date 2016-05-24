package com.example.tbag.weathercheck.ui.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tbag.weathercheck.R;
import com.example.tbag.weathercheck.bluetooth.BTService;
import com.example.tbag.weathercheck.data.event.BusProvider;
import com.example.tbag.weathercheck.data.event.DeviceConnectedEvent;
import com.example.tbag.weathercheck.ui.fragment.ShowConnectionFragment;
import com.example.tbag.weathercheck.ui.fragment.ShowWeatherFragment;
import com.example.tbag.weathercheck.util.ToastUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class MainActivity extends AppCompatActivity {
  
  private static boolean isConnected = false;
  private int clickCount = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BusProvider.getInstance().register(this);
    setContentView(R.layout.activity_main);
    if(!isConnected) {
      getSupportFragmentManager().beginTransaction().replace(R.id.show_fragment, new ShowConnectionFragment()).commit();
    }
    else {
      getSupportFragmentManager().beginTransaction().replace(R.id.show_fragment,new ShowWeatherFragment()).commit();
    }
  }
  
  @Subscribe
  public void deviceConnected(DeviceConnectedEvent event){
    isConnected = true;
    getSupportFragmentManager().beginTransaction().replace(R.id.show_fragment,new ShowWeatherFragment()).commit();
    ToastUtil.showToast(this,"连接成功");
  }


  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  public void onBackPressed() {
    //super.onBackPressed();
    if(isConnected&&clickCount == 0){
      ToastUtil.showToast(this,"再按返回键退出");
      clickCount++;
    }else if(isConnected&&clickCount ==1){
      this.finish();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    BusProvider.getInstance().unregister(this);
  }
}
