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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FastClass<T> {
    public static <T> FastClass<T> create(Class<T> clazz) {
        return create(clazz, new FastMemberLoader());
    }

    public static <T> FastClass<T>  create(Class<T> clazz, MemberLoadable memberLoader) {
        return new FastClass<>(clazz, memberLoader);
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
        Method method = clazz.getMethod(name, parameterTypes);
        return getOrCreateMethod(method);
    }

    public FastMethod getDeclaredMethod(String name, Class<?> parameterTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        return getOrCreateMethod(method);
    }

    public FastMethod[] getMethods() {
        Method[] methods = clazz.getMethods();
        return doGetMethods(methods);
    }

    public FastMethod[] getDeclaredMethods() {
        Method[] methods = clazz.getDeclaredMethods();
        return doGetMethods(methods);
    }

    public FastConstructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<T> constructor = clazz.getConstructor(parameterTypes);
        return getOrCreateConstructor(constructor);
    }

    public FastConstructor<T> getDeclaredConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
        return getOrCreateConstructor(constructor);
    }

    public FastConstructor<?>[] getConstructors() {
        Constructor[] constructors = clazz.getConstructors();
        return doGetConstructors(constructors);
    }

    public FastConstructor<?>[] getDeclaredConstructors() {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        return doGetConstructors(constructors);
    }

    public FastField getField(String name) throws NoSuchFieldException {
        Field field = clazz.getField(name);
        return getOrCreateField(field);
    }

    public FastField getDeclaredField(String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        return getOrCreateField(field);
    }

    public FastField[] getFields() {
        Field[] fields = clazz.getFields();
        return doGetFields(fields);
    }

    public FastField[] getDeclaredFields() {
        Field[] fields = clazz.getDeclaredFields();
        return doGetFields(fields);
    }

    public Class<T> getRawClass() {
        return clazz;
    }

    private synchronized FastMethod[] doGetMethods(Method[] methods) {
        return Arrays.stream(methods)
                .map(method -> new LazyFastMethod(() -> getOrCreateMethod(method)))
                .toArray(FastMethod[]::new);
    }

    private FastConstructor<T>[] doGetConstructors(Constructor<T>[] constructors) {
        return Arrays.stream(constructors)
                .map(constructor -> new LazyFastConstructor<>(() -> getOrCreateConstructor(constructor)))
                .toArray(FastConstructor[]::new);
    }

    private FastField[] doGetFields(Field[] fields) {
        return Arrays.stream(fields)
                .map(field -> new LazyFastField(() -> getOrCreateField(field)))
                .toArray(FastField[]::new);
    }

    private FastConstructor<T> getOrCreateConstructor(Constructor<T> constructor) {
        return (FastConstructor<T>) getOrCreate(constructor, m -> FastConstructor.create((Constructor<?>) m, memberLoader));
    }

    private FastField getOrCreateField(Field field) {
        return (FastField) getOrCreate(field, m -> FastField.create((Field) m, memberLoader));
    }

    private FastMethod getOrCreateMethod(Method method) {
        return (FastMethod) getOrCreate(method, m -> FastMethod.create((Method) m, memberLoader));
    }

    private FastMember getOrCreate(Member m, Function<? super Member, ? extends FastMember> factory) {
        return fastMemberCache.computeIfAbsent(m, factory);
    }

    private FastClass(Class<T> clazz, MemberLoadable memberLoader) {
        this.clazz = clazz;
        this.memberLoader = memberLoader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastClass)) return false;
        FastClass<?> fastClass = (FastClass<?>) o;
        return memberLoader == fastClass.memberLoader && clazz.equals(fastClass.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, memberLoader);
    }

    @Override
    public String toString() {
        return clazz.toString();
    }

    private final Class<T> clazz;
    private final MemberLoadable memberLoader;
    private final Map<Member, FastMember> fastMemberCache = new ConcurrentHashMap<>();
}
