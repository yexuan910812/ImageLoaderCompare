package com.tencent.henryye.imgcompare.testui;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;

import com.tencent.henryye.imgcompare.R;
import com.tencent.henryye.imgcompare.models.ResourceHolder;
import com.tencent.henryye.imgcompare.models.Utils;
import com.tencent.henryye.imgcompare.loadercompat.CompatImageViewHolder;
import com.tencent.henryye.imgcompare.loadercompat.LoaderFactory;
import com.tencent.henryye.imgcompare.loadercompat.LoaderType;

@SuppressLint("LongLogTag")
public class SingleImageViewActivity extends AppCompatActivity implements PickLoaderView.UICallback {
    private static final String TAG = "MicroMsg.SingleImageViewActivity";

    private PickLoaderView mPickLoaderView = null;
    private CompatImageViewHolder mLargeImageView = null; // 本质上也是ImageView，可以混用
    private RadioGroup mPicSelectRg = null;
    private String selectedAssets = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image_view);

        mPickLoaderView = findViewById(R.id.pick_loader_view);
        mLargeImageView = findViewById(R.id.image_view);
        mPicSelectRg = findViewById(R.id.select_single_image);

        mPickLoaderView.setUICallback(this);

        mPicSelectRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                updateSeletected(i);
            }
        });
        updateSeletected(R.id.medium_image);
    }

    @Override
    public void onRequestStart(LoaderType type) {
        Utils.setSelectEnabled(mPicSelectRg, false);
        LoaderFactory.INSTANCE.getLoader(type).fillingWithUri(this, mLargeImageView, Utils.getUriFromAssets(selectedAssets, type), new LoaderFactory.IOnLoadCallback() {
            @Override
            public void onLoad(boolean isSuc) {
                Log.i(TAG, "hy: is load succ: " + isSuc);
                mPickLoaderView.onLoadedDone();
                Utils.setSelectEnabled(mPicSelectRg, true);
            }
        }, false);
    }

    private void updateSeletected(int id) {
        switch (id) {
            case R.id.medium_image:
                selectedAssets = ResourceHolder.assets[8];
                break;
            case R.id.large_image:
                selectedAssets = ResourceHolder.assets[7];
                break;
            case R.id.extremely_large_image:
                selectedAssets = ResourceHolder.assets[4];
                break;
        }
    }
}
