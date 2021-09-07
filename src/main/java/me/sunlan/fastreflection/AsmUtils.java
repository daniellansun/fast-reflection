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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

class AsmUtils {
    public static void cast(MethodVisitor mv, Class<?> parameterType) {
        if (parameterType.isPrimitive()) {
            String wrapperInternalName = getInternalName(getWrapper(parameterType));
            mv.visitTypeInsn(CHECKCAST, wrapperInternalName);
            mv.visitMethodInsn(INVOKEVIRTUAL, wrapperInternalName, parameterType.getSimpleName() + "Value", "()" + Type.getType(parameterType), false);
        } else {
            mv.visitTypeInsn(CHECKCAST, getInternalName(parameterType));
        }
    }

    public static void doReturn(MethodVisitor mv, Class<?> returnType) {
        if (void.class == returnType) {
            mv.visitInsn(ACONST_NULL);
        } else {
            if (returnType.isPrimitive()) {
                Class<?> returnTypeWrapper = getWrapper(returnType);
                String valueOfMethodDescriptor = getMethodDescriptor(returnTypeWrapper, new Class[] {returnType});
                mv.visitMethodInsn(INVOKESTATIC, getInternalName(returnTypeWrapper), "valueOf", valueOfMethodDescriptor, false);
            }
        }
        mv.visitInsn(ARETURN);
    }

    public static void visitLdcTypeInsn(MethodVisitor mv, Class<?> type) {
        if (type.isPrimitive()) {
            mv.visitFieldInsn(GETSTATIC, getInternalName(getWrapper(type)), "TYPE", "Ljava/lang/Class;");
        } else {
            mv.visitLdcInsn(Type.getObjectType(getInternalName(type)));
        }
    }

    public static String getInternalName(Class<?> type) {
        return type.getName().replace('.', '/');
    }

    public static Class<?> getWrapper(Class<?> primitiveType) {
        return PRIMITIVES_TO_WRAPPERS.get(primitiveType);
    }

    public static String getMethodDescriptor(Class<?> returnType, Class<?>[] paramTypes) {
        StringJoiner buffer = new StringJoiner("", "(", ")" + Type.getDescriptor(returnType));
        for (Class<?> paramType : paramTypes) {
            buffer.add(Type.getDescriptor(paramType));
        }
        return buffer.toString();
    }

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS;
    static {
        Map<Class<?>, Class<?>> m = new HashMap<>();
        m.put(boolean.class, Boolean.class);
        m.put(byte.class, Byte.class);
        m.put(char.class, Character.class);
        m.put(double.class, Double.class);
        m.put(float.class, Float.class);
        m.put(int.class, Integer.class);
        m.put(long.class, Long.class);
        m.put(short.class, Short.class);
        m.put(void.class, Void.class);
        PRIMITIVES_TO_WRAPPERS = Collections.unmodifiableMap(m);
    }

    private AsmUtils() {}
}
