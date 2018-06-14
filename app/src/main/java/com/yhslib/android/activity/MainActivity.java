package com.yhslib.android.activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.yhslib.android.util.ActivityContainer;
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
    private Boolean getUnredFlag = true;  // 是否请求到未读通知数量的标记
    private int unreadNotificationNumber = 0;

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
            setNotificationButton();
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
     * 未读通知按钮跳转
     */
    private void setNotificationButton() {
        if (actionBar != null) {
            ImageView have_new_message;
            have_new_message = actionBar.getCustomView().findViewById(R.id.have_new_message);
            TextView unread;
            ImageView redDot;
            unread = actionBar.getCustomView().findViewById(R.id.unread_text);
            redDot = actionBar.getCustomView().findViewById(R.id.red_dot);
            getUnreadNotificationNumber(unread, redDot);
            have_new_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigation.setSelectedItemId(R.id.navigation_notifications);
                }
            });
        }
    }

    /**
     * 关于我的页面 右上角设置的监听 跳转到设置页
     */
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
     * 发现在fragment进行切换时，会重复请求接口，因此使用flag来限制重复请求
     *
     * @param unread （未读通知的文本控件）
     * @param redDot （未读通知的小红点）
     */
    private void getUnreadNotificationNumber(final TextView unread, final ImageView redDot) {
        if (!getUnredFlag) {
            // 防止重复请求
            setUnreadView(unread, redDot, unreadNotificationNumber, true, true);
            return;
        }
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
                        setUnreadView(unread, redDot, 0, false, false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int countComment = jsonObject.getInt("count");
                            if (countComment > 0) {
                                setUnreadView(unread, redDot, countComment, true, true);
                            }
                            unreadNotificationNumber = countComment;
                            getUnredFlag = false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setUnreadView(unread, redDot, 0, false, false);
                        }
                    }
                });
    }

    /**
     * 设置右上角 未读通知的样式
     *
     * @param unread        TextView
     * @param redDot        ImageView
     * @param number        未读通知数量
     * @param unreadVisible unreadTxt是否可见
     * @param redDotVisible redDotImage是否可见
     */
    private void setUnreadView(TextView unread, ImageView redDot, int number, Boolean unreadVisible, Boolean redDotVisible) {
        if (unreadVisible) {
            unread.setVisibility(View.VISIBLE);
            if (number > 100) {
                unread.setText("99+");
            } else {
                unread.setText(String.valueOf(number));
            }
        } else {
            unread.setVisibility(View.INVISIBLE);
        }

        if (redDotVisible) {
            redDot.setVisibility(View.VISIBLE);
        } else {
            redDot.setVisibility(View.INVISIBLE);
        }
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

    /**
     * 重写返回按钮的点击监听
     */
    @Override
    public void onBackPressed() {
        exit_dialog();
    }

    /**
     * 退出整个app
     *
     * @param
     * @return
     */
    private void exit_dialog() {
        new AlertDialog.Builder(this).setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // MainActivity.this.finish();
                ActivityContainer.getInstance().finishAllActivity();
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
