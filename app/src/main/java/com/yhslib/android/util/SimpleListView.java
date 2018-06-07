package com.yhslib.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;


/**
 * 项目名称：Huakui
 * 创建人：付三
 * 创建时间：2016/6/3 13:58
 *
 * @version V1.0
 */

public class SimpleListView extends SwipeRefreshLayout {

    private ListView mListView;
    private LoadMoreStatus mLoadMoreStatus = LoadMoreStatus.CLICK_TO_LOAD;

    private OnLoadListener mOnLoadListener;
    private View mLoadMoreView;
    private AbsListView.OnScrollListener mOnScrollListener;
    private View mEmptyView;
    private ListAdapter mAdapter;
    private View foreground;
    public ListView getmListView() {
        return mListView;
    }
    public OnLoadListener getmOnLoadListener() {
        return mOnLoadListener;
    }
    /**
     * 加载更多状态
     */
    public static enum LoadMoreStatus {
        /**
         * 点击加载更多
         */
        CLICK_TO_LOAD,
        /**
         * 正在加载
         */
        LOADING,
        /**
         * 没有更多内容了
         */
        LOADED_ALL
    }

    /**
     * 加载监听器
     */
    public static interface OnLoadListener {
        /**
         * 下来刷新或者加载更多时触发该回调
         *
         * @param isRefresh true为下拉刷新 false为加载更多
         */
        public void onLoad(boolean isRefresh);
    }

    public SimpleListView(Context context) {
        super(context);
        init(context, null);
    }

    public SimpleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(final Context context, AttributeSet attrs) {
        mListView = new ListView(context, attrs);
        addView(mListView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        mListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
////                Toast.makeText(getContext(), "长按了" + 2323232 + " ", Toast.LENGTH_SHORT).show();
////                foreground=v.findViewById(R.id.foreground);
////                Animation open = AnimationUtils.loadAnimation(context, R.anim.welcome_alpha);
////                AnimationSet animationSet;
////                animationSet = new AnimationSet(true);
////                animationSet.addAnimation(open);
////                animationSet.setFillAfter(true);
////                foreground.startAnimation(animationSet);
//            }
//        });
//
////        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
////            @Override
////            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
////                Toast.makeText(getContext(), "长按了" + 2323232 + " ", Toast.LENGTH_SHORT).show();
////                return false;
////            }
////        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean mIsEnd = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
                if (scrollState == SCROLL_STATE_IDLE) {
                    //1:到达底部 2：底部当前可以加载更多 3：顶部不在刷新中状态
                    if (mIsEnd && mLoadMoreStatus == LoadMoreStatus.CLICK_TO_LOAD && !isRefreshing()) {
                        setLoadMoreStatus(LoadMoreStatus.LOADING);
                        if (mLoadMoreStatus != null) {
                            mOnLoadListener.onLoad(false);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
                if (firstVisibleItem + visibleItemCount >= totalItemCount - 1) {
                    if (mLoadMoreView != null)
                        mLoadMoreView.setVisibility(VISIBLE);
                    mIsEnd = true;
                } else {
                    mIsEnd = false;
                    if (mLoadMoreView != null)
                        mLoadMoreView.setVisibility(GONE);
                }
            }
        });

        super.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLoadMoreStatus != LoadMoreStatus.LOADING) {
                    if (mOnLoadListener != null) {
                        mOnLoadListener.onLoad(true);
                    }
                } else {
                    SimpleListView.super.setRefreshing(false);
                }
            }
        });
    }
//
//    public void addHeaderView(View view) {
//        mListView.addHeaderView(view);
//    }
//
//    public void addHeaderView(View v, Object data, boolean isSelectable) {
//        mListView.addHeaderView(v, data, isSelectable);
//    }
//
//    public void addFooterView(View view) {
//        mListView.addFooterView(view);
//    }
//
//    public void addFooterView(View v, Object data, boolean isSelectable) {
//        mListView.addFooterView(v, data, isSelectable);
//    }

    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mListView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mListView.setOnItemLongClickListener(listener);
    }

    public void setEmptyView(View emptyView) {
        if (emptyView != null) {
            mEmptyView = emptyView;
            if (mAdapter != null && mAdapter.getCount() > 0) {
                mEmptyView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
//            mListView.setEmptyView(emptyView);
        }
    }

    @Override
    @Deprecated
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
    }

    @Override
    @Deprecated
    public void setRefreshing(boolean refreshing) {
    }

    public void setAdapter(final ListAdapter adapter) {
        if (adapter == null) {
            return;
        }
        mAdapter = adapter;
        if (mLoadMoreView == null) {
//            mLoadMoreView = new FrameLayout(R.layout.actionbar_community);
//            mLoadMoreView.setTextColor(0xff333333);
//            mLoadMoreView.setTextSize(14);
//            mLoadMoreView.setGravity(Gravity.CENTER);
            int count = adapter.getCount();
            if (mLoadMoreView != null)
                mLoadMoreView.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            if (mEmptyView != null) {
                mEmptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
            }
            if (mLoadMoreView != null) {
                mLoadMoreView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLoadMoreStatus == LoadMoreStatus.CLICK_TO_LOAD && !isRefreshing()) {
                            setLoadMoreStatus(LoadMoreStatus.LOADING);
                            if (mLoadMoreStatus != null) {
                                mOnLoadListener.onLoad(false);
                            }
                        }
                    }
                });
            }
            if (mLoadMoreView != null) {
                mLoadMoreView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.offset_10dp) * 4));
                mListView.addFooterView(mLoadMoreView);
            }

        }
        mListView.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                int count = adapter.getCount();
                mLoadMoreView.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
                if (mEmptyView != null) {
                    mEmptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
                }
            }
        });
    }

    TextView tittle;
    ProgressBar progressBar;

    private void setLoadMoreStatus(LoadMoreStatus status) {
        tittle = mLoadMoreView.findViewById(R.id.title);
        progressBar = mLoadMoreView.findViewById(R.id.progressBar);
        mLoadMoreStatus = status;
        if (mLoadMoreView != null) {
            if (mLoadMoreStatus == LoadMoreStatus.LOADED_ALL) {
                progressBar.setVisibility(INVISIBLE);
                tittle.setText("没有更多内容了");
            } else if (mLoadMoreStatus == LoadMoreStatus.LOADING) {
                progressBar.setVisibility(VISIBLE);
                tittle.setText("正在加载...");
            } else {
                progressBar.setVisibility(INVISIBLE);
                tittle.setText("下滑加载更多");
            }
        }
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mOnLoadListener = listener;
    }

    public void finishLoad(boolean loadAll) {
        super.setRefreshing(false);
        setLoadMoreStatus(loadAll ? LoadMoreStatus.LOADED_ALL : LoadMoreStatus.CLICK_TO_LOAD);
    }

    public void setFooter(View view) {
        this.mLoadMoreView = view;
    }


}
