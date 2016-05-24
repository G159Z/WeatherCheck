package com.example.tbag.weathercheck.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.example.tbag.weathercheck.R;

/**
 * Created by tbag on 16/5/3.
 */
public class WavingLayout extends LinearLayout {

  private static final long WAVE_DURATION = 1800;
  private ValueAnimator waveAnimator;
  private float outerWaveRadius = 0;
  private float innerWaveRadius = 0;
  private float maxWaveRadius = 0;
  private Paint mPaint;
  private int waveColor;
  private float waveCenterX;
  private float waveCenterY;

  public WavingLayout(Context context) {
    super(context);
    init();
  }

  private void init() {
    mPaint = new Paint();
    waveColor = getResources().getColor(R.color.waving_blue);
  }

  public WavingLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public WavingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void startWave(float centerX, float centerY) {
    this.waveCenterX = centerX;
    this.waveCenterY = centerY;
    maxWaveRadius = Math.max(getMeasuredHeight(), getMeasuredWidth())/2;
    waveAnimator = ValueAnimator.ofFloat(0, 1);
    waveAnimator.setDuration(WAVE_DURATION);
    waveAnimator.setRepeatCount(Animation.INFINITE);
    waveAnimator.setInterpolator(new AccelerateInterpolator());
    waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float value = (float) animation.getAnimatedValue();
        outerWaveRadius = value * maxWaveRadius;
        // innerWaveRadius = outerWaveRadius / 2;
        if (value < 0.7) {
          innerWaveRadius = 0;
        } else {
          innerWaveRadius =
              (int) ((outerWaveRadius - innerWaveRadius) * (value - 0.5) * 2 + innerWaveRadius);
        }
        if (innerWaveRadius >= outerWaveRadius) {
          innerWaveRadius = outerWaveRadius;
        }
        invalidate();
      }
    });
    waveAnimator.start();
  }

  public void stopWave(){
    if(waveAnimator != null)
    waveAnimator.cancel();
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    mPaint.setColor(waveColor);
    mPaint.setAlpha(60);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setStrokeWidth(outerWaveRadius - innerWaveRadius);
    canvas.drawCircle(waveCenterX, waveCenterY, outerWaveRadius, mPaint);
  }
}
