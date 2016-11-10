/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.tasks.cache.config;

import org.gradle.StartParameter;
import org.gradle.api.internal.tasks.cache.LocalDirectoryTaskOutputCache;
import org.gradle.api.internal.tasks.cache.TaskOutputCache;
import org.gradle.api.internal.tasks.cache.TaskOutputCacheFactory;
import org.gradle.cache.CacheRepository;

import java.io.File;

public class DefaultTaskCaching implements TaskCachingInternal {
    private final boolean pullAllowed;
    private final boolean pushAllowed;
    private final CacheRepository cacheRepository;
    private TaskOutputCacheFactory factory;

    public DefaultTaskCaching(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
        useLocalCache();
        this.pullAllowed = "true".equalsIgnoreCase(System.getProperty("org.gradle.cache.tasks.pull", "true").trim());
        this.pushAllowed = "true".equalsIgnoreCase(System.getProperty("org.gradle.cache.tasks.push", "true").trim());
    }

    @Override
    public void useLocalCache() {
        this.factory = new TaskOutputCacheFactory() {
            @Override
            public TaskOutputCache createCache(StartParameter startParameter) {
                String cacheDirectoryPath = System.getProperty("org.gradle.cache.tasks.directory");
                return cacheDirectoryPath != null
                    ? new LocalDirectoryTaskOutputCache(cacheRepository, new File(cacheDirectoryPath))
                    : new LocalDirectoryTaskOutputCache(cacheRepository, "task-cache");
            }
        };
    }

    @Override
    public void useLocalCache(final File directory) {
        this.factory = new TaskOutputCacheFactory() {
            @Override
            public TaskOutputCache createCache(StartParameter startParameter) {
                return new LocalDirectoryTaskOutputCache(cacheRepository, directory);
            }
        };
    }

    @Override
    public void useCacheFactory(TaskOutputCacheFactory factory) {
        this.factory = factory;
    }

    @Override
    public TaskOutputCacheFactory getCacheFactory() {
        return factory;
    }

    @Override
    public boolean isPullAllowed() {
        return pullAllowed;
    }

    @Override
    public boolean isPushAllowed() {
        return pushAllowed;
    }
}
