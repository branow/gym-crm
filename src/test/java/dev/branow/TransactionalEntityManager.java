package dev.branow;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.function.Consumer;

public class TransactionalEntityManager {

    private final EntityManager manager;

    public TransactionalEntityManager(EntityManager manager) {
        this.manager = manager;
    }

    public void flush() {
        manager.flush();
    }

    public<T> T find(Class<T> clazz, Object id) {
        return manager.find(clazz, id);
    }

    public void remove(Object entity) {
        doTransaction(manager -> manager.remove(entity));
    }

    public void merge(Object entity) {
        doTransaction(manager -> manager.merge(entity));
    }

    public void persist(Object entity) {
        doTransaction(manager -> manager.persist(entity));
    }

    public boolean contains(Class<?> clazz, Long id) {
        return manager.find(clazz, id) != null;
    }

    public <E> List<E> findAll(Class<E> clazz) {
        var query = String.format("SELECT e FROM %s e", clazz.getName());
        return manager.createQuery(query, clazz).getResultList();
    }

    public long lastId(String clazz, String filed) {
        var query = String.format("SELECT MAX(e.%S) FROM %s e", filed, clazz);
        return manager.createQuery(query, Long.class).getResultList().get(0);
    }

    public long count(Class<?> clazz) {
        var query = String.format("SELECT COUNT(e) FROM %s e", clazz.getName());
        return manager.createQuery(query, Long.class).getResultList().get(0);
    }

    public void doTransaction(Consumer<EntityManager> action) {
        manager.getTransaction().begin();
        action.accept(manager);
        manager.getTransaction().commit();
    }

}
