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

    public static DBTransaction getTransaction(){
        return new DBTransaction(entityManager);
    }

    @Nonnull
    public static <T> Optional<T> first(final TypedQuery<T> query) {
        final List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(resultList.get(0));
    }

    @Nonnull
    public static <T> Optional<T> single(final TypedQuery<T> query) {
        final List<T> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return Optional.empty();
        }

        if (resultList.size() != 1) {
            throw new RuntimeException("Result not unique!");
        }

        return Optional.of(resultList.get(0));
    }

    public static void save(final Object... things) {
        entityManager.getTransaction().begin();
        for(Object thing: things) {
            entityManager.merge(thing);
        }
        entityManager.getTransaction().commit();
    }

    public static Optional<User> getUser(final String username) {
        return DB.single(DB.m().createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username));
    }
}
