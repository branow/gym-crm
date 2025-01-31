package dev.branow.storage;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Reference<T> {
    protected final Type _type;

    protected Reference() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Reference constructed without actual type information");
        } else {
            this._type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    public Type getType() {
        return this._type;
    }
}
