package com.yhslib.android.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by jerryzheng on 2018/5/24.
 */

public class SlideBar {
    private int lastVisibleItemPosition=0;
    private boolean isDown=true,isUp=true;
    private View view;
    private ListView listView;
    public SlideBar(View view, ListView listView){
        this.view=view;
        this.listView=listView;
    }
    public void SetSlideBar(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > lastVisibleItemPosition) {
                    beginScrollHideRight(true,isDown);
                    isDown=false;
                    isUp=true;
                } else if (firstVisibleItem < lastVisibleItemPosition) {
                    beginScrollHideRight(false,isUp);
                    isUp=false;
                    isDown=true;
                } else if (firstVisibleItem == lastVisibleItemPosition) {
                    return;
                }
                lastVisibleItemPosition = firstVisibleItem;
            }
        });
    }

    public void beginScrollHideRight(boolean isDown,boolean isDone) {
        TranslateAnimation translateAnimation;
        if (isDown){
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -170f);
        }else {
            translateAnimation = new TranslateAnimation(0.0f, 0.0f, -170f, -0f);
        }

//        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (view.getVisibility()==View.VISIBLE){
//                    view.setVisibility(View.GONE);
//                }
//                if (view.getVisibility()==View.GONE){
//                    view.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(300);
        if (isDone){
            view.startAnimation(translateAnimation);
        }
    }
}
