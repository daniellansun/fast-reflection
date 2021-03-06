/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package me.sunlan.fastreflection;

import java.lang.reflect.Executable;
import java.util.Arrays;

public abstract class FastExecutable implements FastMember {
    private final Executable executable;
    private final MemberLoadable memberLoader;
    protected final FastClass<?> declaringClass;

    protected FastExecutable(Executable executable, MemberLoadable memberLoader) {
        this.executable = executable;
        this.memberLoader = memberLoader;
        this.declaringClass = null == executable
                                ? null : FastClass.create(executable.getDeclaringClass(), memberLoader);
    }

    public boolean isVarArgs() {
        return executable.isVarArgs();
    }

    public FastClass<?>[] getParameterTypes() {
        return Arrays.stream(executable.getParameterTypes())
                .map(pt -> FastClass.create(pt, memberLoader))
                .toArray(FastClass[]::new);
    }
}
