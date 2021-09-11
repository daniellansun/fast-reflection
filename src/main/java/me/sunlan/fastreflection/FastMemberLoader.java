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

import me.sunlan.fastreflection.generator.MemberData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FastMemberLoader extends ClassLoader implements MemberLoadable {
    private static final FastMemberLoader DEFAULT_LOADER = new FastMemberLoader();

    public static FastMemberLoader getDefaultLoader() {
        return DEFAULT_LOADER;
    }

    public FastMemberLoader() {
        this(FastMemberLoader.class.getClassLoader());
    }

    public FastMemberLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public <T extends FastMember> T load(MemberData memberData) {
        final String fastMemberClassName = memberData.getName();
        FastMember result = loadedFastMemberCache.computeIfAbsent(fastMemberClassName, m -> {
            Class<?> fastMemberClass = defineClass(fastMemberClassName, memberData.getBytes());
            try {
                Member member = memberData.getMember();
                return (FastMember) fastMemberClass.getConstructor(member.getClass(), MemberLoadable.class).newInstance(member, this);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new FastInstantiationException(e);
            } catch (ExceptionInInitializerError e) {
                throw (FastInstantiationException) e.getCause();
            }
        });

        return (T) result;
    }

    private Class<?> defineClass(String className, byte[] bytes) {
        Class<?> result = findLoadedClass(className);
        if (null != result) {
            return result;
        }
        return super.defineClass(className, bytes, 0, bytes.length);
    }

    private final Map<String, FastMember> loadedFastMemberCache = new ConcurrentHashMap<>();
}
