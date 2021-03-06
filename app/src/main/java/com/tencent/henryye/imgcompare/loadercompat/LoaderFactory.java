package com.tencent.henryye.imgcompare.loadercompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.common.references.CloseableReference;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
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

        void fillingWithUri(Context context, CompatImageViewHolder ivHolder, Uri uri, IOnLoadCallback callback, boolean isAnimated);

        void cancelFilling(Context context, CompatImageViewHolder ivHolder);

        void clearAllCache(Context context);
    }

    public interface IOnLoadCallback {
        void onLoad(boolean isSuc);
    }

    public ILoader getLoader(LoaderType type) {
        switch (type) {
            case Picasso:
                return PicassoLoader.getInstance();
            case Glide:
                return GlideLoader.getInstance();
            case Fresco:
            default:
                return FrescoLoader.getInstance();
        }
    }

    public void initAll(Context context) {
        PicassoLoader.getInstance().init(context);
        GlideLoader.getInstance().init(context);
        FrescoLoader.getInstance().init(context);
    }

    private static class PicassoLoader implements ILoader {
        private static final String TAG = "MicroMsg.LoaderFactory.PicassoLoader";

        private volatile static ILoader sInstance = null;

        private static ILoader getInstance() {
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
        public void fillingWithUri(Context context, CompatImageViewHolder ivHolder, final Uri uri, final IOnLoadCallback callback, boolean isAnimated) {
            ImageView iv = ivHolder.switchToLegend();
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
        public void cancelFilling(Context context, CompatImageViewHolder ivHolder) {
            Picasso.with(context).cancelRequest(ivHolder.switchToLegend());
        }

        @Override
        public void clearAllCache(Context context) {
            Picasso.with(context).shutdown();
        }
    }

    private static class GlideLoader implements ILoader {
        private static final String TAG = "MicroMsg.LoaderFactory.GlideLoader";

        private volatile static ILoader sInstance = null;

        private static ILoader getInstance() {
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
        public void fillingWithUri(Context context, CompatImageViewHolder ivHolder, final Uri uri, final IOnLoadCallback callback, boolean isAnimated) {
            ImageView iv = ivHolder.switchToLegend();
            RequestManager requestManager = Glide.with(context);
            RequestBuilder requestBuilder;
            if(isAnimated) {
                requestBuilder = requestManager.asGif();
            } else {
                requestBuilder = requestManager.asBitmap();
            }
            requestBuilder.load(uri).listener(new RequestListener<Object>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Object> target, boolean isFirstResource) {
                    Log.v(TAG, "img: load uri: " + uri + " error");
                    callback.onLoad(false);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target<Object> target, DataSource dataSource, boolean isFirstResource) {
                    Log.v(TAG, "img: load uri: " + uri + " success");
                    callback.onLoad(true);
                    return false;
                }
            }).into(iv);
        }

        @Override
        public void cancelFilling(Context context, CompatImageViewHolder ivHolder) {
            Glide.with(context).clear(ivHolder.switchToLegend());
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

        private static ILoader getInstance() {
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
            Fresco.initialize(context);
        }

        @Override
        public void fillingWithUri(Context context, CompatImageViewHolder ivHolder, Uri uri, final IOnLoadCallback callback, boolean isAnimated) {
            SimpleDraweeView iv = ivHolder.switchToFresco();

            ImageRequestBuilder imageRequestBuilder =
                    ImageRequestBuilder.newBuilderWithSource(uri);
//            imageRequestBuilder.setResizeOptions(new ResizeOptions(
//                    iv.getLayoutParams().width,
//                    iv.getLayoutParams().height));
            ImageRequest request = imageRequestBuilder.build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(iv.getController())
                    .setControllerListener(new BaseControllerListener<ImageInfo>(){
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            Log.i(TAG, "hy: load succ!");
                            callback.onLoad(true);
                        }

                        @Override
                        public void onFailure(String id, Throwable throwable) {
                            super.onFailure(id, throwable);
                            Log.w(TAG, "hy: load failed!");
                            callback.onLoad(false);
                        }
                    })
                    .setAutoPlayAnimations(isAnimated)
                    .build();
            iv.setController(draweeController);
            iv.setTag(request);
        }

        @Override
        public void cancelFilling(Context context, CompatImageViewHolder ivHolder) {
            Object tag = ivHolder.switchToFresco().getTag();
            if(tag != null && tag instanceof ImageRequest) {
                ImagePipeline pipeline = Fresco.getImagePipeline();
                com.facebook.datasource.DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage((ImageRequest) ivHolder.getTag(), context);
                if(dataSource != null) {
                    dataSource.close();
                }
            }

        }

        @Override
        public void clearAllCache(Context context) {
            Fresco.getImagePipeline().clearCaches();
        }
    }
}
