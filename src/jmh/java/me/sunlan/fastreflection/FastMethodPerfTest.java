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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 *
 * Benchmark                                        Mode  Cnt   Score   Error  Units
 * FastMethodPerfTest.direct_StringStartsWith       avgt   15   0.471 ± 0.054  ns/op
 * FastMethodPerfTest.fastreflect_StringStartsWith  avgt   15   0.463 ± 0.017  ns/op
 * FastMethodPerfTest.reflect_StringStartsWith      avgt   15  10.643 ± 0.969  ns/op
 *
 */

@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(3)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class FastMethodPerfTest {
    @Benchmark
    public void method_direct_StringStartsWith() {
        "abc".startsWith("a");
    }

    @Benchmark
    public void method_reflect_StringStartsWith() throws Throwable {
        STARTSWITH_METHOD.invoke("abc", "a");
    }

    @Benchmark
    public void method_fastreflect_StringStartsWith() throws Throwable {
        FAST_STARTSWITH_METHOD.invoke("abc", "a");
    }


    @Benchmark
    public void constructor_direct_StringCtorCharArray() {
        new String(CHAR_ARRAY);
    }

    @Benchmark
    public void constructor_reflect_StringCtorCharArray() throws Throwable {
        STRING_CONSTRUCTOR_CHAR_ARRAY.newInstance(CHAR_ARRAY_OBJECT);
    }

    @Benchmark
    public void constructor_fastreflect_StringCtorCharArray() throws Throwable {
        FAST_STRING_CONSTRUCTOR_CHAR_ARRAY.invoke(CHAR_ARRAY_OBJECT);
    }

    @Benchmark
    public boolean method_direct_StringStartsWith_Return() {
        return "abc".startsWith("a");
    }

    @Benchmark
    public Object method_reflect_StringStartsWith_Return() throws Throwable {
        return STARTSWITH_METHOD.invoke("abc", "a");
    }

    @Benchmark
    public Object method_fastreflect_StringStartsWith_Return() throws Throwable {
        return FAST_STARTSWITH_METHOD.invoke("abc", "a");
    }

    private static final Method STARTSWITH_METHOD;
    private static final FastMethod FAST_STARTSWITH_METHOD;

    private static final Constructor<String> STRING_CONSTRUCTOR_CHAR_ARRAY;
    private static final FastConstructor<String> FAST_STRING_CONSTRUCTOR_CHAR_ARRAY;
    private static final char[] CHAR_ARRAY = {'a', 'b', 'c'};
    private static final Object CHAR_ARRAY_OBJECT = CHAR_ARRAY;

    static {
        try {
            STARTSWITH_METHOD = String.class.getMethod("startsWith", String.class);
            FAST_STARTSWITH_METHOD = FastMethod.create(STARTSWITH_METHOD);

            STRING_CONSTRUCTOR_CHAR_ARRAY = String.class.getConstructor(char[].class);
            FAST_STRING_CONSTRUCTOR_CHAR_ARRAY = FastConstructor.create(STRING_CONSTRUCTOR_CHAR_ARRAY);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
