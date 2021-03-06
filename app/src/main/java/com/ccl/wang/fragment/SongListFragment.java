package com.ccl.wang.fragment;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ccl.wang.R;
import com.ccl.wang.activity.SongListActivity;
import com.ccl.wang.adapter.SongListAdapter;
import com.ccl.wang.asynctask.SongListTask;
import com.ccl.wang.bean.SongList;
import com.ccl.wang.utils.DataUtils;
import com.github.clans.fab.FloatingActionMenu;

public class SongListFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private SongListAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private GridLayoutManager mGridLayoutManager;
    private FloatingActionMenu mFabMenu;
    private com.github.clans.fab.FloatingActionButton mFabWangyi;
    private com.github.clans.fab.FloatingActionButton mFabXiami;
    private com.github.clans.fab.FloatingActionButton mFabQQ;
    public static int mCurrentDataType = DataUtils.DATA_WANGYI;
    private boolean mSongListToBack = false;

    @Override
    protected int getViewId() {
        return R.layout.fragment_auslese_songlist_layout;
    }

    @Override
    protected void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recuclerview_auslese_songlist);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
        mFabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mFabWangyi = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_wangyi);
        mFabXiami = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_xiami);
        mFabQQ = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_qq);
    }

    @Override
    protected void initData() {
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new SongListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mFabMenu.setClosedOnTouchOutside(true);
    }

    private View.OnClickListener mFabItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int dataType = -1;
            switch (id) {
                case R.id.fab_wangyi:
                    dataType = DataUtils.DATA_WANGYI;
                    break;
                case R.id.fab_xiami:
                    dataType = DataUtils.DATA_XIAMI;
                    break;
                case R.id.fab_qq:
                    dataType = DataUtils.DATA_QQ;
                    break;
            }
            mFabMenu.toggle(true);
            if (dataType != -1 && dataType != mCurrentDataType) {
                mCurrentDataType = dataType;
                reGetData();
            }
        }
    };

    @Override
    protected void initListener() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SongListTask.LOAD_OFFSET = 0;
                new SongListTask(mAdapter, SongListFragment.this.getContext(), mRefreshLayout).execute(mCurrentDataType);
            }
        });

        mFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    mFabMenu.getMenuIconView().setImageResource(R.mipmap.ic_fab_close);
                } else {
                    mFabMenu.getMenuIconView().setImageResource(R.mipmap.ic_fab_open);
                }
            }
        });
        mFabWangyi.setOnClickListener(mFabItemClick);
        mFabXiami.setOnClickListener(mFabItemClick);
        mFabQQ.setOnClickListener(mFabItemClick);

        mAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSongListToBack = true;
                Intent intent = new Intent(getContext(), SongListActivity.class);
                ImageView iv = (ImageView) ((ViewGroup) ((ViewGroup) v).getChildAt(0)).getChildAt(0);
                SongList bean = (SongList) iv.getTag(R.id.songlist_img_tag);
                intent.putExtra("img", bean.imgUrl);
                intent.putExtra("href", bean.href);
                intent.putExtra("title", bean.title);

                if (Build.VERSION.SDK_INT >= 21) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), iv, "top").toBundle();
                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mSongListToBack) {
            reGetData();
        }else{
            mSongListToBack = false;
        }
    }

    private void reGetData() {
        mRefreshLayout.setRefreshing(true);
        SongListTask.LOAD_OFFSET = 0;
        new SongListTask(mAdapter, this.getContext(), mRefreshLayout).execute(mCurrentDataType);
    }
}
