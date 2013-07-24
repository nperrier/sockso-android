package com.pugh.sockso.android.data;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;


public class CoverArtMemoryCache {
    
    private static final String TAG = CoverArtMemoryCache.class.getSimpleName();

    private static final int CACHE_SIZE = 1024 * 1024 * 4; // 4 MB
    
    private LruCache<String, Bitmap> mCache;
    
    
    public CoverArtMemoryCache() {

        mCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
            @Override
            protected int sizeOf(String itemId, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }
    
    public void addCover(String itemId, Bitmap bitmap) {
        
        if ( getCover(itemId) == null && bitmap != null ) {
            mCache.put(itemId, bitmap);
        }
    }
    
    public Bitmap getCover(String itemId) {
        
        return mCache.get(itemId);
    }

    public void clear() {
        mCache.evictAll();
    }
    
}
