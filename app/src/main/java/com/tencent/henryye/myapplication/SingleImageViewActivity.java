package com.tencent.henryye.myapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;

@SuppressLint("LongLogTag")
public class SingleImageViewActivity extends AppCompatActivity implements PickLoaderView.UICallback {
    private static final String TAG = "MicroMsg.SingleImageViewActivity";

    private PickLoaderView mPickLoaderView = null;
    private ImageView mLargeImageView = null;
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

            }
        });
    }

    @Override
    public void onRequestStart(LoaderType type) {
        LoaderFactory.INSTANCE.getLoader(type).fillingWithUri(this, mLargeImageView, Utils.getUriFromAssets("extremely_large.png"), new LoaderFactory.IOnLoadCallback() {
            @Override
            public void onLoad(boolean isSuc) {
                Log.i(TAG, "hy: is load succ: " + isSuc);
                mPickLoaderView.onLoadedDone();
            }
        });
    }

    private void updateSeletected(int id) {
        switch (id) {
            case R.id.medium_image:
                selectedAssets = ResourceHolder.assets[];
        }
    }
}
