package me.sunlan.fastreflection;

import java.util.function.Supplier;

public class LazyFastField extends FastField {
    private final Supplier<FastField> supplier;
    private volatile FastField delegate;

    public LazyFastField(Supplier<FastField> supplier) {
        this.supplier = supplier;
    }

    private void lazyInit() {
        if (null != delegate) return;
        synchronized (this) {
            if (null != delegate) return;
            delegate = supplier.get();
        }
    }

    @Override
    public Object get(Object obj) throws Throwable {
        lazyInit();
        return delegate.get(obj);
    }

    @Override
    public void set(Object obj, Object value) throws Throwable {
        lazyInit();
        delegate.set(obj, value);
    }

    @Override
    public FastClass<?> getDeclaringClass() {
        lazyInit();
        return delegate.getDeclaringClass();
    }

    @Override
    public String getName() {
        lazyInit();
        return delegate.getName();
    }

    @Override
    public int getModifiers() {
        lazyInit();
        return delegate.getModifiers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LazyFastField)) return false;
        lazyInit();
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        lazyInit();
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        lazyInit();
        return delegate.toString();
    }
}
