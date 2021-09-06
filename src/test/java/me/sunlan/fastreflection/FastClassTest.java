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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FastClassTest {
    @Test
    public void testGetMethod() throws Throwable {
        FastClass<String> fc = FastClass.create(String.class);
        FastMethod fm = fc.getMethod("startsWith", String.class);
        boolean result = (boolean) fm.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetDeclaredMethod() throws Throwable {
        FastClass<String> fc = FastClass.create(String.class);
        FastMethod fm = fc.getDeclaredMethod("startsWith", String.class);
        boolean result = (boolean) fm.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetMethods() throws Throwable {
        FastClass<String> fc = FastClass.create(String.class);
        FastMethod[] fms = fc.getMethods();
        FastMethod startsWithFastMethod = Arrays.stream(fms)
                .filter(fm -> fm.getName().equals("startsWith") && fm.getParameterTypes().length == 1)
                .findAny()
                .orElseThrow(() -> new AssertionError("method `startsWith` not found"));

        boolean result = (boolean) startsWithFastMethod.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetMethodsMultipleTimes() {
        FastClass<String> fc = FastClass.create(String.class);
        FastMethod[] fms = fc.getMethods();
        fms = fc.getMethods();
    }

    @Test
    public void testGetDeclaredMethods() throws Throwable {
        FastClass<String> fc = FastClass.create(String.class);
        FastMethod[] fms = fc.getDeclaredMethods();
        FastMethod startsWithFastMethod = Arrays.stream(fms)
                .filter(fm -> {
                    try {
                        return fm.getName().equals("startsWith") && fm.getParameterTypes().length == 1;
                    } catch (FastMemberInstantiationException ignored) {
                        return false;
                    }
                })
                .findAny()
                .orElseThrow(() -> new AssertionError("method `startsWith` not found"));

        boolean result = (boolean) startsWithFastMethod.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetDeclaredMethodsMultipleTimes() {
        FastClass<String> fc = FastClass.create(String.class);
        FastMethod[] fms = fc.getDeclaredMethods();
        fms = fc.getDeclaredMethods();
    }

    @Test
    public void testEqualsAndHashCode() {
        Set<FastClass<String>> fastClassSet = new HashSet<>();
        FastMemberLoader fastMemberLoader = new FastMemberLoader();
        FastClass<String> fc1 = FastClass.create(String.class, fastMemberLoader);
        FastClass<String> fc2 = FastClass.create(String.class, fastMemberLoader);
        fastClassSet.add(fc1);
        fastClassSet.add(fc2);
        assertEquals(1, fastClassSet.size());
        assertSame(fc1, new ArrayList<>(fastClassSet).get(0));
    }

    @Test
    public void testToString() {
        Class<String> clazz = String.class;
        FastClass<String> fc = FastClass.create(clazz);
        assertEquals(clazz.toString(), fc.toString());
    }

    @Test
    public void testGetName() {
        Class<String> clazz = String.class;
        FastClass<String> fc = FastClass.create(clazz);
        assertEquals(clazz.getName(), fc.getName());
    }

    @Test
    public void testGetSimpleName() {
        Class<String> clazz = String.class;
        FastClass<String> fc = FastClass.create(clazz);
        assertEquals(clazz.getSimpleName(), fc.getSimpleName());
    }

    @Test
    public void testGetModifiers() {
        Class<String> clazz = String.class;
        FastClass<String> fc = FastClass.create(clazz);
        assertEquals(clazz.getModifiers(), fc.getModifiers());
    }

    @Test
    public void testGetRawClass() {
        Class<String> clazz = String.class;
        FastClass<String> fc = FastClass.create(clazz);
        assertSame(clazz, fc.getRawClass());
    }
}
