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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FastFieldTest {
    @Test
    public void testGet() throws Throwable {
        FastField ff = FastField.create(Person.class.getField("name"));
        String name = "Daniel";
        Person person = new Person(name);
        String result = (String) ff.get(person);
        assertEquals(name, result);
    }

    @Test
    public void testGet2() throws Throwable {
        Field sizeField = Integer.class.getField("SIZE");
        FastField ff = FastField.create(sizeField);
        MethodHandle sizeGetter = MethodHandles.publicLookup().unreflectGetter(sizeField);
        int expected = (int) sizeField.get(null);
        int result1 = (int) sizeGetter.invokeExact();
        int result2 = (int) ff.get(null);
        assertEquals(expected, result1);
        assertEquals(expected, result2);
    }

    @Test
    public void testSet() throws Throwable {
        FastField ff = FastField.create(Person.class.getField("name"));
        String name = "Daniel";
        Person person = new Person(name);
        ff.set(person, "sunlan");
        assertEquals("sunlan", person.name);
    }

    @Test
    public void testSet2() throws Throwable {
        FastField ff = FastField.create(Person.class.getField("JUST_FOR_FASTFIELDTEST_TESTSET2"));
        int newValue = 6;
        ff.set(null, newValue);
        assertEquals(newValue, Person.JUST_FOR_FASTFIELDTEST_TESTSET2);
    }

    @Test
    public void testGetName() throws NoSuchFieldException {
        String fieldName = "name";
        FastField ff = FastField.create(Person.class.getField(fieldName));
        assertEquals(fieldName, ff.getName());
    }

    @Test
    public void testEqualsAndHashCode() throws NoSuchFieldException {
        FastField ff1 = FastField.create(Person.class.getField("name"));
        FastField ff2 = FastField.create(Person.class.getField("name"));
        Set<FastField> fastFieldSet = new HashSet<>();
        fastFieldSet.add(ff1);
        fastFieldSet.add(ff2);
        assertEquals(1, fastFieldSet.size());
        assertSame(ff1, new ArrayList<>(fastFieldSet).get(0));
    }

    @Test
    public void testToString() throws NoSuchFieldException {
        Field field = Person.class.getField("name");
        FastField ff = FastField.create(field);
        assertEquals(field.toString(), ff.toString());
    }

}