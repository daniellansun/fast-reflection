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

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class FastMethodPerfTest {
    @Benchmark
    public boolean method_direct_StringStartsWith() {
        return "abc".startsWith("a");
    }

    @Benchmark
    public Object method_reflect_StringStartsWith() throws Throwable {
        return STARTSWITH_METHOD.invoke("abc", "a");
    }

    @Benchmark
    public Object method_reflect_accessible_StringStartsWith() throws Throwable {
        return STARTSWITH_METHOD_ACCESSIBLE.invoke("abc", "a");
    }

    @Benchmark
    public Object method_constant_handle_StringStartsWith() throws Throwable {
        return (boolean) STARTSWITH_CONSTANT_METHOD_HANDLE.invokeExact("abc", "a");
    }

    @Benchmark
    public Object method_instance_handle_StringStartsWith() throws Throwable {
        return (boolean) startswithInstanceMethodHandle.invokeExact("abc", "a");
    }

    @Benchmark
    public Object method_fastreflect_StringStartsWith() throws Throwable {
        return FAST_STARTSWITH_METHOD.invoke("abc", "a");
    }


    @Benchmark
    public Object constructor_direct_StringCtorCharArray() {
        return new String(CHAR_ARRAY);
    }

    @Benchmark
    public Object constructor_reflect_StringCtorCharArray() throws Throwable {
        return STRING_CONSTRUCTOR_CHAR_ARRAY.newInstance(CHAR_ARRAY_OBJECT);
    }

    @Benchmark
    public Object constructor_reflect_accessible_StringCtorCharArray() throws Throwable {
        return STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE.newInstance(CHAR_ARRAY_OBJECT);
    }

    @Benchmark
    public Object constructor_constant_handle_StringCtorCharArray() throws Throwable {
        return (String) STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_HANDLE.invokeExact(CHAR_ARRAY);
    }

    @Benchmark
    public Object constructor_instance_handle_StringCtorCharArray() throws Throwable {
        return (String) stringConstructorCharArrayInstanceHandle.invokeExact(CHAR_ARRAY);
    }

    @Benchmark
    public Object constructor_fastreflect_StringCtorCharArray() throws Throwable {
        return FAST_STRING_CONSTRUCTOR_CHAR_ARRAY.invoke(CHAR_ARRAY_OBJECT);
    }

    private static final Method STARTSWITH_METHOD;
    private static final Method STARTSWITH_METHOD_ACCESSIBLE;
    private static final MethodHandle STARTSWITH_CONSTANT_METHOD_HANDLE;
    private final MethodHandle startswithInstanceMethodHandle;
    private static final FastMethod FAST_STARTSWITH_METHOD;

    private static final Constructor<String> STRING_CONSTRUCTOR_CHAR_ARRAY;
    private static final Constructor<String> STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE;
    private static final MethodHandle STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_HANDLE;
    private final MethodHandle stringConstructorCharArrayInstanceHandle;
    private static final FastConstructor<String> FAST_STRING_CONSTRUCTOR_CHAR_ARRAY;
    private static final char[] CHAR_ARRAY = {'a', 'b', 'c'};
    private static final Object CHAR_ARRAY_OBJECT = CHAR_ARRAY;

    static {
        try {
            STARTSWITH_METHOD = String.class.getMethod("startsWith", String.class);
            STARTSWITH_METHOD_ACCESSIBLE = String.class.getMethod("startsWith", String.class);
            STARTSWITH_METHOD_ACCESSIBLE.setAccessible(true);
            STARTSWITH_CONSTANT_METHOD_HANDLE = MethodHandles.publicLookup().unreflect(STARTSWITH_METHOD);
            FAST_STARTSWITH_METHOD = FastMethod.create(STARTSWITH_METHOD);

            STRING_CONSTRUCTOR_CHAR_ARRAY = String.class.getConstructor(char[].class);
            STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE = String.class.getConstructor(char[].class);
            STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE.setAccessible(true);
            STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_HANDLE = MethodHandles.publicLookup().unreflectConstructor(STRING_CONSTRUCTOR_CHAR_ARRAY);
            FAST_STRING_CONSTRUCTOR_CHAR_ARRAY = FastConstructor.create(STRING_CONSTRUCTOR_CHAR_ARRAY);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    {
        try {
            startswithInstanceMethodHandle = MethodHandles.publicLookup().unreflect(STARTSWITH_METHOD);
            stringConstructorCharArrayInstanceHandle = MethodHandles.publicLookup().unreflectConstructor(STRING_CONSTRUCTOR_CHAR_ARRAY);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
