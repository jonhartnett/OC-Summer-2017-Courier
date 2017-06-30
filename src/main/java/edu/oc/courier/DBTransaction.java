package edu.oc.courier;

import edu.oc.courier.data.User;

import java.io.Closeable;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class DBTransaction implements Closeable {
    private EntityManager manager;

    public DBTransaction(EntityManager manager){
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

    public <T> Optional<T> getAny(Class<T> cls){
        return getFirst(
            query("SELECT e FROM " + cls.getName() + " e", cls)
        );
    }

    public <T> Optional<T> getFirst(TypedQuery<T> query){
        final List<T> resultList = query.getResultList();
        if(resultList.isEmpty())
            return Optional.empty();
        return Optional.ofNullable(resultList.get(0));
    }

    public <T, T2> Optional<T> get(Class<T> cls, T2 id){
        return getWhere(cls, "id", id);
    }

    public <T> Optional<T> get(TypedQuery<T> query){
        final List<T> resultList = query.getResultList();
        if (resultList.isEmpty())
            return Optional.empty();
        if (resultList.size() != 1)
            throw new RuntimeException("Result not unique!");
        return Optional.ofNullable(resultList.get(0));
    }

    public <T> List<T> getAll(TypedQuery<T> query){
        return query.getResultList();
    }

    public <T, T2> Optional<T> getWhere(Class<T> cls, String column, T2 value){
        return get(
            where(cls, column, value)
        );
    }

    public <T> void save(T... entities){
        for(T entity : entities) {
            manager.merge(entity);
        }
    }

    public <T> void delete(T... entities){
        for(T entity : entities)
            manager.remove(entity);
    }

    public <T> TypedQuery<T> query(String qlString, Class<T> resultClass){
        return manager.createQuery(qlString, resultClass);
    }

    public <T, T2> TypedQuery<T> where(Class<T> cls, String column, T2 value){
        return query("SELECT e FROM " + cls.getName() + " e WHERE e." + column + " = :value", cls)
            .setParameter("value", value);
    }

    public Optional<User> getUser(String username){
        return getWhere(User.class, "username", username);
    }
}
