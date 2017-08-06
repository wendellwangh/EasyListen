package com.ccl.ccltools.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccl.ccltools.R;
import com.ccl.ccltools.asynctask.AusleseSongListTask;
import com.ccl.ccltools.bean.AusleseSongListBean;
import com.ccl.ccltools.fragment.AusleseSongListFragment;
import com.ccl.ccltools.other.GlideApp;
import com.ccl.ccltools.utils.UIUtils;

import java.util.List;


public class AusleseSongListAdapter extends RecyclerView.Adapter<AusleseSongListAdapter.MyViewHolder> {

    private Context mContext;
    private List<AusleseSongListBean> mDatas;
    private boolean mLoading = true;
    private int mItemHeight;

    public AusleseSongListAdapter(Context context) {
        mContext = context;
        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        mItemHeight = dm.widthPixels/2 - UIUtils.dp2px(12);
    }

    public void setDatas(List<AusleseSongListBean> data){
        mDatas = data;
        mLoading = false;
    }

    public void addDatas(List<AusleseSongListBean> data){
        if(mDatas != null){
            mDatas.addAll(data);
        }else{
            mDatas = data;
        }
        mLoading = false;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_auslese_view, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AusleseSongListBean ausleseSongListBean = mDatas.get(position);
        holder.title.setText(ausleseSongListBean.title);
        GlideApp.with(mContext)
                .load(ausleseSongListBean.imgUrl)
                .placeholder(R.mipmap.loading_spinner)
                .into(holder.img);

        if(!mLoading && position >= mDatas.size() - 10){
            mLoading = true;
            new AusleseSongListTask(this, mContext, null).execute(AusleseSongListFragment.mCurrentDataType);
        }
    }

    @Override
    public int getItemCount() {
        if(mDatas != null){
            return mDatas.size();
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        ImageView img;

        public MyViewHolder(View view)
        {
            super(view);
            title = (TextView) view.findViewById(R.id.tv_auslese_title);
            img = (ImageView) view.findViewById(R.id.iv_auslese_img);
            ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
            layoutParams.height = mItemHeight;
        }
    }
}