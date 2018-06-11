package com.yhslib.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.config.HashMapField;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.CustomListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class EmailActivity extends BaseActivity {
    private String TAG = "EmailActivity";
    private String token;
    private int currentPage = 1;
    private int lastPage;

    private ArrayList<HashMap<String, Object>> hm = new ArrayList<>();

    private CustomListView listView;
    private SimpleAdapter adapter;
    private Boolean RefreshFlag = false; // 防止多次刷新标记
    private ActionBar actionBar;
    private ImageView returnArrowImage;
    private ImageView addImage;

    private AlertDialog.Builder addEmailDialogBuilder;
    private AlertDialog addEmailDialog;

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        token = intent.getStringExtra(IntentFields.TOKEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_list;
    }

    @Override
    protected void findView() {
        listView = findViewById(R.id.email_list);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_email);
            returnArrowImage = findViewById(R.id.return_image); // 此image findView 只能写在这
            addImage = findViewById(R.id.add_image);
        }
    }

    @Override
    protected void initView() {
        buildAddEmailDialog();
    }

    @Override
    protected void setListener() {
        setListViewPullListener();
        returnArrowImage.setOnClickListener(this);
        addImage.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        fetchEmail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_image:
                EmailActivity.this.finish();
                break;
            case R.id.add_image:
                addEmailDialog.show();
                break;
        }
    }

    /**
     * 设置往下拉至底部加载更多数据
     */
    private void setListViewPullListener() {
        // list view 下拉加载下一页文章
        listView.setOnPullToRefreshListener(new CustomListView.OnPullToRefreshListener() {
            @Override
            public void onBottom() {
                if (!RefreshFlag) {
                    return;
                }
                if (currentPage == lastPage) {
                    return;
                }
                listView.onRefresh(true);
                currentPage += 1;
                fetchEmail();
                listView.onRefresh(false);
            }

            @Override
            public void onTop() {

            }
        });
    }

    /**
     * 获取邮箱列表
     */
    private void fetchEmail() {
        RefreshFlag = false;
        String url = URL.User.getEmailList();
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addParams("page", currentPage + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                        hm.addAll(formatEmailsJSON(response));
                        // 在请求第一页的时候初始化Adapter
                        // 其他时候更新Adapter即可
                        if (currentPage == 1) {
                            setEmailListAdapter(hm);
                            RefreshFlag = true;
                            return;
                        }
                        adapter.notifyDataSetChanged();
                        RefreshFlag = true;
                    }
                });
    }

    /**
     * 解析JSON字符串
     *
     * @param response 获取到的JSON数据
     * @return ArrayList
     */
    private ArrayList<HashMap<String, Object>> formatEmailsJSON(String response) {
        RefreshFlag = false;
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            lastPage = jsonObject.getInt("last_page");
            JSONArray emailsArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < emailsArray.length(); i++) {
                JSONObject emailObject = (JSONObject) emailsArray.opt(i);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(HashMapField.EMAILID, emailObject.getString("id"));
                hashMap.put(HashMapField.EMAIL, emailObject.getString("email"));

                String primary = emailObject.getString("primary");
                if (primary.equals("true")) {
                    hashMap.put(HashMapField.PRIMARY, getResources().getString(R.string.primary_email));
                } else {
                    hashMap.put(HashMapField.PRIMARY, "");
                }

                String verified = emailObject.getString("verified");
                if (verified.equals("false")) {
                    hashMap.put(HashMapField.VERIFIED, getResources().getString(R.string.unverified));
                } else {
                    hashMap.put(HashMapField.VERIFIED, "");
                }

                resultList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 设置listAdapter
     *
     * @param list 存放email id, email, PRIMARY, PRIMARY的ArrayList
     */
    private void setEmailListAdapter(final ArrayList<HashMap<String, Object>> list) {
        String[] from = {HashMapField.EMAIL, HashMapField.PRIMARY, HashMapField.VERIFIED};
        int[] to = {R.id.email, R.id.email_primary, R.id.email_verified};
        adapter = new SimpleAdapter(EmailActivity.this, list, R.layout.list_email, from, to) {
            @Override
            public long getItemId(int position) {
                return Integer.parseInt(list.get(position).get(HashMapField.EMAILID).toString());
            }
        };
        listView.setAdapter(adapter);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater menuInflater = EmailActivity.this.getMenuInflater();
                menuInflater.inflate(R.menu.email_list_menu, menu);
            }
        });
    }

    /**
     * 长按item点击菜单
     *
     * @param item 被点击的item项
     * @return boolean 没有实际意义
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = info.id;
        Log.d(TAG, id + "");
        switch (item.getItemId()) {
            case R.id.set_primary:
                setPrimaryEmail(id + "");
                break;
            case R.id.verify:
                verifyEmail(id + "");
                break;
            case R.id.delete:
                deleteEmail(id + "");
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 创建添加邮箱的对话框
     */
    private void buildAddEmailDialog() {
        addEmailDialogBuilder = new AlertDialog.Builder(EmailActivity.this);
        addEmailDialogBuilder.setTitle("添加邮箱");
        final EditText editText = new EditText(EmailActivity.this);
        editText.setSingleLine();
        addEmailDialogBuilder.setView(editText);
        addEmailDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addEmail(editText.getText().toString());
            }
        });
        addEmailDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        addEmailDialogBuilder.setCancelable(true);
        addEmailDialog = addEmailDialogBuilder.create();
        addEmailDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * 设置主邮箱
     *
     * @param emailID email的id
     */
    private void setPrimaryEmail(String emailID) {
        String url = URL.User.setPrimaryEmail(emailID);
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "设置失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.d(TAG, response);
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "设置成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 删除邮箱
     *
     * @param emailID email的id
     */
    private void deleteEmail(String emailID) {
        String url = URL.User.deleteEmail(emailID);
        OkHttpUtils
                .delete()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "邮箱删除失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "邮箱删除成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 验证邮箱
     *
     * @param emailID email的id
     */
    private void verifyEmail(String emailID) {
        String url = URL.User.deleteEmail(emailID);
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "邮箱验证失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.d(TAG, response);
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "验证已发送至邮箱，请查收!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 添加邮箱
     *
     * @param email 邮箱地址
     */
    private void addEmail(String email) {
        String url = URL.User.addEmail();
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addParams("email", email)
                .build()
                .connTimeOut(10000)
                .readTimeOut(10000)
                .writeTimeOut(10000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "邮箱添加失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "邮箱添加成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}