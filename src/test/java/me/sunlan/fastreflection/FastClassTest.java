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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FastClassTest {
    @Test
    public void testGetMethod() throws Throwable {
        FastClass fc = FastClass.create(String.class);
        FastMethod fm = fc.getMethod("startsWith", String.class);
        boolean result = (boolean) fm.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetDeclaredMethod() throws Throwable {
        FastClass fc = FastClass.create(String.class);
        FastMethod fm = fc.getDeclaredMethod("startsWith", String.class);
        boolean result = (boolean) fm.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetMethods() throws Throwable {
        FastClass fc = FastClass.create(String.class);
        FastMethod[] fms = fc.getMethods();
        FastMethod startsWithFastMethod = Arrays.stream(fms)
                .filter(fm -> fm.getName().equals("startsWith"))
                .findAny()
                .orElseThrow(() -> new AssertionError("method `startsWith` not found"));

        boolean result = (boolean) startsWithFastMethod.invoke("abc", "a");
        assertTrue(result);
    }

    @Test
    public void testGetDeclaredMethods() throws Throwable {
        FastClass fc = FastClass.create(String.class);
        FastMethod[] fms = fc.getDeclaredMethods();
        FastMethod startsWithFastMethod = Arrays.stream(fms)
                .filter(fm -> fm.getName().equals("startsWith"))
                .findAny()
                .orElseThrow(() -> new AssertionError("method `startsWith` not found"));

        boolean result = (boolean) startsWithFastMethod.invoke("abc", "a");
        assertTrue(result);
    }
}
