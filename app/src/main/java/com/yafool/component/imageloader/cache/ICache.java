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

/**
 * @Package: com.yafool.component.imageloader.cache
 * @ClassName: com.yafool.component.imageloader.cache.ICache.java
 * @Description: TODO
 * @CreateDate: 2019/4/25 6:44 PM
 * @Author: yafool
 * @Email: yafool@icloud.com
 * @UpdateUser: yafool
 * @UpdateDate: 2019/4/25 6:44 PM
 */
public interface ICache<K, V> {
    /**
     * Gets an value for the specified {@code key} or return {@code null}.
     *
     * @param key key
     * @return the value or {@code null}.
     */
    V get(K key);

    /**
     * Puts an value in the cache for the specified {@code key}.
     *
     * @param key   key
     * @param value image
     * @return the previous value.
     */
    V put(K key, V value);

    /**
     * Removes the entry for {@code key} if it exists or return {@code null}.
     *
     * @return the previous value or @{code null}.
     */
    V remove(K key);

    /**
     * Clears all the entries in the cache.
     */
    void clear();

    /**
     * Returns the max memory size of the cache.
     *
     * @return max memory size.
     */
    int getMaxMemorySize();

    /**
     * Returns the current memory size of the cache.
     *
     * @return current memory size.
     */
    int getMemorySize();
}
