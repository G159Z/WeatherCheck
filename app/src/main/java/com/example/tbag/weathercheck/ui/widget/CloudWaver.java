package com.example.tbag.weathercheck.ui.widget;

import android.animation.Animator;
import android.view.View;
import android.view.ViewPropertyAnimator;

/**
 * Created by suanmiao on 15/2/23.
 * animator controller for cloud in score page
 */
public class CloudWaver {
  private View target;
  private ViewPropertyAnimator currentAnimator;

  public CloudWaver(View target) {
    this.target = target;
  }

  public void start() {
    boolean originalPlace = target.getTranslationX() == 0;
    int randomRange = (int) (100 + Math.random() * 50);
    long randomDuration = (long) (6500 + Math.random() * 3500);
    currentAnimator =
        target.animate().translationX(originalPlace ? randomRange : 0).setDuration(randomDuration)
            .setListener(listener);
    currentAnimator.start();
  }

  private Animator.AnimatorListener listener = new Animator.AnimatorListener() {
    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
      currentAnimator.setListener(null);
      start();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
  };

  public void stop() {
    if (currentAnimator != null) {
      currentAnimator.cancel();
    }
  }

}
