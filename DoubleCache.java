package com.lxy.iamgeloader;

import android.graphics.Bitmap;

/**
 * Created by lxy on 2015/12/3.
 * double cache(MemoryCache DiskCache)
 */
public class DoubleCache implements ImageCache{
    
    MemoryCache memoryCache;
    DiskCache diskCache;
    
    public DoubleCache(){
        memoryCache = new MemoryCache();
        diskCache = new DiskCache();
    }
    
    
    @Override
    public void put(String url, Bitmap bitmap) {
        memoryCache.put(url,bitmap);
        diskCache.put(url,bitmap);
    }

    @Override
    public Bitmap get(String url) {
        Bitmap bitmap = memoryCache.get(url);
        if (bitmap == null){
            bitmap = diskCache.get(url);
        }
        return bitmap;
    }
}
