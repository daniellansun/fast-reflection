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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;

public class FastMemberLoader extends ClassLoader implements MemberLoadable {
    public FastMemberLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public FastMemberLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public <T extends FastMember> T load(ClassData classData) {
        Class<?> fastMemberClass = defineClass(classData.getName(), classData.getBytes());
        Member member = classData.getMember();

        try {
            return (T) fastMemberClass.getConstructor(member.getClass(), MemberLoadable.class).newInstance(member, this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FastInstantiationException(e);
        } catch (ExceptionInInitializerError e) {
            throw (FastInstantiationException) e.getCause();
        }
    }

    private synchronized Class<?> defineClass(String className, byte[] bytes) {
        Class<?> result = findLoadedClass(className);
        if (null != result) {
            return result;
        }
        return super.defineClass(className, bytes, 0, bytes.length);
    }
}
