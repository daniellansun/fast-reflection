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

import me.sunlan.fastreflection.generator.ClassData;
import me.sunlan.fastreflection.generator.FastMethodGenerator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public abstract class FastMethod implements FastMember {

    FastMethod() {
        this.method = null;
        this.declaringClass = null;
        this.memberLoader = null;
    }

    public FastMethod(Method method, MemberLoadable memberLoader) {
        this.method = method;
        this.memberLoader = memberLoader;
        this.declaringClass = FastClass.create(method.getDeclaringClass(), memberLoader);
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

    public FastClass<?>[] getParameterTypes() {
        return Arrays.stream(method.getParameterTypes())
                .map(pt -> FastClass.create(pt, memberLoader))
                .toArray(FastClass[]::new);
    }

    public abstract Object invoke(Object obj, Object... args) throws Throwable;

    public static FastMethod create(Method method) {
        return create(method, new FastMemberLoader());
    }

    public static FastMethod create(Method method, MemberLoadable memberLoader) {
//        long b = System.currentTimeMillis();
        ClassData classData = FastMethodGenerator.INSTANCE.generate(method);
//        long m = System.currentTimeMillis();
//        System.out.println("gen: " + (m - b) + "ms");

        Class<?> fastMethodClass = memberLoader.load(classData.getName(), classData.getBytes());
//        long end = System.currentTimeMillis();
//        System.out.println("defineClass: " + (end - m) + "ms");
        try {
            return (FastMethod) fastMethodClass.getConstructor(Method.class, MemberLoadable.class).newInstance(method, memberLoader);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FastMemberInstantiationException(e);
        } catch (ExceptionInInitializerError e) {
            throw (FastMemberInstantiationException) e.getCause();
        }
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
    private final FastClass<?> declaringClass;
    private final MemberLoadable memberLoader;

    }
