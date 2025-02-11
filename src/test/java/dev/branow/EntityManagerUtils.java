package dev.branow;

import jakarta.persistence.EntityManager;

import java.util.List;

public class EntityManagerUtils {

    public static void clean(EntityManager manager) {
        manager.clear();
        if (manager.getTransaction().isActive()) {
            manager.getTransaction().rollback();
        }
    }

    public static void remove(EntityManager manager, Object entity) {
        doTransaction(manager, () -> manager.remove(entity));
    }

    public static void merge(EntityManager manager, Object entity) {
        doTransaction(manager, () -> manager.merge(entity));
    }

    public static void persist(EntityManager manager, Object entity) {
        doTransaction(manager, () -> manager.persist(entity));
    }

    public static boolean contains(EntityManager manager, Class<?> clazz, Long id) {
        return manager.find(clazz, id) != null;
    }

    public static<E> List<E> findAll(EntityManager manager, Class<E> clazz) {
        var query = String.format("SELECT e FROM %s e", clazz.getName());
        return manager.createQuery(query, clazz).getResultList();
    }

    public static long lastId(EntityManager manager, String clazz, String filed) {
        var query = String.format("SELECT MAX(e.%S) FROM %s e", filed, clazz);
        return manager.createQuery(query, Long.class).getResultList().get(0);
    }

    public static long count(EntityManager manager, Class<?> clazz) {
        var query = String.format("SELECT COUNT(e) FROM %s e", clazz.getName());
        return manager.createQuery(query, Long.class).getResultList().get(0);
    }

    public static void doTransaction(EntityManager manager, Runnable runnable) {
        manager.getTransaction().begin();
        runnable.run();
        manager.getTransaction().commit();
    }

}
