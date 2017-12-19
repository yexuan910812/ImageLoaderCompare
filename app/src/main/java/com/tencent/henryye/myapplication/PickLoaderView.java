package com.tencent.henryye.myapplication;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by henryye on 2017/12/13.
 */

public class PickLoaderView extends LinearLayout {
    private static final String TAG = "MicroMsg.PIckLoaderView";

    private LoaderType mCurrentType = LoaderType.Picasso;

    private Button mStartBtn = null;
    private Button mClearCacheBtn = null;
    private RadioGroup mCurrentSelectedTypeRg = null;
    private TextView mLoadingDuringTv = null;
    private TextView mPerformanceTv = null;

    private boolean isLocked = false;

    private UICallback mUICallback = null;

    private long startTicks = 0;

    public PickLoaderView(Context context) {
        this(context, null);
    }

    public PickLoaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PickLoaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.pick_loader_layout, this);
        mStartBtn = findViewById(R.id.start_load);
        mClearCacheBtn = findViewById(R.id.clean_cache);
        mCurrentSelectedTypeRg = findViewById(R.id.type_rg);
        mLoadingDuringTv = findViewById(R.id.loading_cost);
        mPerformanceTv = findViewById(R.id.performance);

        mStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUICallback == null) {
                    return;
                }
                updateLock(true);
                startTicks = System.currentTimeMillis();
                StatsMonitor.INSTANCE.setContext(getContext());
                StatsMonitor.INSTANCE.requestStartMonitor();
                mUICallback.onRequestStart(mCurrentType);
            }
        });

        mClearCacheBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoaderFactory.INSTANCE.getLoader(mCurrentType).clearAllCache(getContext());
            }
        });
        mCurrentSelectedTypeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d(TAG, "hy: on check changed!");
                switch (i) {
                    case R.id.radio_picasso:
                        mCurrentType = LoaderType.Picasso;
                        break;
                    case R.id.radio_glide:
                        mCurrentType = LoaderType.Glide;
                        break;
                    case R.id.radio_fresco:
                    default:
                        mCurrentType = LoaderType.Fresco;
                }
            }
        });
    }

    private void updateLock(boolean isLocked) {
        Log.d(TAG, "hy: updating is locked! " + isLocked);
        this.isLocked = isLocked;
        if (isLocked) {
            mStartBtn.setEnabled(false);
            mClearCacheBtn.setEnabled(false);
            Utils.setSelectEnabled(mCurrentSelectedTypeRg, false);
            mLoadingDuringTv.setText(getResources().getText(R.string.loading));
            mPerformanceTv.setText(getResources().getText(R.string.loading));
        } else {
            mStartBtn.setEnabled(true);
            mClearCacheBtn.setEnabled(true);
            Utils.setSelectEnabled(mCurrentSelectedTypeRg, true);

            // start fetch info
            mLoadingDuringTv.setText(getResources().getString(R.string.load_cost_in_ms, System.currentTimeMillis() - startTicks));

            CharSequence info = StatsMonitor.INSTANCE.dumpInfo();
            Log.i(TAG, "hy: info: " + info);
            mPerformanceTv.setText(info);
            StatsMonitor.INSTANCE.requestStopMonitor();
        }
    }

    public void onLoadedDone() {
        if (!isLocked) {
            return;
        }
        updateLock(false);
    }

    public void setUICallback(UICallback callback) {
        this.mUICallback = callback;
    }

    public interface UICallback {
        void onRequestStart(LoaderType type);
    }
}
