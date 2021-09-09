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

import me.sunlan.fastreflection.FastConstructor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;

import static me.sunlan.fastreflection.generator.AsmUtils.getMethodDescriptor;
import static me.sunlan.fastreflection.generator.EncodingUtils.md5;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class FastConstructorGenerator implements FastMemberGenerator {
    public static final FastConstructorGenerator INSTANCE = new FastConstructorGenerator();

    private FastConstructorGenerator() {}

    @Override
    public Class<FastConstructor> getFastMemberClass() {
        return FastConstructor.class;
    }

    @Override
    public String generateClassName(Member member) {
        return getFastMemberClass().getName() + "_" + md5(((Constructor) member).toGenericString());
    }

    @Override
    public String getMemberDescriptor() {
        return "Ljava/lang/reflect/Constructor;";
    }

    @Override
    public Class<?> getInvokeMethodReturnType(Member member) {
        return member.getDeclaringClass();
    }

    @Override
    public void visitGetMember(MethodVisitor mv, Member member) {
        boolean isPublic = Modifier.isPublic(member.getModifiers());
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", isPublic ? "getConstructor" : "getDeclaredConstructor", "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;", false);
    }

    @Override
    public Class<?>[] getParameterTypes(Member member) {
        return ((Constructor) member).getParameterTypes();
    }

    @Override
    public void visitFindMethod(MethodVisitor mv, Member member) {
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflectConstructor", "(Ljava/lang/reflect/Constructor;)Ljava/lang/invoke/MethodHandle;", false);
    }

    @Override
    public void visitMemberName(MethodVisitor mv, Member member) {
        // do nothing
    }

    @Override
    public void visitTargetObject(Member member, MethodVisitor mv) {
        // do nothing
    }

    @Override
    public int getArgsIndex() {
        return 1;
    }

    @Override
    public String getInvokeExactMethodDescriptor(Member member, Class<?>[] parameterTypes, Class<?> returnType) {
        return getMethodDescriptor(returnType, parameterTypes);
    }

    @Override
    public String getInvokeMethodDescriptor() {
        return "([Ljava/lang/Object;)Ljava/lang/Object;";
    }
}
