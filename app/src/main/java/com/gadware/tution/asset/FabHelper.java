package com.gadware.tution.asset;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gadware.tution.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabHelper {

    public static int animateFAB(Context context, int isFabOpen, FloatingActionButton fab, FloatingActionButton fab1, FloatingActionButton fab2, TextView l1, TextView l2){
        Animation fab_open = AnimationUtils.loadAnimation(context, R.anim.fab_open);
        Animation fab_close = AnimationUtils.loadAnimation(context, R.anim.fab_close);
        Animation rotate_forward = AnimationUtils.loadAnimation(context, R.anim.rotate_forward);
        Animation rotate_backward = AnimationUtils.loadAnimation(context, R.anim.rotate_backward);
        if(isFabOpen==1){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.GONE);

            return 0;

        } else {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.VISIBLE);
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);

            return  1;

        }
    }
}
