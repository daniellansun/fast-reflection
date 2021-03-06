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

import me.sunlan.fastreflection.generator.FastMethodGenerator;

import java.lang.reflect.Method;
import java.util.Objects;

public abstract class FastMethod extends FastExecutable {
    FastMethod() {
        super(null, null);
        this.method = null;
        this.memberLoader = null;
    }

    protected FastMethod(Method method, MemberLoadable memberLoader) {
        super(method, memberLoader);
        this.method = method;
        this.memberLoader = memberLoader;
    }

    @Override
    public FastClass<?> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    public FastClass<?> getReturnType() {
        return FastClass.create(method.getReturnType(), memberLoader);
    }

    public abstract Object invoke(Object obj, Object... args) throws Throwable;

    public static FastMethod create(Method method) {
        return create(method, false);
    }

    public static FastMethod create(Method method, boolean toSetAccessible) {
        return create(method, FastMemberLoader.getDefaultLoader(), toSetAccessible);
    }

    public static FastMethod create(Method method, MemberLoadable memberLoader, boolean toSetAccessible) {
        return memberLoader.load(FastMethodGenerator.INSTANCE.generate(method, toSetAccessible));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastMethod)) return false;
        FastMethod that = (FastMethod) o;
        return method.equals(that.method) && declaringClass.equals(that.declaringClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, declaringClass);
    }

    @Override
    public String toString() {
        return method.toString();
    }

    private final Method method;
    private final MemberLoadable memberLoader;
}
