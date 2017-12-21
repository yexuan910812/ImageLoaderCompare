package com.tencent.henryye.imgcompare.testui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.henryye.imgcompare.R;
import com.tencent.henryye.imgcompare.loadercompat.CompatImageViewHolder;
import com.tencent.henryye.imgcompare.loadercompat.LoaderFactory;
import com.tencent.henryye.imgcompare.loadercompat.LoaderType;
import com.tencent.henryye.imgcompare.models.ResourceHolder;
import com.tencent.henryye.imgcompare.models.Utils;

import java.util.ArrayList;

@SuppressLint("LongLogTag")
public class ListImageActivity extends AppCompatActivity {
    private static final String TAG = "MicroMsg.ListImageActivity";

    private RecyclerView mList = null;
    private PickLoaderView mPickLoaderView = null;
    private PickerListAdapter mPickerListAdapter = null;
    private RecyclerView.LayoutManager mLayoutManager = null;
    private RecyclerView.SmoothScroller mScroller = null;

    private String[] resHolder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_image);
        mPickLoaderView = findViewById(R.id.pick_loader_view);
        mList = findViewById(R.id.test_list);
        initView();
    }

    private void initView() {
        mPickerListAdapter = new PickerListAdapter();
        mPickerListAdapter.setOnAllLoadedCallback(new PickerListAdapter.OnAllLoadedCallback() {
            @Override
            public void onAllLoaded() {
                Log.i(TAG, "hy: on all loaded. type: " + mPickLoaderView.getCurrentType());
                mPickLoaderView.onLoadedDone();
            }
        });
        mPickLoaderView.setUICallback(new PickLoaderView.UICallback() {
            @Override
            public void onRequestStart(LoaderType type) {
                resHolder = ResourceHolder.sampleAssets;
                mPickerListAdapter.resetData(resHolder, type);
//                startPlay();
            }
        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mScroller = new LinearSmoothScroller(this);
        mList.setLayoutManager(mLayoutManager);
        mList.setAdapter(mPickerListAdapter);
    }

    private void startPlay() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resHolder != null && resHolder.length != 0) {
                    mScroller.setTargetPosition(resHolder.length - 1);
                    mLayoutManager.startSmoothScroll(mScroller);
                }
            }
        });
    }

    // TODO 不考虑这么多数据同步了，反正一个加载完下一个才能加载
    private static class PickerListAdapter extends RecyclerView.Adapter<PickerListAdapter.ViewHolder> {
        private static final String TAG = "MicroMsg.PickerListAdapter";

        private ArrayList<PicModel> dataModelSet = null;
        private LoaderType mType = LoaderType.Picasso;
        private OnAllLoadedCallback mOnAllLoadedCallback = null;

        private PickerListAdapter() {
//            resetData(ResourceHolder.assets, LoaderType.Picasso);
        }

        public void resetData(String[] dataSet, LoaderType loaderType) {
            synchronized (PickerListAdapter.class) {
                if(dataSet != null) {
                    dataModelSet = new ArrayList<>(10);
                    for(String item : dataSet) {
                        PicModel model = new PicModel();
                        model.url = item;
                        dataModelSet.add(model);
                    }
                } else {
                    dataModelSet = null;
                }
                this.mType = loaderType;
                notifyDataSetChanged();
            }
        }

        public void setOnAllLoadedCallback(OnAllLoadedCallback callback) {
            this.mOnAllLoadedCallback = callback;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CompatImageViewHolder holder = new CompatImageViewHolder(parent.getContext());
            holder.setLayoutParams(new FrameLayout.LayoutParams(160 * 3, 160 * 3));
            ViewHolder viewHolder = new ViewHolder(holder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PicModel model = dataModelSet.get(position);
            Uri uri = Utils.getUriFromAssets(model.url, mType);
            LoaderFactory.INSTANCE.getLoader(mType).fillingWithUri(holder.ivHolder.getContext(), holder.ivHolder, uri, new LoaderFactory.IOnLoadCallback() {
                @Override
                public void onLoad(boolean isSuc) {
                    Log.i(TAG, "hy: loaded " + model.url);
                    synchronized (PickerListAdapter.class) {
                        model.isLoaded = true;
                        judgeCallback();
                    }
                }
            }, false);
        }

        private void judgeCallback() {
            if(dataModelSet != null) {
                boolean isAllSuc = true;
                for(PicModel model : dataModelSet) {
                    if(!model.isLoaded) {
                        isAllSuc = false;
                        break;
                    }
                }
                if(isAllSuc) {
                    if(mOnAllLoadedCallback != null) {
                        mOnAllLoadedCallback.onAllLoaded();
                    }
                }
            }
        }


        @Override
        public int getItemCount() {
            return dataModelSet != null ? dataModelSet.size() : 0;
        }

        private class PicModel {
            String url = null;
            boolean isLoaded = false;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(View itemView) {
                super(itemView);
                ivHolder = (CompatImageViewHolder) itemView;
            }
            private CompatImageViewHolder ivHolder;
        }

        public interface OnAllLoadedCallback {
            void onAllLoaded();
        }
    }
}
