package me.sunlan.fastreflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class FastField implements FastMember {
    protected FastField() {
        this.field = null;
        this.memberLoader = null;
        this.declaringClass = null;
        this.fastFieldGetter = null;
        this.fastFieldSetter = null;
    }

    private FastField(Field field, MemberLoadable memberLoader, FastFieldGetter fastFieldGetter, FastFieldSetter fastFieldSetter) {
        this.field = field;
        this.memberLoader = memberLoader;
        this.declaringClass = FastClass.create(field.getDeclaringClass(), memberLoader);
        this.fastFieldGetter = fastFieldGetter;
        this.fastFieldSetter = fastFieldSetter;
    }

    public static FastField create(Field field) {
        return create(field, false);
    }

    public static FastField create(Field field, boolean toSetAccessible) {
        return create(field, FastMemberLoader.getDefaultLoader(), toSetAccessible);
    }

    public static FastField create(Field field, MemberLoadable memberLoader, boolean toSetAccessible) {
        FastFieldGetter fastFieldGetter = FastFieldGetter.create(field, memberLoader, toSetAccessible);
        FastFieldSetter fastFieldSetter = Modifier.isFinal(field.getModifiers())
                ? null : FastFieldSetter.create(field, memberLoader, toSetAccessible);
        return new FastField(field, memberLoader, fastFieldGetter, fastFieldSetter);
    }

    public Object get(Object obj) throws Throwable {
        return fastFieldGetter.get(obj);
    }

    public void set(Object obj, Object value) throws Throwable {
        if (null == fastFieldSetter) {
            throw new IllegalAccessException("unexpected set of a final field: " + field.toString());
        }
        fastFieldSetter.set(obj, value);
    }

    @Override
    public FastClass<?> getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public int getModifiers() {
        return field.getModifiers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FastField)) return false;
        FastField fastField = (FastField) o;
        return field.equals(fastField.field) && declaringClass.equals(fastField.declaringClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, declaringClass);
    }

    @Override
    public String toString() {
        return field.toString();
    }

    private final Field field;
    private final MemberLoadable memberLoader;
    private final FastClass<?> declaringClass;
    private final FastFieldGetter fastFieldGetter;
    private final FastFieldSetter fastFieldSetter;
}
