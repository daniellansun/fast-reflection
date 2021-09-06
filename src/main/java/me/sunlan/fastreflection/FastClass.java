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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FastClass<T> {
    public static <T> FastClass<T> create(Class<T> clazz) {
        return create(clazz, new FastMemberLoader(Thread.currentThread().getContextClassLoader()));
    }

    public static <T> FastClass<T>  create(Class<T> clazz, ClassDefinable classDefiner) {
        return new FastClass<>(clazz, classDefiner);
    }

    public String getName() {
        return clazz.getName();
    }

    public String getSimpleName() {
        return clazz.getSimpleName();
    }

    public int getModifiers() {
        return clazz.getModifiers();
    }

    public FastMethod getMethod(String name, Class<?> parameterTypes) throws NoSuchMethodException {
        Method m = clazz.getMethod(name, parameterTypes);
        return fastMethodMapCache.computeIfAbsent(m, k -> FastMethod.create(m, classDefiner));
    }

    public FastMethod getDeclaredMethod(String name, Class<?> parameterTypes) throws NoSuchMethodException {
        Method m = clazz.getDeclaredMethod(name, parameterTypes);
        return fastMethodMapCache.computeIfAbsent(m, k -> FastMethod.create(m, classDefiner));
    }

    public FastMethod[] getMethods() {
        Method[] methods = clazz.getMethods();
        return doGetMethods(methods);
    }

    public FastMethod[] getDeclaredMethods() {
        Method[] methods = clazz.getDeclaredMethods();
        return doGetMethods(methods);
    }

    private synchronized FastMethod[] doGetMethods(Method[] methods) {
        FastMethod[] fastMethods = new FastMethod[methods.length];
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            fastMethods[i] = fastMethodMapCache.computeIfAbsent(m, k -> FastMethod.create(m, classDefiner));
        }
        return fastMethods;
    }

    private FastClass(Class<T> clazz, ClassDefinable classDefiner) {
        this.clazz = clazz;
        this.classDefiner = classDefiner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastClass)) return false;
        FastClass<?> fastClass = (FastClass<?>) o;
        return classDefiner == fastClass.classDefiner && clazz.equals(fastClass.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, classDefiner);
    }

    @Override
    public String toString() {
        return clazz.toString();
    }

    private final Class<T> clazz;
    private final ClassDefinable classDefiner;
    private final Map<Method, FastMethod> fastMethodMapCache = new ConcurrentHashMap<>();
}
