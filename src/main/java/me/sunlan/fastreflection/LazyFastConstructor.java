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

import java.util.function.Supplier;

final class LazyFastConstructor<T> extends FastConstructor<T> {
    private final Supplier<FastConstructor<T>> supplier;
    private volatile FastConstructor<T> delegate;

    public LazyFastConstructor(Supplier<FastConstructor<T>> supplier) {
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
    public Object invoke(Object... args) throws Throwable {
        lazyInit();
        return delegate.invoke(args);
    }

    @Override
    public FastClass<T> getDeclaringClass() {
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
    public boolean isVarArgs() {
        lazyInit();
        return delegate.isVarArgs();
    }

    @Override
    public FastClass<?>[] getParameterTypes() {
        lazyInit();
        return delegate.getParameterTypes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LazyFastConstructor)) return false;
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
