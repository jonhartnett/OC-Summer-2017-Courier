package edu.oc.courier;

import java.io.Closeable;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.intellij.lang.annotations.Language;

public final class DBTransaction implements Closeable {
    private EntityManager manager;

    public DBTransaction(final EntityManager manager){
        this.manager = manager;
        manager.getTransaction().begin();
    }

    public void commit(){
        manager.getTransaction().commit();
        manager = null;
    }

    @Override
    public void close(){
        if(manager != null){
            manager.getTransaction().rollback();
            manager = null;
        }
    }

    public <T> Optional<T> getAny(final Class<T> cls){
        return getFirst(
            query("SELECT e FROM " + cls.getName() + " e", cls)
        );
    }

    public <T> Optional<T> getFirst(final TypedQuery<T> query){
        final List<T> resultList = query.getResultList();
        if(resultList.isEmpty())
            return Optional.empty();
        return Optional.ofNullable(resultList.get(0));
    }

    public <T> Optional<T> get(final TypedQuery<T> query){
        final List<T> resultList = query.getResultList();
        if (resultList.isEmpty())
            return Optional.empty();
        if (resultList.size() != 1)
            throw new RuntimeException("Result not unique!");
        return Optional.ofNullable(resultList.get(0));
    }

    public <T> List<T> getAll(final TypedQuery<T> query){
        return query.getResultList();
    }

    public void save(final Object... entities){
        for(Object entity : entities) {
            manager.merge(entity);
        }
    }

    public void delete(final Object... entities){
        for(Object entity : entities) {
            manager.remove(entity);
        }
    }

    public <T> TypedQuery<T> query(@Language("HQL") final String qlString, final Class<T> resultClass){
        return manager.createQuery(qlString, resultClass);
    }
}
