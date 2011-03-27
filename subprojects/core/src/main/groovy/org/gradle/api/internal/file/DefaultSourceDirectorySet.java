/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.internal.file;

import groovy.lang.Closure;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.collections.DefaultConfigurableFileCollection;
import org.gradle.api.internal.file.collections.DirectoryFileTree;
import org.gradle.api.internal.file.collections.FileCollectionResolveContext;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultSourceDirectorySet extends CompositeFileTree implements SourceDirectorySet {
    private final DefaultConfigurableFileCollection srcDirs;
    private final String displayName;
    private final PatternSet patterns = new PatternSet();
    private final PatternSet filter = new PatternSet();

    public DefaultSourceDirectorySet(String displayName, FileResolver fileResolver) {
        this.displayName = displayName;
        srcDirs = new DefaultConfigurableFileCollection(fileResolver, null);
    }

    public Set<File> getSrcDirs() {
        return srcDirs.getFiles();
    }

    public Set<String> getIncludes() {
        return patterns.getIncludes();
    }

    public Set<String> getExcludes() {
        return patterns.getExcludes();
    }

    public PatternFilterable setIncludes(Iterable<String> includes) {
        patterns.setIncludes(includes);
        return this;
    }

    public PatternFilterable setExcludes(Iterable<String> excludes) {
        patterns.setExcludes(excludes);
        return this;
    }

    public PatternFilterable include(String... includes) {
        patterns.include(includes);
        return this;
    }

    public PatternFilterable include(Iterable<String> includes) {
        patterns.include(includes);
        return this;
    }

    public PatternFilterable include(Spec<FileTreeElement> includeSpec) {
        patterns.include(includeSpec);
        return this;
    }

    public PatternFilterable include(Closure includeSpec) {
        patterns.include(includeSpec);
        return this;
    }

    public PatternFilterable exclude(Iterable<String> excludes) {
        patterns.exclude(excludes);
        return this;
    }

    public PatternFilterable exclude(String... excludes) {
        patterns.exclude(excludes);
        return this;
    }

    public PatternFilterable exclude(Spec<FileTreeElement> excludeSpec) {
        patterns.exclude(excludeSpec);
        return this;
    }

    public PatternFilterable exclude(Closure excludeSpec) {
        patterns.exclude(excludeSpec);
        return this;
    }

    public PatternFilterable getFilter() {
        return filter;
    }

    @Override
    public void resolve(FileCollectionResolveContext context) {
        for (File sourceDir : getExistingSourceDirs()) {
            context.add(new DirectoryFileTree(sourceDir, patterns).filter(filter));
        }
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    protected Set<File> getExistingSourceDirs() {
        Set<File> existingSourceDirs = new LinkedHashSet<File>();
        for (File srcDir : srcDirs) {
            if (srcDir.isDirectory()) {
                existingSourceDirs.add(srcDir);
            } else if (srcDir.exists()) {
                throw new InvalidUserDataException(String.format("Source directory '%s' is not a directory.", srcDir));
            }
        }
        return existingSourceDirs;
    }

    public SourceDirectorySet srcDir(Object srcDir) {
        srcDirs.from(srcDir);
        return this;
    }

    public SourceDirectorySet srcDirs(Object... srcDirs) {
        this.srcDirs.from(srcDirs);
        return this;
    }

    public SourceDirectorySet setSrcDirs(Iterable<Object> srcPaths) {
        srcDirs.clear();
        srcDirs.from(srcPaths);
        return this;
    }
}
