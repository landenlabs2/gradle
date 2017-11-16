/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.integtests.fixtures.publish

import org.gradle.test.fixtures.HttpRepository

class RemoteRepositorySpec {
    public static final ThreadLocal<Boolean> DEFINES_INTERACTIONS = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            false
        }
    }

    final Map<String, GroupSpec> groups = [:].withDefault { new GroupSpec(it) }

    void nextStep() {
        groups.clear()
    }

    void group(String group, @DelegatesTo(value=GroupSpec, strategy = Closure.DELEGATE_ONLY) Closure<Void> groupSpec) {
        groupSpec.delegate = groups[group]
        groupSpec.resolveStrategy = Closure.DELEGATE_ONLY
        groupSpec()
    }

    void build(HttpRepository repository) {
        groups.values()*.build(repository)
    }

    void methodMissing(String name, args) {
        def (gid, aid, v) = name.split(':') as List
        Closure spec = {}
        if (args && args.length == 1 && args[0] instanceof Closure) {
            spec = args[0]
        }
        if (gid && aid) {
            group(gid) {
                if (v) {
                    module(aid) {
                        version(v, spec)
                    }
                } else {
                    module(aid, spec)
                }
            }
        } else if (gid) {
            group(gid, spec)
        }

    }
}
