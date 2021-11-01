/*
 *  Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.huawei.hms.videoeditor.codelab.ui.common.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.huawei.hms.videoeditor.codelab.ui.common.bean.ThumbInfo;

public class ThumbNailMemoryCache {
    private static final int MEMORY_CACHE_SIZE = 1024 * 1024 * 5;

    private LruCache<String, ThumbInfo> memoryCache;

    private ThumbNailMemoryCache() {
    }

    private static class ThumbNailCacheHolder {
        static ThumbNailMemoryCache singleton = new ThumbNailMemoryCache();
    }

    public static ThumbNailMemoryCache getInstance() {
        return ThumbNailCacheHolder.singleton;
    }

    public synchronized void init() {
        if (memoryCache != null) {
            memoryCache.evictAll();
            memoryCache = null;
        }
        memoryCache = new ThumbLruCache(MEMORY_CACHE_SIZE);
    }

    public synchronized void recycler() {
        if (memoryCache != null && memoryCache.size() > 0 && memoryCache.snapshot() != null
            && !memoryCache.snapshot().isEmpty()) {
            memoryCache.evictAll();
            memoryCache = null;
        }
    }

    private static class ThumbLruCache extends LruCache<String, ThumbInfo> {
        /**
         * @param maxSize for caches that do not override {@link #sizeOf}, this is
         *        the maximum number of entries in the cache. For all other caches,
         *        this is the maximum sum of the sizes of the entries in this cache.
         */
        ThumbLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, ThumbInfo value) {
            if (null != value) {
                Bitmap bmp = value.getBitmap();
                if (null != bmp && !bmp.isRecycled()) {
                    return bmp.getByteCount();
                }
            }
            return 0;
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, ThumbInfo oldValue, ThumbInfo newValue) {
            super.entryRemoved(evicted, key, oldValue, newValue);
            if (null != oldValue) {
                oldValue.recycle();
            }
        }
    }
}
