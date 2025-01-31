package dev.branow.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> type, Object id) {
        super(String.format("Entity %s not found by identifier %s",
                type.getSimpleName(), id));
    }
}
