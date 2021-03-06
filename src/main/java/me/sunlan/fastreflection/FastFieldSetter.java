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

import me.sunlan.fastreflection.generator.FastFieldSetterGenerator;

import java.lang.reflect.Field;

public abstract class FastFieldSetter extends FastMethod {
    protected FastFieldSetter(Field field, MemberLoadable memberLoader) {
        super(null, memberLoader);
        this.field = field;
    }

    public static FastFieldSetter create(Field field) {
        return create(field, false);
    }

    public static FastFieldSetter create(Field field, boolean toSetAccessible) {
        return create(field, FastMemberLoader.getDefaultLoader(), toSetAccessible);
    }

    public static FastFieldSetter create(Field field, MemberLoadable memberLoader, boolean toSetAccessible) {
        return memberLoader.load(FastFieldSetterGenerator.INSTANCE.generate(field, toSetAccessible));
    }

    public void set(Object obj, Object value) throws Throwable {
        invoke(obj, value);
    }

    private final Field field;
}
