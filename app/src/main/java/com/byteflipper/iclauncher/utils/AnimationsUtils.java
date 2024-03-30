package com.byteflipper.iclauncher.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.byteflipper.iclauncher.R;

public class AnimationsUtils {

    public static void expand(View view, Context context) {
        Animation expandAnimation = AnimationUtils.loadAnimation(context, R.anim.expand_button);
        view.startAnimation(expandAnimation);
        view.setVisibility(View.VISIBLE);
    }

    public static void collapse(View view, Context context) {
        Animation collapseAnimation = AnimationUtils.loadAnimation(context, R.anim.collapse_button);
        view.startAnimation(collapseAnimation);
        view.setVisibility(View.GONE);
    }
}
