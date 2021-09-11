package me.sunlan.fastreflection.generator;

import me.sunlan.fastreflection.FastInstantiationException;
import me.sunlan.fastreflection.MemberLoadable;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Member;

import static me.sunlan.fastreflection.generator.AsmUtils.cast;
import static me.sunlan.fastreflection.generator.AsmUtils.doReturn;
import static me.sunlan.fastreflection.generator.AsmUtils.visitLdcTypeInsn;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
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
import static org.objectweb.asm.Type.getDescriptor;
import static org.objectweb.asm.Type.getInternalName;

abstract class FastExecutableGenerator implements FastMemberGenerator {

    @Override
    public MemberData generate(Member member) {
        ClassWriter classWriter = new ClassWriter(CLASSWRITER_FLAGS);
        final String className = generateClassName(member);
        final String internalClassName = className.replace('.', '/');
        final String fastMemberInternalName = getInternalName(getFastMemberClass());
        classWriter.visit(V1_8, ACC_CLASS, internalClassName, null, fastMemberInternalName, null);
        classWriter.visitInnerClass(LOOKUP_INTERNAL_NAME, METHODHANDLES_INTERNAL_NAME, "Lookup", ACC_INNERCLASS);

        generateConstantMethodHandleField(classWriter);
        generateConstructor(classWriter, fastMemberInternalName);

        final Class<?>[] parameterTypes = getParameterTypes(member);
        generateInvokeMethod(member, classWriter, internalClassName, parameterTypes);
        generateStaticBlock(member, classWriter, internalClassName, parameterTypes);

        classWriter.visitEnd();

        return new MemberData(className, classWriter.toByteArray(), member);
    }

    private void generateStaticBlock(Member member, ClassWriter classWriter, String internalClassName, Class<?>[] parameterTypes) {
        MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();
        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        mv.visitTryCatchBlock(label0, label1, label2, THROWABLE_INTERNAL_NAME);
        mv.visitLabel(label0);
        visitLdcTypeInsn(mv, member.getDeclaringClass());
        visitMemberName(mv, member);
        visitTypeArray(parameterTypes, mv);
        visitGetMember(mv, member);
        mv.visitVarInsn(ASTORE, 0);
        mv.visitMethodInsn(INVOKESTATIC, METHODHANDLES_INTERNAL_NAME, "lookup", "()" + LOOKUP_DESCRIPTOR, false);
        mv.visitVarInsn(ALOAD, 0);
        visitFindMethod(mv, member);
        mv.visitFieldInsn(PUTSTATIC, internalClassName, "METHOD_HANDLE", METHODHANDLE_DESCRIPTOR);
        mv.visitLabel(label1);
        Label label4 = new Label();
        mv.visitJumpInsn(GOTO, label4);
        mv.visitLabel(label2);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitTypeInsn(NEW, FASTINSTANTIATIONEXCEPTION_INTERNAL_NAME);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, FASTINSTANTIATIONEXCEPTION_INTERNAL_NAME, INIT, "(" + THROWABLE_DESCRIPTOR + ")V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(label4);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateInvokeMethod(Member member, ClassWriter classWriter, String internalClassName, Class<?>[] parameterTypes) {
        MethodVisitor mv = classWriter.visitMethod(ACC_METHOD, "invoke", getInvokeMethodDescriptor(), null, new String[]{THROWABLE_INTERNAL_NAME});
        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitFieldInsn(GETSTATIC, internalClassName, "METHOD_HANDLE", METHODHANDLE_DESCRIPTOR);

        visitTargetObject(member, mv);

        final int argsIndex = getArgsIndex();
        for (int i = 0, n = parameterTypes.length; i < n; i++) {
            mv.visitVarInsn(ALOAD, argsIndex);
            mv.visitLdcInsn(i);
            mv.visitInsn(AALOAD);
            cast(mv, parameterTypes[i]);
        }

        final Class<?> invokeMethodReturnType = getInvokeMethodReturnType(member);
        String invokeExactMethodDescriptor = getInvokeExactMethodDescriptor(member, parameterTypes, invokeMethodReturnType);
        mv.visitMethodInsn(INVOKEVIRTUAL, METHODHANDLE_INTERNAL_NAME, "invokeExact", invokeExactMethodDescriptor, false);
        doReturn(mv, invokeMethodReturnType);
        Label label3 = new Label();
        mv.visitLabel(label3);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateConstructor(ClassWriter classWriter, String fastMemberInternalName) {
        final String constructorDescriptor = "(" + getMemberDescriptor() + MEMBERLOADABLE_DESCRIPTOR + ")V";
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, INIT, constructorDescriptor, null, null);
        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, fastMemberInternalName, INIT, constructorDescriptor, false);
        mv.visitInsn(RETURN);
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateConstantMethodHandleField(ClassWriter classWriter) {
        FieldVisitor fv = classWriter.visitField(ACC_FIELD, "METHOD_HANDLE", METHODHANDLE_DESCRIPTOR, null, null);
        fv.visitEnd();
    }

    protected void visitTypeArray(Class<?>[] parameterTypes, MethodVisitor mv) {
        mv.visitLdcInsn(parameterTypes.length);
        mv.visitTypeInsn(ANEWARRAY, CLASS_INTERNAL_NAME);
        for (int i = 0, n = parameterTypes.length; i < n; i++) {
            mv.visitInsn(DUP);
            mv.visitLdcInsn(i);
            visitLdcTypeInsn(mv, parameterTypes[i]);
            mv.visitInsn(AASTORE);
        }
    }

    private static final int CLASSWRITER_FLAGS = ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
    private static final int ACC_CLASS = ACC_PUBLIC | ACC_FINAL | ACC_SUPER;
    private static final int ACC_INNERCLASS = ACC_PUBLIC | ACC_FINAL | ACC_STATIC;
    private static final int ACC_FIELD = ACC_PRIVATE | ACC_FINAL | ACC_STATIC;
    private static final int ACC_METHOD = ACC_PUBLIC | ACC_VARARGS;
    private static final String INIT = "<init>";
    protected static final String METHODHANDLE_DESCRIPTOR = getDescriptor(MethodHandle.class);
    protected static final String METHODHANDLE_INTERNAL_NAME = getInternalName(MethodHandle.class);
    protected static final String METHODHANDLES_INTERNAL_NAME = getInternalName(MethodHandles.class);
    protected static final String FASTINSTANTIATIONEXCEPTION_INTERNAL_NAME = getInternalName(FastInstantiationException.class);
    protected static final String THROWABLE_INTERNAL_NAME = getInternalName(Throwable.class);
    protected static final String THROWABLE_DESCRIPTOR = getDescriptor(Throwable.class);
    protected static final String MEMBERLOADABLE_DESCRIPTOR = getDescriptor(MemberLoadable.class);
    protected static final String LOOKUP_DESCRIPTOR = getDescriptor(MethodHandles.Lookup.class);
    protected static final String CLASS_INTERNAL_NAME = getInternalName(Class.class);
}
