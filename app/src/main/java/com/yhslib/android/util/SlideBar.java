package com.yhslib.android.util;

import android.support.design.widget.BottomNavigationView;
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
    private boolean topBaIsDown=true,topBarIsUp=true,deepBaIsDown=true,deepBarIsUp=true;
    private View topBar;
    private BottomNavigationView deepBar;
    private ListView listView;
    public SlideBar(View topBar,BottomNavigationView deepBar, ListView listView){
        this.topBar=topBar;
        this.listView=listView;
        this.deepBar=deepBar;
    }
    public void SetSlideBar(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem > lastVisibleItemPosition) {
                    if (topBar!=null&&topBaIsDown){
                        slidBar(0,-170,topBar);
                        topBaIsDown=false;
                        topBarIsUp=true;
                    }
                    if (deepBar!=null&&deepBarIsUp){
                        slidBar(0,170,deepBar);
                        deepBaIsDown=true;
                        deepBarIsUp=false;
                    }
                } else if (firstVisibleItem < lastVisibleItemPosition) {
                    if (topBar!=null&&topBarIsUp){
                        slidBar(-170,0,topBar);
                        topBarIsUp=false;
                        topBaIsDown=true;
                    }
                    if (deepBar!=null&&deepBaIsDown){
                        slidBar(170,0,deepBar);
                        deepBaIsDown=false;
                        deepBarIsUp=true;
                    }
                } else if (firstVisibleItem == lastVisibleItemPosition) {
                    return;
                }
                lastVisibleItemPosition = firstVisibleItem;
            }
        });
    }

    public void slidBar(float from,float to,View view) {
        TranslateAnimation translateAnimation;
        translateAnimation = new TranslateAnimation(0.0f, 0.0f, from ,to);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(300);
        view.startAnimation(translateAnimation);
    }
}
