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

import me.sunlan.fastreflection.FastMember;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Member;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;

interface FastMemberGenerator {
    ClassData generate(Member member);
    String getInvokeMethodDescriptor();
    String getInvokeExactMethodDescriptor(Member member, Class<?>[] parameterTypes, Class<?> returnType);
    int getArgsIndex();
    void visitTargetObject(Member member, MethodVisitor mv);
    void visitMemberName(MethodVisitor mv, Member member);
    void visitFindMethod(MethodVisitor mv, Member member);
    Class<?> getInvokeMethodReturnType(Member member);
    void visitGetMember(MethodVisitor mv, Member member);
    Class<?>[] getParameterTypes(Member member);
    String getMemberDescriptor();
    Class<? extends FastMember> getFastMemberClass();
    String generateClassName(Member member);
    default String getConstructorDescriptor() {
        return "(" + getMemberDescriptor() + "Lme/sunlan/fastreflection/MemberLoadable;)V";
    }

    int CLASSWRITER_FLAGS = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
    int ACC_CLASS = ACC_PUBLIC | ACC_FINAL | ACC_SUPER;
    int ACC_INNERCLASS = ACC_PUBLIC | ACC_FINAL | ACC_STATIC;
    int ACC_FIELD = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;
    int ACC_METHOD = ACC_PUBLIC | ACC_VARARGS;
    String METHODHANDLE_DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";
    String METHODHANDLE_INTERNAL_NAME = "java/lang/invoke/MethodHandles";
    String LOOKUP_INTERNAL_NAME = "java/lang/invoke/MethodHandles$Lookup";
    String FASTMEMBERINSTANTIATIONEXCEPTION_INTERNAL_NAME = "me/sunlan/fastreflection/FastMemberInstantiationException";
}
