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

import static org.junit.jupiter.api.Assertions.assertEquals;

class FastConstructorTest {
    @Test
    public void testStringCtorCharArray() throws Throwable {
        FastConstructor<String> fc = FastConstructor.create(String.class.getConstructor(char[].class));
        Object arg = new char[]{'a', 'b', 'c'};
        String result = (String) fc.invoke(arg);
        assertEquals("abc", result);
    }

    @Test
    public void testStringCtorCharArrayIntInt() throws Throwable {
        FastConstructor<String> fc = FastConstructor.create(String.class.getConstructor(char[].class, int.class, int.class));
        Object arg = new char[]{'a', 'b', 'c'};
        String result = (String) fc.invoke(arg, 1, 2);
        assertEquals("bc", result);
    }

    @Test
    public void testStringCtorByteArrayString() throws Throwable {
        String str = "abc";
        String encoding = "UTF-8";
        byte[] bytes = str.getBytes(encoding);
        FastConstructor<String> fc = FastConstructor.create(String.class.getConstructor(byte[].class, String.class));
        String result = (String) fc.invoke(bytes, encoding);
        assertEquals(str, result);
    }

}
