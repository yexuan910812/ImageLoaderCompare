package com.tencent.henryye.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by henryye on 2017/12/13.
 */
@SuppressLint("LongLogTag")
public enum LoaderFactory {
    INSTANCE;

    private static final String TAG = "MicroMsg.LoaderFactory";

    public interface ILoader {
        void init(Context context);

        void fillingWithUri(Context context, ImageView iv, Uri uri, IOnLoadCallback callback);

        void cancelFilling(Context context, ImageView iv);

        void clearAllCache(Context context);
    }

    public interface IOnLoadCallback {
        void onLoad(boolean isSuc);
    }

    public ILoader getLoader(LoaderType type) {
        switch (type) {
            case Picasso:
                return PicassoLoader.newInstance();
            case Glide:
                return GlideLoader.newInstance();
            case Fresco:
            default:
                return FrescoLoader.newInstance();
        }
    }

    private static class PicassoLoader implements ILoader {
        private static final String TAG = "MicroMsg.LoaderFactory.PicassoLoader";

        private volatile static ILoader sInstance = null;

        private static ILoader newInstance() {
            if (sInstance != null) {
                return sInstance;
            } else {
                synchronized (PicassoLoader.class) {
                    if (sInstance == null) {
                        sInstance = new PicassoLoader();
                    }
                    return sInstance;
                }
            }
        }

        @Override
        public void init(Context context) {

        }

        @Override
        public void fillingWithUri(Context context, ImageView iv, final Uri uri, final IOnLoadCallback callback) {
            Picasso.with(context).load(uri).into(iv, new Callback() {
                @Override
                public void onSuccess() {
                    Log.v(TAG, "img: load uri: " + uri + " success");
                    callback.onLoad(true);
                }

                @Override
                public void onError() {
                    Log.v(TAG, "img: load uri: " + uri + " error");
                    callback.onLoad(false);
                }
            });
        }

        @Override
        public void cancelFilling(Context context, ImageView iv) {
            Picasso.with(context).cancelRequest(iv);
        }

        @Override
        public void clearAllCache(Context context) {
            Picasso.with(context).shutdown();
        }
    }

    private static class GlideLoader implements ILoader {
        private static final String TAG = "MicroMsg.LoaderFactory.GlideLoader";

        private volatile static ILoader sInstance = null;

        private static ILoader newInstance() {
            if (sInstance != null) {
                return sInstance;
            } else {
                synchronized (PicassoLoader.class) {
                    if (sInstance == null) {
                        sInstance = new GlideLoader();
                    }
                    return sInstance;
                }
            }
        }

        @Override
        public void init(Context context) {
        }

        @Override
        public void fillingWithUri(Context context, ImageView iv, final Uri uri, final IOnLoadCallback callback) {
            Glide.with(context).load(uri).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.v(TAG, "img: load uri: " + uri + " error");
                    callback.onLoad(false);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.v(TAG, "img: load uri: " + uri + " success");
                    callback.onLoad(true);
                    return false;
                }
            }).into(iv);
        }

        @Override
        public void cancelFilling(Context context, ImageView iv) {
            Glide.with(context).clear(iv);
        }

        @Override
        public void clearAllCache(final Context context) {
            // must in main
            Glide.get(context).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // must in background
                    Glide.get(context).clearDiskCache();
                }
            }).start();
        }
    }

    private static class FrescoLoader implements ILoader {
        private static final String TAG = "MicroMsg.LoaderFactory.FrescoLoader";

        private volatile static ILoader sInstance = null;

        private static ILoader newInstance() {
            if (sInstance != null) {
                return sInstance;
            } else {
                synchronized (PicassoLoader.class) {
                    if (sInstance == null) {
                        sInstance = new FrescoLoader();
                    }
                    return sInstance;
                }
            }
        }

        @Override
        public void init(Context context) {

        }

        @Override
        public void fillingWithUri(Context context, ImageView iv, Uri uri, IOnLoadCallback callback) {

        }

        @Override
        public void cancelFilling(Context context, ImageView iv) {

        }

        @Override
        public void clearAllCache(Context context) {

        }
    }
}
