package edu.oc.courier;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

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
}
