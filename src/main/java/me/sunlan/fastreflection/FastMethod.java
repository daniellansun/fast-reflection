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

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static me.sunlan.fastreflection.AsmUtils.cast;
import static me.sunlan.fastreflection.AsmUtils.getInternalName;
import static me.sunlan.fastreflection.AsmUtils.getMethodDescriptor;
import static me.sunlan.fastreflection.AsmUtils.getWrapper;
import static me.sunlan.fastreflection.AsmUtils.visitLdcTypeInsn;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

public abstract class FastMethod implements FastMember {
    FastMethod() {
        this.method = null;
        this.declaringClass = null;
        this.classDefiner = null;
    }

    public FastMethod(Method method, ClassDefinable classDefiner) {
        this.method = method;
        this.classDefiner = classDefiner;
        this.declaringClass = FastClass.create(method.getDeclaringClass(), classDefiner);
    }

    @Override
    public FastClass<?> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    public FastClass<?> getReturnType() {
        return FastClass.create(method.getReturnType(), classDefiner);
    }

    public FastClass<?>[] getParameterTypes() {
        return Arrays.stream(method.getParameterTypes())
                .map(pt -> FastClass.create(pt, classDefiner))
                .toArray(FastClass[]::new);
    }

    public abstract Object invoke(Object obj, Object... args) throws Throwable;

    public static FastMethod create(Method method) {
        return create(method, new FastMemberLoader(Thread.currentThread().getContextClassLoader()));
    }

    public static FastMethod create(Method method, ClassDefinable classDefiner) {
        String className = "me.sunlan.fastreflection.runtime.FastMethod_" + md5(method.toGenericString());
//        long b = System.currentTimeMillis();
        byte[] bytes = gen(className, method);
//        long m = System.currentTimeMillis();
//        System.out.println("dump: " + (m - b) + "ms");

        Class<?> fastMethodClass = classDefiner.defineClass(className, bytes);
//        long e = System.currentTimeMillis();
//        System.out.println("defineClass: " + (e - m) + "ms");
        try {
            return (FastMethod) fastMethodClass.getConstructor(Method.class, ClassDefinable.class).newInstance(method, classDefiner);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new FastMemberInstantiationException(e);
        } catch (ExceptionInInitializerError e) {
            throw (FastMemberInstantiationException) e.getCause();
        }
    }

    private static byte[] gen(String className, Method method) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        final String internalClassName = className.replace('.', '/');
        classWriter.visit(V1_8, ACC_CLASS, internalClassName, null, FASTMETHOD_INTERNAL_NAME, null);
        classWriter.visitInnerClass(LOOKUP_INTERNAL_NAME, METHODHANDLE_INTERNAL_NAME, "Lookup", ACC_INNERCLASS);

        {
            FieldVisitor fv = classWriter.visitField(ACC_FIELD, "METHOD_HANDLE", METHODHANDLE_DESCRIPTOR, null, null);
            fv.visitEnd();
        }

        final String classDescriptor = "L" + internalClassName + ";";
        {
            MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", CONSTRUCTOR_DESCRIPTOR, null, null);
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, FASTMETHOD_INTERNAL_NAME, "<init>", CONSTRUCTOR_DESCRIPTOR, false);
            mv.visitInsn(RETURN);
            Label label2 = new Label();
            mv.visitLabel(label2);
            mv.visitLocalVariable("this", classDescriptor, null, label0, label2, 0);
            mv.visitLocalVariable("method", "Ljava/lang/reflect/Method;", null, label0, label2, 1);
            mv.visitLocalVariable("classDefiner", "Lme/sunlan/fastreflection/ClassDefinable;", null, label0, label2, 2);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?> returnType = method.getReturnType();
        final boolean isStaticMethod = Modifier.isStatic(method.getModifiers());
        {
            MethodVisitor mv = classWriter.visitMethod(ACC_METHOD, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, new String[]{"java/lang/Throwable"});
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitFieldInsn(GETSTATIC, internalClassName, "METHOD_HANDLE", METHODHANDLE_DESCRIPTOR);

            if (!isStaticMethod) {
                mv.visitVarInsn(ALOAD, 1);
                cast(mv, method.getDeclaringClass());
            }

            for (int i = 0, n = parameterTypes.length; i < n; i++) {
                mv.visitVarInsn(ALOAD, 2);
                mv.visitLdcInsn(i);
                mv.visitInsn(AALOAD);
                cast(mv, parameterTypes[i]);
            }

            Stream<Class<?>> parameterTypeStream = Arrays.stream(parameterTypes);
            if (!isStaticMethod) {
                parameterTypeStream = Stream.concat(Stream.of(method.getDeclaringClass()), parameterTypeStream);
            }

            String invokeExactMethodDescriptor = getMethodDescriptor(returnType, parameterTypeStream.toArray(Class[]::new));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invokeExact", invokeExactMethodDescriptor, false);
            if (void.class != returnType) {
                if (returnType.isPrimitive()) {
                    Class<?> returnTypeWrapper = getWrapper(returnType);
                    String valueOfMethodDescriptor = getMethodDescriptor(returnTypeWrapper, new Class[] { returnType });
                    mv.visitMethodInsn(INVOKESTATIC, getInternalName(returnTypeWrapper), "valueOf", valueOfMethodDescriptor, false);
                }
            } else {
                mv.visitInsn(ACONST_NULL);
            }
            mv.visitInsn(ARETURN);
            Label label3 = new Label();
            mv.visitLabel(label3);
            mv.visitLocalVariable("this", classDescriptor, null, label0, label3, 0);
            mv.visitLocalVariable("obj", "Ljava/lang/Object;", null, label0, label3, 1);
            mv.visitLocalVariable("args", "[Ljava/lang/Object;", null, label0, label3, 2);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            mv.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
            mv.visitLabel(label0);
            mv.visitMethodInsn(INVOKESTATIC, METHODHANDLE_INTERNAL_NAME, "lookup", "()Ljava/lang/invoke/MethodHandles$Lookup;", false);
            visitLdcTypeInsn(mv, method.getDeclaringClass());
            mv.visitLdcInsn(method.getName());
            visitLdcTypeInsn(mv, returnType);
            mv.visitLdcInsn(parameterTypes.length);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            if (parameterTypes.length > 0) mv.visitInsn(DUP);
            for (int i = 0, n = parameterTypes.length; i < n; i++) {
                mv.visitLdcInsn(i);
                visitLdcTypeInsn(mv, parameterTypes[i]);
                mv.visitInsn(AASTORE);
                if (i != n - 1) mv.visitInsn(DUP);
            }
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, LOOKUP_INTERNAL_NAME, isStaticMethod ? "findStatic" : "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 0);
            mv.visitLabel(label1);
            Label label4 = new Label();
            mv.visitJumpInsn(GOTO, label4);
            mv.visitLabel(label2);
            mv.visitVarInsn(ASTORE, 1);
            mv.visitTypeInsn(NEW, FASTMEMBERINSTANTIATIONEXCEPTION_INTERNAL_NAME);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESPECIAL, FASTMEMBERINSTANTIATIONEXCEPTION_INTERNAL_NAME, "<init>", "(Ljava/lang/Throwable;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(label4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(PUTSTATIC, internalClassName, "METHOD_HANDLE", METHODHANDLE_DESCRIPTOR);
            Label label6 = new Label();
            mv.visitLabel(label6);
            mv.visitInsn(RETURN);
            mv.visitLocalVariable("mh", METHODHANDLE_DESCRIPTOR, null, label0, label6, 0);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }

    private static String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            return new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // should never happen
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastMethod)) return false;
        FastMethod that = (FastMethod) o;
        return method.equals(that.method) && declaringClass.equals(that.declaringClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, declaringClass);
    }

    @Override
    public String toString() {
        return method.toString();
    }

    private final Method method;
    private final FastClass<?> declaringClass;
    private final ClassDefinable classDefiner;

    private static final int ACC_CLASS = ACC_PUBLIC | ACC_FINAL | ACC_SUPER;
    private static final int ACC_INNERCLASS = ACC_PUBLIC | ACC_FINAL | ACC_STATIC;
    private static final int ACC_FIELD = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;
    private static final int ACC_METHOD = ACC_PUBLIC | ACC_VARARGS;
    private static final String METHODHANDLE_DESCRIPTOR = "Ljava/lang/invoke/MethodHandle;";
    private static final String CONSTRUCTOR_DESCRIPTOR = "(Ljava/lang/reflect/Method;Lme/sunlan/fastreflection/ClassDefinable;)V";
    private static final String FASTMETHOD_INTERNAL_NAME = FastMethod.class.getName().replace('.', '/');
    private static final String METHODHANDLE_INTERNAL_NAME = "java/lang/invoke/MethodHandles";
    private static final String LOOKUP_INTERNAL_NAME = "java/lang/invoke/MethodHandles$Lookup";
    private static final String FASTMEMBERINSTANTIATIONEXCEPTION_INTERNAL_NAME = "me/sunlan/fastreflection/FastMemberInstantiationException";
}
