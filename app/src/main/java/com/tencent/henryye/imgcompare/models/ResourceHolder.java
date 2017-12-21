package com.tencent.henryye.imgcompare.models;

/**
 * Created by henryye on 2017/12/18.
 *
 *
 */

public class ResourceHolder {
    private static final String TAG = "MicroMsg.ResourceHolder";

    public static String[] assets = {"111.jpg", "20170613.webp", "aaa.jpg", "ccc.jpg", "extremely_large.jpg", "giphy.gif",
            "common_gif.gif", "pony.jpg", "medium.jpg", "sample_0.jpg", "sample_1.jpg", "sample_2.jpg", "sample_3.jpg", "sample_4.jpg", "ex_large.gif"};

    public static String[] sampleAssets = {assets[9], assets[10], assets[11], assets[12], assets[13]};
    // 网络性能比较参考价值不大，会因为网速等因素，导致数据差距很大。而且各家实现方法都是将图片加载到本地-decode文件的方式，没有区别，因此可以忽略
//    public static String[] urls = {"http://img.sc115.com/uploads/sc/jpgs/05/xpic6813_sc115.com.jpg"
//            , "http://pic.sc.chinaz.com/files/pic/pic9/201303/xpic10458.jpg"
//            , "http://www.m555.com/mb_pic/2007/09/20070917093919_6a0709.jpg"
//            , "http://img.1985t.com/uploads/attaches/2017/12/139077-LWZzryD.jpg"
//            , "https://ps.ssl.qhmsg.com/t01eb0e4ef0be64be37.jpg"};
}
