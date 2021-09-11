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
import me.sunlan.fastreflection.generator.FastFieldGetterGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public abstract class FastFieldGetter extends FastMethod {
    public FastFieldGetter(Field field, MemberLoadable memberLoader) {
        super(null, memberLoader);
        this.field = field;
    }

    public static FastMethod create(Field field) {
        return create(field, new FastMemberLoader());
    }
    public static FastFieldGetter create(Field field, MemberLoadable memberLoader) {
        ClassData classData = FastFieldGetterGenerator.INSTANCE.generate(field);
        Class<?> fastMethodClass = memberLoader.load(classData.getName(), classData.getBytes());
        try {
            return (FastFieldGetter) fastMethodClass.getConstructor(Field.class, MemberLoadable.class).newInstance(field, memberLoader);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FastInstantiationException(e);
        } catch (ExceptionInInitializerError e) {
            throw (FastInstantiationException) e.getCause();
        }
    }

    public Object get(Object obj) throws Throwable {
        return invoke(obj);
    }

    private final Field field;
}
