/**
 * Copyright (c) 2022 "Yukthi Techsoft Pvt. Ltd." (http://yukthitech.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package freemarker.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Strong cache storage is a cache storage that simply wraps a {@link Map}. It holds a strong reference to all objects
 * it was passed, therefore prevents the cache from being purged during garbage collection. This class is always
 * thread-safe since 2.3.24, before that if we are running on Java 5 or later.
 *
 * @see freemarker.template.Configuration#setCacheStorage(CacheStorage)
 */
public class StrongCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
    
    private final Map map = new ConcurrentHashMap();

    /**
     * Always returns {@code true}.
     */
    public boolean isConcurrent() {
        return true;
    }
    
    public Object get(Object key) {
        return map.get(key);
    }

    public void put(Object key, Object value) {
        map.put(key, value);
    }

    public void remove(Object key) {
        map.remove(key);
    }
    
    /**
     * Returns a close approximation of the number of cache entries.
     * 
     * @since 2.3.21
     */
    public int getSize() {
        return map.size();
    }
    
    public void clear() {
        map.clear();
    }
}
