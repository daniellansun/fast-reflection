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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastMethodTest {
    @Test
    public void testStringStartsWith() throws Throwable {
        FastMethod fm = FastMethod.create(String.class.getMethod("startsWith", String.class));
        boolean result = (boolean) fm.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testStringSubstring() throws Throwable {
        FastMethod fm = FastMethod.create(String.class.getMethod("substring", int.class, int.class));
        Object result = fm.invoke("abc", 1, 2);
        assertEquals("b", result);
    }

    @Test
    public void testStringToCharArray() throws Throwable {
        FastMethod fm = FastMethod.create(String.class.getMethod("toCharArray"));
        char[] result = (char[]) fm.invoke("abc");
        assertArrayEquals(new char[]{'a', 'b', 'c'}, result);
    }

    @Test
    public void testStringSplit() throws Throwable {
        FastMethod fm = FastMethod.create(String.class.getMethod("split", String.class));
        String[] result = (String[]) fm.invoke("a,b,c", ",");
        assertArrayEquals(new String[]{"a", "b", "c"}, result);
    }

    @Test
    public void testStringValueOf() throws Throwable {
        FastMethod fm = FastMethod.create(String.class.getMethod("valueOf", int.class));
        Object result = fm.invoke(null, 123);
        assertEquals("123", result);
    }

    @Test
    public void testStringValueOf2() throws Throwable {
        FastMethod fm = FastMethod.create(String.class.getMethod("valueOf", char[].class));
        Object arg = new char[]{'1', '2', '3'};
        Object result = fm.invoke(null, arg);
        assertEquals("123", result);
    }

    @Test
    public void testEqualsAndHashCode() throws NoSuchMethodException {
        Set<FastMethod> fastMethodSet = new HashSet<>();
        FastMemberLoader fastMemberLoader = new FastMemberLoader();
        FastMethod fm1 = FastMethod.create(String.class.getMethod("startsWith", String.class), fastMemberLoader);
        FastMethod fm2 = FastMethod.create(String.class.getMethod("startsWith", String.class), fastMemberLoader);
        fastMethodSet.add(fm1);
        fastMethodSet.add(fm2);
        assertEquals(1, fastMethodSet.size());
        assertSame(fm1, new ArrayList<>(fastMethodSet).get(0));
    }

    @Test
    public void testToString() throws NoSuchMethodException {
        Method startsWithMethod = String.class.getMethod("startsWith", String.class);
        FastMethod fm = FastMethod.create(startsWithMethod);
        assertEquals(fm.toString(), startsWithMethod.toString());
    }

    @Test
    public void testGetModifiers() throws NoSuchMethodException {
        Method startsWithMethod = String.class.getMethod("startsWith", String.class);
        FastMethod fm = FastMethod.create(startsWithMethod);
        assertEquals(startsWithMethod.getModifiers(), fm.getModifiers());
    }

    @Test
    public void testGetDeclaringClass() throws NoSuchMethodException {
        FastMethod fm = FastMethod.create(String.class.getMethod("startsWith", String.class));
        FastClass<?> declaringClass = fm.getDeclaringClass();
        FastMethod fm2 = declaringClass.getDeclaredMethod("startsWith", String.class);
        assertEquals(fm, fm2);
    }

    @Test
    public void testGetReturnType() throws NoSuchMethodException {
        FastMethod fm = FastMethod.create(String.class.getMethod("startsWith", String.class));
        FastClass<?> returnType = fm.getReturnType();
        assertEquals("boolean", returnType.getName());
    }

    @Test
    public void testInvisibleMethod() {
        FastMemberInstantiationException exception = assertThrows(FastMemberInstantiationException.class, () -> {
            FastMethod.create(AbstractList.class.getDeclaredMethod("removeRange", int.class, int.class));
        });
        Throwable cause = exception.getCause();
        assertTrue(cause instanceof IllegalAccessException);
        assertEquals("no such method: java.util.AbstractList.removeRange(int,int)void/invokeVirtual", cause.getMessage());
    }

    @Test
    public void testIsVarArgs() throws Throwable {
        FastMethod fm = FastMethod.create(Arrays.class.getMethod("asList", Object[].class));
        Object arg = new Object[] {1, 2, 3};
        Object result = fm.invoke(null, arg);
        assertEquals(Arrays.asList(1, 2, 3), result);
        assertTrue(fm.isVarArgs());
    }
}
