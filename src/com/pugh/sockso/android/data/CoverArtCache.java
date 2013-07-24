package com.pugh.sockso.android.data;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

// Singleton
public class CoverArtCache {

    private static final String TAG = CoverArtCache.class.getSimpleName();

    private CoverArtFileCache mFileCache;
    private CoverArtMemoryCache mMemCache;

    private static final String CACHE_DIR_NAME = "cover_art";
    private static final long CACHE_SIZE = 1024 * 1024 * 10; // 10 MB

    private static CoverArtCache instance = null;

    public static CoverArtCache getInstance(Context context) {

        if (instance == null) {
            instance = new CoverArtCache(context);
        }

        return instance;
    }

    private CoverArtCache(Context context) {

        this.mMemCache = new CoverArtMemoryCache();

        File cacheDir = CoverArtFileCache.getDiskCacheDir(context, CACHE_DIR_NAME);
        this.mFileCache = CoverArtFileCache.openCache(context, cacheDir, CACHE_SIZE);
    }

    public Bitmap getCover(String itemId) {

        // Get from memory cache.
        if (mMemCache != null) {

            final Bitmap bitmap = mMemCache.getCover(itemId);

            if (bitmap != null) {
                Log.d(TAG, "Memory cache hit: " + itemId);
                return bitmap;
            }
        }

        // Get from disk cache.
        if (mFileCache != null) {

            Log.d(TAG, "File cache hit: " + itemId);
            final Bitmap image = mFileCache.get(itemId);
            // Add it to memory cache:
            mMemCache.addCover(itemId, image);

            return image;
        }

        return null;
    }

    public void addCover(String itemId, Bitmap bitmap) {

        if (itemId == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        if (mMemCache != null && mMemCache.getCover(itemId) == null) {
            mMemCache.addCover(itemId, bitmap);
        }

        // Add to disk cache
        if (mFileCache != null && !mFileCache.containsKey(itemId)) {
            mFileCache.put(itemId, bitmap);
        }
    }

    public void clearCaches() {

        mFileCache.clearCache();
        mMemCache.clear();
    }

}
