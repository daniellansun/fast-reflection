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

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        return STRING_METHOD_STARTSWITH.invoke("abc", "a");
    }

    @Benchmark
    public Object method_reflect_accessible_StringStartsWith() throws Throwable {
        return STRING_METHOD_STARTSWITH_ACCESSIBLE.invoke("abc", "a");
    }

    @Benchmark
    public Object method_constant_handle_StringStartsWith() throws Throwable {
        return (boolean) STRING_METHOD_STARTSWITH_CONSTANT_HANDLE.invokeExact("abc", "a");
    }

    @Benchmark
    public Object method_instance_handle_StringStartsWith() throws Throwable {
        return (boolean) startswithInstanceMethodHandle.invokeExact("abc", "a");
    }

    @Benchmark
    public Object method_fastreflect_StringStartsWith() throws Throwable {
        return STRING_FAST_METHOD_STARTSWITH.invoke("abc", "a");
    }

    @Benchmark
    public Object method_constant_lambdametafactory_StringStartsWith() throws Throwable {
        return STRING_CONSTANT_FUNCTION_STARTSWITH.apply("abc", "a");
    }

    @Benchmark
    public Object method_instance_lambdametafactory_StringStartsWith() throws Throwable {
        return stringInstanceFunctionStartswith.apply("abc", "a");
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
        return STRING_FAST_CONSTRUCTOR_CHAR_ARRAY.invoke(CHAR_ARRAY_OBJECT);
    }

    @Benchmark
    public Object constructor_constant_lambdametafactory_StringCtorCharArray() throws Throwable {
        return STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_FUNCTION.apply(CHAR_ARRAY_OBJECT);
    }

    @Benchmark
    public Object constructor_instance_lambdametafactory_StringCtorCharArray() throws Throwable {
        return stringCharArrayInstanceFunction.apply(CHAR_ARRAY_OBJECT);
    }

    private static final Method STRING_METHOD_STARTSWITH;
    private static final Method STRING_METHOD_STARTSWITH_ACCESSIBLE;
    private static final MethodHandle STRING_METHOD_STARTSWITH_CONSTANT_HANDLE;
    private final MethodHandle startswithInstanceMethodHandle;
    private static final FastMethod STRING_FAST_METHOD_STARTSWITH;
    private static final BiFunction STRING_CONSTANT_FUNCTION_STARTSWITH;

    private static final Constructor<String> STRING_CONSTRUCTOR_CHAR_ARRAY;
    private static final Constructor<String> STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE;
    private static final MethodHandle STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_HANDLE;
    private final MethodHandle stringConstructorCharArrayInstanceHandle;
    private static final FastConstructor<String> STRING_FAST_CONSTRUCTOR_CHAR_ARRAY;
    private static final char[] CHAR_ARRAY = {'a', 'b', 'c'};
    private static final Object CHAR_ARRAY_OBJECT = CHAR_ARRAY;
    private static final Function STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_FUNCTION;

    static {
        try {
            STRING_METHOD_STARTSWITH = String.class.getMethod("startsWith", String.class);
            STRING_METHOD_STARTSWITH_ACCESSIBLE = String.class.getMethod("startsWith", String.class);
            STRING_METHOD_STARTSWITH_ACCESSIBLE.setAccessible(true);
            STRING_METHOD_STARTSWITH_CONSTANT_HANDLE = MethodHandles.publicLookup().unreflect(STRING_METHOD_STARTSWITH);
            STRING_FAST_METHOD_STARTSWITH = FastMethod.create(STRING_METHOD_STARTSWITH);
            STRING_CONSTANT_FUNCTION_STARTSWITH = createStartsWithFunction();


            STRING_CONSTRUCTOR_CHAR_ARRAY = String.class.getConstructor(char[].class);
            STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE = String.class.getConstructor(char[].class);
            STRING_CONSTRUCTOR_CHAR_ARRAY_ACCESSIBLE.setAccessible(true);
            STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_HANDLE = MethodHandles.publicLookup().unreflectConstructor(STRING_CONSTRUCTOR_CHAR_ARRAY);
            STRING_FAST_CONSTRUCTOR_CHAR_ARRAY = FastConstructor.create(STRING_CONSTRUCTOR_CHAR_ARRAY);
            STRING_CONSTRUCTOR_CHAR_ARRAY_CONSTANT_FUNCTION = createStringConstructorCharArrayFunction();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static Function createStringConstructorCharArrayFunction() throws Throwable {
        CallSite site = LambdaMetafactory.metafactory(MethodHandles.lookup(),
                "apply",
                MethodType.methodType(Function.class),
                MethodType.methodType(Object.class, Object.class),
                MethodHandles.publicLookup().unreflectConstructor(String.class.getConstructor(char[].class)),
                MethodType.methodType(String.class, char[].class));
        return (Function) site.getTarget().invokeExact();
    }

    private static BiFunction createStartsWithFunction() throws Throwable {
        CallSite site = LambdaMetafactory.metafactory(MethodHandles.lookup(),
                "apply",
                MethodType.methodType(BiFunction.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                MethodHandles.lookup().findVirtual(String.class, "startsWith", MethodType.methodType(boolean.class, String.class)),
                MethodType.methodType(boolean.class, String.class, String.class));
        return (BiFunction) site.getTarget().invokeExact();
    }

    private final BiFunction stringInstanceFunctionStartswith;
    private final Function stringCharArrayInstanceFunction;

    {
        try {
            startswithInstanceMethodHandle = MethodHandles.publicLookup().unreflect(STRING_METHOD_STARTSWITH);
            stringConstructorCharArrayInstanceHandle = MethodHandles.publicLookup().unreflectConstructor(STRING_CONSTRUCTOR_CHAR_ARRAY);

            stringInstanceFunctionStartswith = createStartsWithFunction();
            stringCharArrayInstanceFunction = createStringConstructorCharArrayFunction();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
