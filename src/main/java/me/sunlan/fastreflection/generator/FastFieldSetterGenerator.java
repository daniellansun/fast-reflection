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
package me.sunlan.fastreflection.generator;

import me.sunlan.fastreflection.FastFieldSetter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import static me.sunlan.fastreflection.generator.EncodingUtils.md5;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class FastFieldSetterGenerator extends FastMethodGenerator {
    public static final FastFieldSetterGenerator INSTANCE = new FastFieldSetterGenerator();

    @Override
    public Class<FastFieldSetter> getFastMemberClass() {
        return FastFieldSetter.class;
    }

    @Override
    public String generateClassName(Member member) {
        return getFastMemberClass().getName() + "_" + md5(((Field) member).toGenericString());
    }

    @Override
    public Class<?> getInvokeMethodReturnType(Member member) {
        return void.class;
    }

    @Override
    public void visitGetMember(MethodVisitor mv, Member member) {
        boolean isPublic = Modifier.isPublic(member.getModifiers());
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", isPublic ? "getField" : "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
    }

    @Override
    public Class<?>[] getParameterTypes(Member member) {
        return new Class<?>[] { ((Field) member).getType() };
    }

    @Override
    public String getMemberDescriptor() {
        return "Ljava/lang/reflect/Field;";
    }

    @Override
    public void visitFindMethod(MethodVisitor mv, Member member) {
        mv.visitMethodInsn(INVOKEVIRTUAL, LOOKUP_INTERNAL_NAME, "unreflectSetter", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/MethodHandle;", false);
    }

    @Override
    public void visitTypeArray(Class<?>[] parameterTypes, MethodVisitor mv) {
        // do nothing
    }

    private FastFieldSetterGenerator() {}
}
