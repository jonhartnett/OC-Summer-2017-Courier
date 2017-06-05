package edu.oc.courier;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public final class DB {

    public static final EntityManager entityManager = Persistence.createEntityManagerFactory("courier-types")
        .createEntityManager();

    public static EntityManager m() {
        return entityManager;
    }
}
