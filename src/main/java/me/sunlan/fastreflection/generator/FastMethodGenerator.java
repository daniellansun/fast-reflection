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

import me.sunlan.fastreflection.FastMethod;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import static me.sunlan.fastreflection.generator.AsmUtils.cast;
import static me.sunlan.fastreflection.generator.AsmUtils.getMethodDescriptor;
import static me.sunlan.fastreflection.generator.EncodingUtils.md5;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class FastMethodGenerator implements FastMemberGenerator {
    public static final FastMethodGenerator INSTANCE = new FastMethodGenerator();

    private FastMethodGenerator() {}

    @Override
    public Class<FastMethod> getFastMemberClass() {
        return FastMethod.class;
    }

    @Override
    public String generateClassName(Member member) {
        return getFastMemberClass().getName() + "_" + md5(((Method) member).toGenericString());
    }

    @Override
    public Class<?> getInvokeMethodReturnType(Member member) {
        return ((Method) member).getReturnType();
    }

    @Override
    public Class<?> getFindMethodReturnType(Member member) {
        return getInvokeMethodReturnType(member);
    }

    @Override
    public Class<?>[] getParameterTypes(Member member) {
        return ((Method) member).getParameterTypes();
    }

    @Override
    public String getMemberDescriptor() {
        return "Ljava/lang/reflect/Method;";
    }

    @Override
    public void visitFindMethod(MethodVisitor mv, Member member) {
        boolean isStatic = Modifier.isStatic(member.getModifiers());
        mv.visitMethodInsn(INVOKEVIRTUAL, LOOKUP_INTERNAL_NAME, isStatic ? "findStatic" : "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
    }

    @Override
    public void visitMemberName(MethodVisitor mv, Member member) {
        mv.visitLdcInsn(member.getName());
    }

    @Override
    public void visitTargetObject(Member member, MethodVisitor mv) {
        if (!Modifier.isStatic(member.getModifiers())) {
            mv.visitVarInsn(ALOAD, 1);
            cast(mv, member.getDeclaringClass());
        }
    }

    @Override
    public int getArgsIndex() {
        return 2;
    }

    @Override
    public String getInvokeExactMethodDescriptor(Member member, Class<?>[] parameterTypes, Class<?> returnType) {
        Stream<Class<?>> parameterTypeStream = Arrays.stream(parameterTypes);
        if (!Modifier.isStatic(member.getModifiers())) {
            parameterTypeStream = Stream.concat(Stream.of(member.getDeclaringClass()), parameterTypeStream);
        }

        String invokeExactMethodDescriptor = getMethodDescriptor(returnType, parameterTypeStream.toArray(Class[]::new));
        return invokeExactMethodDescriptor;
    }

    @Override
    public String getInvokeMethodDescriptor() {
        return "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;";
    }
}
