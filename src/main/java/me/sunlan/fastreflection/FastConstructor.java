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

import me.sunlan.fastreflection.generator.FastConstructorGenerator;

import java.lang.reflect.Constructor;
import java.util.Objects;

public abstract class FastConstructor<T> extends FastExecutable {
    FastConstructor() {
        super(null, null);
        this.constructor = null;
        this.memberLoader = null;
        this.declaringClass = null;
    }

    public FastConstructor(Constructor<T> constructor, MemberLoadable memberLoader) {
        super(constructor, memberLoader);
        this.constructor = constructor;
        this.memberLoader = memberLoader;
        this.declaringClass = FastClass.create(constructor.getDeclaringClass(), memberLoader);
    }

    public abstract Object invoke(Object... args) throws Throwable;

    public static <T> FastConstructor<T> create(Constructor<T> constructor) {
        return create(constructor, FastMemberLoader.getDefaultLoader());
    }

    public static <T> FastConstructor<T> create(Constructor<T> constructor, MemberLoadable memberLoader) {
        return memberLoader.load(FastConstructorGenerator.INSTANCE.generate(constructor));
    }

    @Override
    public FastClass<T> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String getName() {
        return constructor.getName();
    }

    @Override
    public int getModifiers() {
        return constructor.getModifiers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastConstructor)) return false;
        FastConstructor<?> that = (FastConstructor<?>) o;
        return constructor.equals(that.constructor) && declaringClass.equals(that.declaringClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, declaringClass);
    }

    @Override
    public String toString() {
        return constructor.toString();
    }

    private final Constructor<T> constructor;
    private final MemberLoadable memberLoader;
    private final FastClass<T> declaringClass;
}
