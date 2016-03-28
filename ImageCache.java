package com.lxy.iamgeloader;

import android.graphics.Bitmap;

/**
 * Created by lxy on 2015/12/3.
 * interface to the ImageLoader's cache
 */
public interface ImageCache {
    public void put(String url , Bitmap bitmap);
    public Bitmap get(String url);
}
