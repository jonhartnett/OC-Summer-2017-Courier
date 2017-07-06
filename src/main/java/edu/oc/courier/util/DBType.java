package edu.oc.courier.util;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@SuppressWarnings("unchecked")
public class DBType<T> {
    private static HashMap<Class, DBType> lookup = new HashMap<>();
    private static final Map<Class<?>, Class<?>> wrappers = new ImmutableMap.Builder<Class<?>, Class<?>>()
            .put(Boolean.class, boolean.class)
            .put(Byte.class, byte.class)
            .put(Character.class, char.class)
            .put(Double.class, double.class)
            .put(Float.class, float.class)
            .put(Integer.class, int.class)
            .put(Long.class, long.class)
            .put(Short.class, short.class)
            .put(Void.class, void.class)
            .build();

    public static <T> DBType<T> from(Class<T> clazz){
        clazz = (Class<T>)wrappers.getOrDefault(clazz, clazz);
        DBType<T> type = lookup.getOrDefault(clazz, null);
        if(type == null && clazz.isAnnotationPresent(Savable.class)){
            final Table<T> table = Table.from(clazz);
            type = new DBType<>("INT",
                table::resultSetToSingle,
                (statement, i, dryRun, value) -> {
                    if(dryRun){
                        i.x++;
                        return;
                    }
                    try {
                        statement.setInt(i.x++, (int)table.meta.id.field.get(value));
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            );
            lookup.put(clazz, type);
        }
        if(type == null)
            throw new RuntimeException(clazz.getName() + " is not supported by the database.");
        return type;
    }

    static {
        addType(boolean.class, "BIT", ResultSet::getBoolean, PreparedStatement::setBoolean);
        addType(byte.class, "TINYINT", ResultSet::getByte, PreparedStatement::setByte);
        addType(short.class, "SMALLINT", ResultSet::getShort, PreparedStatement::setShort);
        addType(int.class, "INT", ResultSet::getInt, PreparedStatement::setInt);
        addType(long.class, "BIGINT", ResultSet::getLong, PreparedStatement::setLong);
        addType(float.class, "FLOAT", ResultSet::getFloat, PreparedStatement::setFloat);
        addType(double.class, "DOUBLE", ResultSet::getDouble, PreparedStatement::setDouble);
        addType(String.class, "VARCHAR(255)", ResultSet::getString, PreparedStatement::setString);
        addType(byte[].class, "TINYBLOB", ResultSet::getBytes, PreparedStatement::setBytes);
        addType(BigDecimal.class, "DECIMAL(19,2)", ResultSet::getBigDecimal, PreparedStatement::setBigDecimal);
        addType(Instant.class, "DATETIME",
            (result, i) -> {
                Timestamp time = result.getTimestamp(i);
                if(time == null)
                    return null;
                else
                    return time.toInstant();
            },
            (statement, i, value) -> {
                if(value == null)
                    statement.setNull(i, Types.TIMESTAMP);
                else
                    statement.setTimestamp(i, Timestamp.from(value));
            }
        );
    }

    public interface TypeGetter<T>{
        T get(ResultSet result, int i) throws SQLException;
    }
    public interface TypeSetter<T>{
        void set(PreparedStatement statement, int i, T value) throws SQLException;
    }
    public interface ExtTypeGetter<T>{
        T get(ResultSet result, Index i, boolean dryRun, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue) throws SQLException;
    }
    public interface ExtTypeSetter<T>{
        void set(PreparedStatement statement, Index i, boolean dryRun, T value) throws SQLException;
    }

    public String name;
    public ExtTypeGetter<T> getter;
    public ExtTypeSetter<T> setter;

    public DBType(String name, ExtTypeGetter<T> getter, ExtTypeSetter<T> setter){
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public static <T> void addType(Class<T> clazz, String name, TypeGetter<T> getter, TypeSetter<T> setter){
        lookup.put(clazz,
            new DBType<>(name,
                (result, i, dryRun, cache, queue) -> {
                    if(!dryRun){
                        return getter.get(result, i.x++);
                    }else{
                        i.x++;
                        return null;
                    }
                },
                (statement, i, dryRun, value) -> {
                    if(!dryRun){
                        setter.set(statement, i.x++, (T)value);
                    }else{
                        i.x++;
                    }
                }
            )
        );
    }
    public static <T> void addLargeType(Class<T> clazz, String name, ExtTypeGetter<T> getter, ExtTypeSetter<T> setter){
        lookup.put(clazz, new DBType<>(name, getter, setter));
    }
    public static void addEnumType(Class clazz){
        if(!clazz.isEnum())
            throw new RuntimeException("Class " + clazz.getName() + " is not an enum.");
        try {
            Method method = clazz.getMethod("ordinal");
            addType(clazz, "INT",
                (result, i) -> {
                    int ordinal = result.getInt(i);
                    if(result.wasNull())
                        return null;
                    return clazz.getEnumConstants()[ordinal];
                },
                (statement, i, value) -> {
                    if(value == null){
                        statement.setNull(i, Types.INTEGER);
                        return;
                    }
                    try {
                        statement.setInt(i, (int)method.invoke(value));
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            );
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}
