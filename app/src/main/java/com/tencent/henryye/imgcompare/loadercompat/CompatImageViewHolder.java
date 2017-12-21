package com.tencent.henryye.imgcompare.loadercompat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by henryye on 2017/12/19.
 */

public class CompatImageViewHolder extends FrameLayout {
    private static final String TAG = "MicroMsg.CompatImageViewHolder";

    private ImageView legendIv = null;
    private SimpleDraweeView frescoIv = null;

    public CompatImageViewHolder(@NonNull Context context) {
        this(context, null);
    }

    public CompatImageViewHolder(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CompatImageViewHolder(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        legendIv = new ImageView(getContext());
        legendIv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(legendIv);
        frescoIv = new SimpleDraweeView(getContext());
        frescoIv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(frescoIv);
    }

    public ImageView switchToLegend() {
        legendIv.setVisibility(View.VISIBLE);
        frescoIv.setVisibility(View.GONE);
        return legendIv;
    }

    public SimpleDraweeView switchToFresco() {
        legendIv.setVisibility(View.GONE);
        frescoIv.setVisibility(View.VISIBLE);
        return frescoIv;
    }

}
