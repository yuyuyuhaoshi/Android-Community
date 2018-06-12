package com.yhslib.android.activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.fragment.CommunityFragment;
import com.yhslib.android.fragment.DiscoveryFragment;
import com.yhslib.android.fragment.MineFragment;
import com.yhslib.android.fragment.NotificationFragment;
import com.yhslib.android.util.BaseActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import okhttp3.Call;


public class MainActivity extends BaseActivity {
    private String TAG = "MainActivity";
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private Fragment[] fragments;
    private String userID;
    private String token;
    private ImageView settingsImage;

    private final int FRAGMENT_COUNT = 4;
    private final int COMMUNITY_FRAGMENT = 0;
    private final int DISCOVERY_FRAGMENT = 1;
    private final int NOTIFICATION_FRAGMENT = 2;
    private final int MINE_FRAGMENT = 3;

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra(IntentFields.USERID);
        token = intent.getStringExtra(IntentFields.TOKEN);
        Log.d(TAG, userID + "  " + token);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findView() {
        navigation = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.viewPager);
        actionBar = getSupportActionBar();
    }

    @Override
    protected void initView() {
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_community);
        }
        fragments = new Fragment[FRAGMENT_COUNT];
        fragments[COMMUNITY_FRAGMENT] = CommunityFragment.newInstance();
        fragments[DISCOVERY_FRAGMENT] = DiscoveryFragment.newInstance();
        fragments[NOTIFICATION_FRAGMENT] = NotificationFragment.newInstance(token);
        fragments[MINE_FRAGMENT] = MineFragment.newInstance(userID, token);

        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager.setOffscreenPageLimit(FRAGMENT_COUNT - 1);
        disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_community:
                    viewPager.setCurrentItem(COMMUNITY_FRAGMENT);
                    actionBar.setCustomView(R.layout.actionbar_community);
                    setNotificationButton();
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
                    setSettingsButton();
                    return true;
            }
            return false;
        }
    };

    /**
     * [设置未读通知按钮跳转]
     */
    private void setNotificationButton() {
        if (actionBar != null) {
            ImageView have_new_message;
            have_new_message = actionBar.getCustomView().findViewById(R.id.have_new_message);
            TextView unread;
            ImageView redDot;
            unread = actionBar.getCustomView().findViewById(R.id.unread_text);
            redDot = actionBar.getCustomView().findViewById(R.id.red_dot);
            setUnread(unread, redDot);
            have_new_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigation.setSelectedItemId(R.id.navigation_notifications);
                }
            });
        }
    }

    private void setSettingsButton() {
        if (actionBar != null) {
            settingsImage = actionBar.getCustomView().findViewById(R.id.settings_image);
            settingsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    /**
     * [设置未读通知的数量]
     *
     * @param unread （未读通知的文本控件）
     * @param redDot （未读通知的小红点）
     */
    private void setUnread(final TextView unread, final ImageView redDot) {
        String url = URL.Notification.getNotification();
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addParams("unread", "true")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, ".setUnread()" + e.getMessage());
                        unread.setText("");
                        redDot.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int countComment = jsonObject.getInt("count");
                            if (countComment > 0) {
                                unread.setVisibility(View.VISIBLE);
                                redDot.setVisibility(View.VISIBLE);
                                if (countComment > 100) {
                                    unread.setText("99+");
                                } else
                                    unread.setText(String.valueOf(countComment));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            unread.setVisibility(View.INVISIBLE);
                            redDot.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

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
     * 去掉底部bar的滑动偏移
     *
     * @param
     * @return
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
