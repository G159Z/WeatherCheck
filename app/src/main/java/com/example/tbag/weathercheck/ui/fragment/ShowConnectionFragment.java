package com.example.tbag.weathercheck.ui.fragment;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.tbag.weathercheck.R;
import com.example.tbag.weathercheck.bluetooth.BTService;
import com.example.tbag.weathercheck.data.event.BusProvider;
import com.example.tbag.weathercheck.data.event.DeviceConnectedEvent;
import com.example.tbag.weathercheck.ui.activity.MainActivity;
import com.example.tbag.weathercheck.ui.widget.CloudWaver;
import com.example.tbag.weathercheck.ui.widget.WavingLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by tbag on 16/5/17.
 */
public class ShowConnectionFragment extends Fragment {

  @InjectView(R.id.score_cloud_1)
  View scoreCloud1;
  @InjectView(R.id.score_cloud_2)
  View scoreCloud2;
  @InjectView(R.id.score_cloud_3)
  View scoreCloud3;
  @InjectView(R.id.score_cloud_4)
  View scoreCloud4;
  @InjectView(R.id.layout_waving)
  WavingLayout layoutWaving;
  @InjectView(R.id.text_animator)
  TextView textAnimator;
  @InjectView(R.id.text_hint)
  TextView textHint;

  private Context mContext;
  private String stringAnimator = "....";
  private String address;
  private BTService service;
  private boolean init = false;
  private CloudWaver[] cloudWavers = new CloudWaver[4];


  private void afterInject() {
    final ValueAnimator textAnimator = ValueAnimator.ofInt(4);
    textAnimator.setRepeatCount(Animation.INFINITE);
    textAnimator.setDuration(1800);
    textAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        textAnimator((int) (animation.getAnimatedValue()));
      }
    });
    textAnimator.start();
    cloudWavers[0] = new CloudWaver(scoreCloud1);
    cloudWavers[1] = new CloudWaver(scoreCloud2);
    cloudWavers[2] = new CloudWaver(scoreCloud3);
    cloudWavers[3] = new CloudWaver(scoreCloud4);
    for (CloudWaver waver : cloudWavers) {
      waver.start();
    }
    Handler handler = new Handler(getActivity().getMainLooper());
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Rect wavingRect = new Rect();
        layoutWaving.getLocalVisibleRect(wavingRect);
        layoutWaving.startWave(wavingRect.centerX(), wavingRect.centerY());
      }
    }, 200);
  }

  private void textAnimator(int count) {
    textAnimator.setText("Waiting" + stringAnimator.substring(0, count));
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
      , @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View contentView = inflater.inflate(R.layout.fragment_show_connect, container, false);
    ButterKnife.inject(this, contentView);
    afterInject();
    startConnect();
    return contentView;
  }

  private void startConnect() {
    BTService.getInstance(mContext, new BTService.ServiceGotListener() {
      BroadcastReceiver bcr;

      @Override
      public void gotOneInstanceService(final BTService service) {
        updateAdapter(service);
        bcr = new BroadcastReceiver() {
          @Override
          public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            if (arg1.getAction().equals(BTService.ScanCompleteBroadcast)) {
            }
            if (arg1.getAction().equals(BTService.DeviceFoundBroadcast)) {
              if (arg1.getStringExtra("name").equals("HC-06")) {
                textHint.setText("找到设备开始连接。");
                address = arg1.getStringExtra("address");
                new Thread(new Runnable() {
                  @Override
                  public void run() {
                    boolean isOpen = service.connectTo(address);
                    if (isOpen) {
                      service.startListen();
                      ((MainActivity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          textHint.setText("设备连接完成。");
                          deviceConnected();

                        }
                      });
                    }
                  }
                }).start();
              }
            }
          }

        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(BTService.ScanCompleteBroadcast);
        filter.addAction(BTService.DeviceFoundBroadcast);
        mContext.registerReceiver(bcr, filter);
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    mContext = context;
  }

  private void updateAdapter(BTService service) {
    init = true;
    this.service = service;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (init) {
      if (!service.isOk) {
        new Thread(new Runnable() {
          @Override
          public void run() {
            boolean isOpen = service.connectTo(address);
            if (isOpen) {
              service.startListen();
              ((MainActivity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  deviceConnected();
                }
              });
            }
          }
        }).start();

      }
    }

  }

  private void deviceConnected() {
    BusProvider.getInstance().post(new DeviceConnectedEvent());
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    for (int i = 0; i < 4; i++) {
      cloudWavers[i].stop();
    }
    layoutWaving.stopWave();
  }
}
