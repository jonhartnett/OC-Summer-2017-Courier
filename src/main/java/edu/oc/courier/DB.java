package edu.oc.courier;

import edu.oc.courier.data.User;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public final class DB {

    public static final EntityManager entityManager = Persistence.createEntityManagerFactory("courier-types")
        .createEntityManager();

    @Nonnull
    public static EntityManager m() {
        return entityManager;
    }

    @Nonnull
    public static <T> Optional<T> first(TypedQuery<T> query) {
        final List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(resultList.get(0));
    }

    @Nonnull
    public static <T> Optional<T> single(TypedQuery<T> query) {
        final List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }

        if (resultList.size() != 1) {
            throw new RuntimeException("Result not unique!");
        }

        return Optional.of(resultList.get(0));
    }

    public static void save(Object thing) {
        entityManager.getTransaction().begin();
        entityManager.persist(thing);
        entityManager.getTransaction().commit();
    }

    public static void saveMulti(Object... things) {
        entityManager.getTransaction().begin();
        for(Object thing: things) {
            entityManager.persist(thing);
        }
        entityManager.getTransaction().commit();
    }

    public static Optional<User> getUser(String username) {
        return DB.single(DB.m().createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username));
    }
}
