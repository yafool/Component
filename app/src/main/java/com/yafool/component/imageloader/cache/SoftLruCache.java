/*
 *   Copyright (C) 2019 yafool Individual developer
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.yafool.component.imageloader.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Package: com.yafool.component.imageloader.cache
 * @ClassName: com.yafool.component.imageloader.cache.SoftLruCache.java
 * @Description: TODO
 * @CreateDate: 2019/4/26 1:07 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/26 1:07 PM
 */
public abstract class SoftLruCache<K, V> implements ICache<K, V> {

    private final LinkedHashMap<K, SoftReference<V>> map;

    private final Map<K, Integer> previousMap;

    private final int maxMemorySize;

    private int memorySize;

    public SoftLruCache() {
        this(CacheConstant.Default.CAPACITY);
    }

    public SoftLruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity <= 0");
        }

        maxMemorySize = capacity * 1024 * 1024;
        map = new LinkedHashMap<K, SoftReference<V>>(0, 0.75f, true);
        previousMap = new HashMap<K, Integer>();
    }

    @Override
    public final V get(K key) {
        Objects.requireNonNull(key, "key == null");

        V value = null;
        synchronized (this) {
            SoftReference<V> reference = map.get(key);

            if (null != reference) {
                value = reference.get();
                if (null == value){
                    memorySize -= previousMap.get(key);
                    map.remove(key);
                    previousMap.remove(key);
                }
            }
        }
        return value;
    }

    @Override
    public final V put(K key, V value) {
        Objects.requireNonNull(key, "key == null");
        Objects.requireNonNull(value, "value == null");

        int size = sizeof(value);
        SoftReference<V> previous;
        synchronized (this) {
            previous = map.put(key, new SoftReference<V>(value));
            memorySize += size;
            if (previous != null) {
                memorySize -= previousMap.get(key);
            }
            previousMap.put(key, size);
            trimToSize(maxMemorySize);
        }
        return value;
    }

    @Override
    public final V remove(K key) {
        Objects.requireNonNull(key, "key == null");
        SoftReference<V> previous;
        V value = null;
        synchronized (this) {
            previous = map.remove(key);
            if (previous != null) {
                value = previous.get();
                memorySize -= previousMap.get(key);
            }
        }
        return value;
    }

    @Override
    public synchronized final void clear() {
        trimToSize(CacheConstant.Default.REMOVE_ALL);
    }

    @Override
    public synchronized final int getMaxMemorySize() {
        return maxMemorySize;
    }

    @Override
    public synchronized final int getMemorySize() {
        return memorySize;
    }


    private void trimToSize(int maxSize) {
        while (true) {
            if (memorySize <= maxSize || map.isEmpty()) {
                break;
            }
            if (memorySize < 0 || (map.isEmpty() && memorySize != 0)) {
                throw new IllegalStateException(className() + ".getValueSize() is reporting inconsistent results");
            }
            LinkedHashMap.Entry<K, SoftReference<V>> toRemove = map.entrySet().iterator().next();
            K key = toRemove.getKey();
            memorySize -= previousMap.get(key);
            map.remove(key);
            previousMap.remove(key);
        }
    }

    /**
     * mark ------- abstract function
     */
    public abstract int sizeof(V value);
    public abstract String className();
}
