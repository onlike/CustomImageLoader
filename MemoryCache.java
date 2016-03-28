package com.lxy.iamgeloader;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by lxy on 2015/12/3.
 * application memory cache
 */
public class MemoryCache implements ImageCache {
	// 创建static变量类型确保只有在进程被杀死的时候才释放缓存资源
	static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); // 获取最大可用缓存
	static final int mMemory = maxMemory / 4;
    public static LruCache<String, Bitmap> sLruCache = new LruCache<String, Bitmap>(mMemory) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };;

    @Override
    public void put(String url, Bitmap bitmap) {
    	 sLruCache.put(url, bitmap);
    }

    @Override
    public Bitmap get(String url) {
        return sLruCache.get(url);
    }

}
