package com.yhslib.android.activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.yhslib.android.R;
import com.yhslib.android.fragment.CommunityFragment;
import com.yhslib.android.fragment.DiscoveryFragment;
import com.yhslib.android.fragment.MineFragment;
import com.yhslib.android.fragment.NotificationFragment;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private Fragment[] fragments;

    private final int FRAGMENT_COUNT = 4;
    private final int COMMUNITY_FRAGMENT = 0;
    private final int DISCOVERY_FRAGMENT = 1;
    private final int NOTIFICATION_FRAGMENT = 2;
    private final int MINE_FRAGMENT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initView();
    }


    private void findView() {
        navigation = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.viewPager);
        actionBar = getSupportActionBar();
    }


    private void initView() {
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_community);
        }

        fragments = new Fragment[FRAGMENT_COUNT];
        fragments[COMMUNITY_FRAGMENT] = CommunityFragment.newInstance();
        fragments[DISCOVERY_FRAGMENT] = DiscoveryFragment.newInstance();
        fragments[NOTIFICATION_FRAGMENT] = NotificationFragment.newInstance();
        fragments[MINE_FRAGMENT] = MineFragment.newInstance();

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.setOffscreenPageLimit(FRAGMENT_COUNT - 1);
        disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Log.d(TAG, item.getItemId() + "");
            switch (item.getItemId()) {
                case R.id.navigation_community:
                    viewPager.setCurrentItem(COMMUNITY_FRAGMENT);
                    actionBar.setCustomView(R.layout.actionbar_community);
                    return true;
                case R.id.navigation_discovery:
                    viewPager.setCurrentItem(DISCOVERY_FRAGMENT);
                    actionBar.setCustomView(R.layout.actionbar_discovery);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(NOTIFICATION_FRAGMENT);
                    actionBar.setCustomView(R.layout.actionbar_notification);
                    return true;
                case R.id.navigation_mine:
                    viewPager.setCurrentItem(MINE_FRAGMENT);
                    actionBar.setCustomView(R.layout.actionbar_mine);
                    return true;
            }
            return false;
        }
    };

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            // Log.d(TAG, position + "");
            switch (position) {
                case COMMUNITY_FRAGMENT:
                    navigation.setSelectedItemId(R.id.navigation_community);
                    break;
                case DISCOVERY_FRAGMENT:
                    navigation.setSelectedItemId(R.id.navigation_discovery);
                    break;
                case NOTIFICATION_FRAGMENT:
                    navigation.setSelectedItemId(R.id.navigation_notifications);
                    break;
                case MINE_FRAGMENT:
                    navigation.setSelectedItemId(R.id.navigation_mine);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }


    };

    @Override
    public void onBackPressed() {
        exit_dialog();
    }

    private void exit_dialog() {
        new AlertDialog.Builder(this).setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        }).setNegativeButton("取消", null).show();
    }

    /**
     * [消除navigation偏移动画]
     *
     * @param view
     */
    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
