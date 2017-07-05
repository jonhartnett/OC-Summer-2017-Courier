package edu.oc.courier.util;

import com.google.common.base.CaseFormat;
import edu.oc.courier.Tuple;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class Table<T> {
    private static HashMap<Class, Table> tableLookup;
    private static PreparedStatement clearForeignCheckFlag;
    private static PreparedStatement setForeignCheckFlag;
    static {
        tableLookup = new HashMap<>();
        try{
            clearForeignCheckFlag = DB.connection().prepareStatement("SET FOREIGN_KEY_CHECKS = 0");
            setForeignCheckFlag = DB.connection().prepareStatement("SET FOREIGN_KEY_CHECKS = 1");
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    private static class Accessor{
        public PreparedStatement getter;
        public PreparedStatement setter;
        public PreparedStatement deleter;
    }
    static class FieldMeta{
        public Field field;
        public boolean isID;
        public boolean isUnique;
        public boolean isForeign;
        public boolean isForeignMulti;
        private String fieldName;

        public FieldMeta(Field field){
            field.setAccessible(true);
            this.field = field;
            this.fieldName = "`" + toSnake(field.getName()) + "`";
            this.isID = field.isAnnotationPresent(Id.class);
            this.isUnique = field.isAnnotationPresent(Unique.class);
            this.isForeign = getType().isAnnotationPresent(Savable.class);
            this.isForeignMulti = getType() == List.class
                    || getType() == Map.class
                    || getType() == Set.class;
            if(isID && getType() != int.class)
                throw new RuntimeException(field.getDeclaringClass().getName() + " id must be an int, not " + field.getType().getName());
            if(isID && isUnique)
                throw new RuntimeException(field.getDeclaringClass().getName() + " is already an id. Unique is redundant.");
        }

        public Class getType(){
            return field.getType();
        }

        public Class[] getSubTypes(){
            Type[] types = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
            Class[] classes = new Class[types.length];
            for(int i = 0; i < types.length; i++)
                classes[i] = (Class)types[i];
            return classes;
        }

        public String getJoinName(String table){
            return (table + fieldName).replaceFirst("``", "_");
        }
    }
    static class ClassMeta<T>{
        private static Collector<CharSequence, ?, String> comma = Collectors.joining(", ");
        private static Collector<CharSequence, ?, String> and = Collectors.joining(" AND ");

        Class<T> clazz;
        List<FieldMeta> all;
        FieldMeta id;
        List<FieldMeta> simple;
        List<FieldMeta> foreign;
        List<FieldMeta> foreignMulti;
        String tableName;

        ClassMeta(Class<T> clazz){
            this.clazz = clazz;
            this.tableName = "`" + toSnake(clazz.getSimpleName()) + "`";
            this.all = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class))
                .map(FieldMeta::new)
                .collect(Collectors.toList());
            List<FieldMeta> ids = this.all.stream().filter(meta -> meta.isID).collect(Collectors.toList());
            if(ids.size() == 0)
                throw new RuntimeException("Class " + clazz.getName() + " does not have an id.");
            if(ids.size() > 1)
                throw new RuntimeException("Class " + clazz.getName() + " has more than one id.");
            this.id = ids.get(0);
            this.simple = this.all.stream().filter(meta -> !meta.isID && !meta.isForeign && !meta.isForeignMulti).collect(Collectors.toList());
            this.foreign = this.all.stream().filter(meta -> meta.isForeign).collect(Collectors.toList());
            this.foreignMulti = this.all.stream().filter(meta -> meta.isForeignMulti).collect(Collectors.toList());
        }

        private void getFields(String name, List<String> fields, List<String> tables, List<String> wheres){
            tables.add(tableName + " " + name);
            fields.add(name + "." + id.fieldName);
            for(FieldMeta fmeta : simple)
                fields.add(name + "." + fmeta.fieldName);
            for(FieldMeta fmeta : foreign){
                Table subTable = Table.from(fmeta.field.getType());
                String subName = "T" + tables.size();
                wheres.add(name + "." + fmeta.fieldName + "=" + subName + "." + subTable.meta.id.fieldName);
                subTable.meta.getFields(subName, fields, tables, wheres);
            }
        }
        private void getFieldsFromSubType(Class clazz, String fieldName, List<String> fields, List<String> tables, List<String> wheres){
            String name = "T" + tables.size();
            if(clazz.isAnnotationPresent(Savable.class)){
                ClassMeta valueMeta = Table.from(clazz).meta;
                wheres.add("T0."+fieldName+"="+name+"." + valueMeta.id.fieldName);
                valueMeta.getFields(name, fields, tables, wheres);
            }else{
                fields.add("T0."+fieldName);
            }
        }

        private String getSqlForJoinPart(Class clazz, String name){
            if(clazz.isAnnotationPresent(Savable.class)){
                Table keyTable = Table.from(clazz);
                return name+" INT, " +
                        "FOREIGN KEY ("+name+") " +
                        "REFERENCES "+keyTable.meta.tableName+"("+keyTable.meta.id.fieldName+"), ";
            }else{
                DBType keyType = DBType.from(clazz);
                return name+" "+keyType.name+", ";
            }
        }

        List<PreparedStatement> createCreate() throws SQLException {
            List<String> sqls = new ArrayList<>();
            String fields = all.stream()
                .map(meta -> {
                    if(meta.isForeignMulti){
                        String joinTableName = meta.getJoinName(tableName);
                        Class joinType = meta.getType();
                        Class[] subTypes = meta.getSubTypes();
                        String sql;
                        if(joinType == Set.class){
                            Class valueClass = subTypes[0];
                            sql = "CREATE TABLE IF NOT EXISTS "+joinTableName+" (" +
                                "`parent` INT, " +
                                "FOREIGN KEY (`parent`) REFERENCES "+tableName+"("+id.fieldName+"), " +
                                getSqlForJoinPart(valueClass, "`value`") +
                                "CONSTRAINT PRIMARY KEY (`parent`, `value`)" +
                            ")";

                        }else{
                            Class keyClass = subTypes.length == 1 ? int.class : subTypes[0];
                            Class valueClass = subTypes.length == 1 ? subTypes[0] : subTypes[1];
                            sql = "CREATE TABLE IF NOT EXISTS "+joinTableName+" (" +
                                "`parent` INT, " +
                                "FOREIGN KEY (`parent`) REFERENCES "+tableName+"("+id.fieldName+"), " +
                                getSqlForJoinPart(keyClass, "`key`") +
                                getSqlForJoinPart(valueClass, "`value`") +
                                "CONSTRAINT PRIMARY KEY (`parent`, `key`)" +
                            ")";
                        }
                        sqls.add(sql);
                        return null;
                    }else{
                        DBType type = DBType.from(meta.field.getType());
                        if(type == null)
                            throw new RuntimeException("Unsupported column type " + meta.field.getType().getName());
                        if(meta.isForeign){
                            Table foreignTable = Table.from(meta.field.getType());
                            return meta.fieldName + " INT NULL, " +
                                    "FOREIGN KEY (" + meta.fieldName + ") " +
                                    "REFERENCES " + foreignTable.meta.tableName + "(" + foreignTable.meta.id.fieldName + ")";
                        }else{
                            String constraints;
                            if(meta.isID)
                                constraints = "AUTO_INCREMENT PRIMARY KEY";
                            else
                                constraints = "NULL";
                            String line = meta.fieldName + " " + type.name + " " + constraints;
                            if(meta.isUnique)
                                line += ", CONSTRAINT UNIQUE ("+meta.fieldName+")";
                            return line;
                        }
                    }
                })
                .filter(Objects::nonNull)
                .collect(comma);
            String sql = "CREATE TABLE IF NOT EXISTS "+tableName+" ("+fields+")";
            sqls.add(0, sql);
            List<PreparedStatement> statements = new ArrayList<>();
            for(String str : sqls){
                System.out.println(str);
                statements.add(DB.connection().prepareStatement(str));
            }
            return statements;
        }
        List<PreparedStatement> createDrop() throws SQLException{
            List<String> sqls = new ArrayList<>();
            for(FieldMeta meta : foreignMulti){
                String joinTableName = (tableName + meta.fieldName).replaceFirst("``", "");
                String sql = "DROP TABLE IF EXISTS "+joinTableName;
                sqls.add(sql);
            }

            sqls.add("DROP TABLE IF EXISTS "+tableName);

            List<PreparedStatement> statements = new ArrayList<>();
            for(String str : sqls){
                System.out.println(str);
                statements.add(DB.connection().prepareStatement(str));
            }
            return statements;
        }
        PreparedStatement createGet() throws SQLException{
            List<String> fieldList = new ArrayList<>();
            List<String> tableList = new ArrayList<>();
            List<String> whereList = new ArrayList<>();
            whereList.add("T0." + id.fieldName + "=?");

            this.getFields("T0", fieldList, tableList, whereList);

            String fields = fieldList.stream().collect(comma);
            String tables = tableList.stream().collect(comma);
            String wheres = whereList.stream().collect(and);

            String sql = "SELECT "+fields+" FROM "+tables+" WHERE "+wheres;
            System.out.println(sql);
            return DB.connection().prepareStatement(sql);
        }
        PreparedStatement createGetAll() throws SQLException{
            List<String> fieldList = new ArrayList<>();
            List<String> tableList = new ArrayList<>();
            List<String> whereList = new ArrayList<>();

            this.getFields("T0", fieldList, tableList, whereList);

            String fields = fieldList.stream().collect(comma);
            String tables = tableList.stream().collect(comma);
            String wheres = whereList.stream().collect(and);

            String sql = "SELECT "+fields+" FROM "+tables;
            if(wheres != null && !wheres.isEmpty())
                sql += " WHERE " + wheres;
            System.out.println(sql);
            return DB.connection().prepareStatement(sql);
        }
        Tuple<String, Boolean> createGetCustom() throws SQLException{
            List<String> fieldList = new ArrayList<>();
            List<String> tableList = new ArrayList<>();
            List<String> whereList = new ArrayList<>();

            this.getFields("T0", fieldList, tableList, whereList);

            String fields = fieldList.stream().collect(comma);
            String tables = tableList.stream().collect(comma);
            String wheres = whereList.stream().collect(and);

            String sql = "SELECT "+fields+" FROM "+tables;
            boolean hasWheres = wheres != null && !wheres.isEmpty();
            if(hasWheres)
                sql += " WHERE " + wheres;
            System.out.println(sql);
            return new Tuple<>(sql, hasWheres);
        }
        PreparedStatement createInsert() throws SQLException{
            String fields = join(simple.stream(), foreign.stream())
                    .map(meta -> meta.fieldName)
                    .collect(Collectors.joining(", "));
            String variables = join(simple.stream(), foreign.stream())
                    .map(meta -> "?")
                    .collect(Collectors.joining(", "));
            String sql = "INSERT INTO "+tableName+" ("+fields+") VALUES (" + variables + ")";
            System.out.println(sql);
            return DB.connection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        }
        PreparedStatement createUpdate() throws SQLException{
            String updates = join(simple.stream(), foreign.stream())
                .map(meta -> meta.fieldName + "=?")
                .collect(Collectors.joining(", "));
            String sql = "UPDATE "+tableName+" SET " + updates + " WHERE "+id.fieldName+"=?";
            System.out.println(sql);
            return DB.connection().prepareStatement(sql);
        }
        PreparedStatement createDelete() throws SQLException{
            String sql = "DELETE FROM "+tableName+" WHERE "+id.fieldName+"=?";
            System.out.println(sql);
            return DB.connection().prepareStatement(sql);
        }
        HashMap<FieldMeta, Accessor> createSubqueries() throws SQLException{
            HashMap<FieldMeta, Accessor> statements = new HashMap<>();
            for(FieldMeta meta : foreignMulti){
                Class joinType = meta.getType();
                Class[] subTypes = meta.getSubTypes();

                List<String> fieldList = new ArrayList<>();
                List<String> tableList = new ArrayList<>();
                List<String> whereList = new ArrayList<>();

                tableList.add(meta.getJoinName(tableName) + " T0");
                whereList.add("T0.`parent`=?");

                String setterSql;
                if(joinType == Set.class){
                    Class valueClass = subTypes[0];

                    getFieldsFromSubType(valueClass, "`value`", fieldList, tableList, whereList);

                    setterSql = "INSERT INTO "+meta.getJoinName(tableName)+"(`parent`, `value`) VALUES (?, ?)";
                }else{
                    Class keyClass = subTypes.length == 1 ? int.class : subTypes[0];
                    Class valueClass = subTypes.length == 1 ? subTypes[0] : subTypes[1];

                    getFieldsFromSubType(keyClass, "`key`", fieldList, tableList, whereList);
                    getFieldsFromSubType(valueClass, "`value`", fieldList, tableList, whereList);

                    setterSql = "INSERT INTO "+meta.getJoinName(tableName)+"(`parent`, `key`, `value`) VALUES (?, ?, ?)";
                }

                String fields = fieldList.stream().collect(comma);
                String tables = tableList.stream().collect(comma);
                String wheres = whereList.stream().collect(and);

                String getterSql = "SELECT "+fields+" FROM "+tables+" WHERE "+wheres;
                String deleterSql = "DELETE FROM "+meta.getJoinName(tableName)+" WHERE `parent`=?";
                System.out.println(getterSql);
                System.out.println(setterSql);
                System.out.println(deleterSql);
                Accessor access = new Accessor();
                access.getter = DB.connection().prepareStatement(getterSql);
                access.setter = DB.connection().prepareStatement(setterSql);
                access.deleter = DB.connection().prepareStatement(deleterSql);
                statements.put(meta, access);
            }
            return statements;
        }
    }
    public class CustomQuery{
        private String where;
        private String orderBy;
        private boolean paginated = false;
        private PreparedStatement statement;

        private CustomQuery(){}

        public CustomQuery where(String where){
            if(statement != null)
                throw new RuntimeException("Cannot modify built query.");
            this.where = where;
            return this;
        }

        public CustomQuery paginated(){
            if(statement != null)
                throw new RuntimeException("Cannot modify built query.");
            paginated = true;
            return this;
        }

        public CustomQuery orderBy(String orderBy){
            if(statement != null)
                throw new RuntimeException("Cannot modify built query.");
            this.orderBy = orderBy;
            return this;
        }

        public CustomQuery build(){
            if(statement == null){
                try {
                    String sql = Table.this.getCustom.x;
                    if(where != null){
                        if(Table.this.getCustom.y)
                            sql += " AND " + where;
                        else
                            sql += " WHERE " + where;
                    }
                    if(orderBy != null)
                        sql += " ORDER BY " + orderBy;
                    if(paginated)
                        sql += " LIMIT ?,?";
                    statement = DB.connection().prepareStatement(sql);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return this;
        }

        public Stream<T> execute(Object ...args){
            return executePage(-1, -1, args);
        }

        public Stream<T> executePage(long page, long pageSize, Object ...args){
            if(page == -1 || pageSize == -1){
                if(paginated)
                    throw new RuntimeException("Must specify page for paginated query.");
            }else{
                if(!paginated)
                    throw new RuntimeException("Cannot specify page for unpaginated query.");
            }
            try{
                build();
                Index i = new Index();
                for(Object arg : args){
                    DBType type = DBType.from(arg.getClass());
                    type.setter.set(statement, i, arg);
                }
                if(paginated){
                    statement.setLong(i.x++, page * pageSize);
                    statement.setLong(i.x++, pageSize);
                }
                ResultSet results = statement.executeQuery();
                return Table.this.resultSetToMulti(results, new TupleMap<>(), null);
            }catch(SQLException ex){
                throw new RuntimeException(ex);
            }

        }
    }

    public static <T> Table<T> from(Class<T> clazz){
        Table<T> table = tableLookup.getOrDefault(clazz, null);
        if(table == null)
            table = new Table<>(clazz);
        return table;
    }

    ClassMeta<T> meta;
    private List<PreparedStatement> create;
    private List<PreparedStatement> drop;
    private PreparedStatement get;
    private PreparedStatement getAll;
    private Tuple<String, Boolean> getCustom;
    private PreparedStatement insert;
    private PreparedStatement update;
    private PreparedStatement delete;
    private HashMap<FieldMeta, Accessor> subqueries;

    private Table(Class<T> clazz){
        tableLookup.put(clazz, this);
        this.meta = new ClassMeta<>(clazz);
        try {
            this.create = meta.createCreate();
            this.drop = meta.createDrop();
            this.get = meta.createGet();
            this.getAll = meta.createGetAll();
            this.getCustom = meta.createGetCustom();
            this.insert = meta.createInsert();
            this.update = meta.createUpdate();
            this.delete = meta.createDelete();
            this.subqueries = meta.createSubqueries();
        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    private static void runUpdates(List<PreparedStatement> statements){
        try {
            for(PreparedStatement statement : statements)
                statement.executeUpdate();
            DB.connection().commit();
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        }
    }

    public static void create(Table ...tables){
        List<PreparedStatement> statements = new ArrayList<>();
        for(Table table : tables)
            table.addCreates(statements);
        removeDuplicates(statements);
        runUpdates(statements);
    }
    public static void drop(Table ...tables){
        List<PreparedStatement> statements = new ArrayList<>();
        for(Table table : tables)
            table.addDrops(statements);
        statements.add(0, clearForeignCheckFlag);
        statements.add(setForeignCheckFlag);
        runUpdates(statements);
    }
    private void addCreates(List<PreparedStatement> list){
        for(FieldMeta fmeta : meta.foreign){
            Table table = Table.from(fmeta.field.getType());
            table.addCreates(list);
        }
        for(FieldMeta fmeta : meta.foreignMulti){
            for(Class clazz : fmeta.getSubTypes()){
                if(clazz.isAnnotationPresent(Savable.class) && clazz != meta.clazz){
                    Table table = Table.from(clazz);
                    table.addCreates(list);
                }
            }
        }
        list.addAll(create);
    }
    private void addDrops(List<PreparedStatement> list){
        list.addAll(drop);
    }

    public Optional<T> get(int id){
        return Optional.ofNullable(get(id, new TupleMap<>(), null));
    }
    private T get(int id, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue){
        try{
            get.setInt(1, id);
            ResultSet results = get.executeQuery();
            if(results.next()){
                return resultSetToSingle(results, cache, subqueryQueue);
            }else{
                return null;
            }
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    public Stream<T> getAll(){
        try{
            ResultSet results = getAll.executeQuery();
            return resultSetToMulti(results, new TupleMap<>(), null);
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }
    public CustomQuery getCustom(){
        return new CustomQuery();
    }
    public void set(T obj){
        set(obj, new HashSet<>(), true);
    }
    private void set(T obj, HashSet<Object> cache, boolean commit){
        try{
            if(cache.contains(obj))
                return;
            cache.add(obj);

            int id = (int)meta.id.field.get(obj);

            PreparedStatement statement = (id != 0) ? update : insert;

            Index i = new Index();
            for(FieldMeta fmeta : meta.simple){
                DBType type = DBType.from(fmeta.field.getType());
                Object value = fmeta.field.get(obj);
                type.setter.set(statement, i, value);
            }
            for(FieldMeta fmeta : meta.foreign){
                Table table = Table.from(fmeta.field.getType());
                Object value = fmeta.field.get(obj);
                if(value != null){
                    table.set(value, cache, false);
                    statement.setInt(i.x++, (int)table.meta.id.field.get(value));
                }else{
                    statement.setNull(i.x++, Types.INTEGER);
                }
            }

            if(id == 0){
                insert.executeUpdate();
                ResultSet results = insert.getGeneratedKeys();
                results.next();
                id = results.getInt(1);
                meta.id.field.set(obj, id);
            }else{
                update.setInt(i.x, id);
                update.executeUpdate();
            }

            for(FieldMeta fmeta : meta.foreignMulti){
                Class[] subTypes = fmeta.getSubTypes();
                PreparedStatement setter = subqueries.get(fmeta).setter;
                PreparedStatement deleter = subqueries.get(fmeta).deleter;

                deleter.setInt(1, id);
                deleter.executeUpdate();

                if(fmeta.getType() == Set.class){
                    Set set = (Set)fmeta.field.get(obj);
                    Class valueClass = subTypes[0];
                    DBType valueType = DBType.from(valueClass);

                    if(valueClass.isAnnotationPresent(Savable.class)){
                        Table table = Table.from(valueClass);
                        for(Object subObj : set)
                            table.set(subObj, cache, false);
                    }

                    for(Object subObj : set){
                        i.reset();
                        setter.setInt(i.x++, id);
                        valueType.setter.set(setter, i, subObj);
                        System.out.println(setter);
                        setter.executeUpdate();
                    }
                }else
                if(fmeta.getType() == List.class){
                    List list = (List)fmeta.field.get(obj);
                    Class valueClass = subTypes[0];
                    DBType valueType = DBType.from(valueClass);

                    if(valueClass.isAnnotationPresent(Savable.class)){
                        Table table = Table.from(valueClass);
                        for(Object subObj : list)
                            table.set(subObj, cache, false);
                    }

                    int index = 0;
                    for(Object subObj : list){
                        i.reset();
                        setter.setInt(i.x++, id);
                        setter.setInt(i.x++, index);
                        valueType.setter.set(setter, i, subObj);
                        setter.executeUpdate();
                    }
                }else{
                    Map map = (Map)fmeta.field.get(obj);
                    Class keyClass = subTypes[0];
                    DBType keyType = DBType.from(keyClass);
                    Class valueClass = subTypes[1];
                    DBType valueType = DBType.from(valueClass);

                    if(keyClass.isAnnotationPresent(Savable.class)){
                        Table table = Table.from(keyClass);
                        for(Object subObj : map.keySet())
                            table.set(subObj, cache, false);
                    }

                    if(valueClass.isAnnotationPresent(Savable.class)){
                        Table table = Table.from(valueClass);
                        for(Object subObj : map.values())
                            table.set(subObj, cache, false);
                    }

                    for(Map.Entry entry : (Set<Map.Entry<Object, Object>>)map.entrySet()){
                        i.reset();
                        setter.setInt(i.x++, id);
                        keyType.setter.set(setter, i, entry.getKey());
                        valueType.setter.set(setter, i, entry.getValue());
                        setter.executeUpdate();
                    }
                }
            }

            if(commit)
                DB.connection().commit();
        }catch(SQLException | IllegalAccessException ex){
            throw new RuntimeException(ex);
        }
    }
    public void delete(T obj){
        try {
            int id = (int)meta.id.field.get(obj);
            if(id == 0)
                throw new RuntimeException("Cannot delete unsaved object "+obj);
            delete.setInt(1, id);
            delete.executeUpdate();
        } catch (SQLException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void getAndSet(FieldMeta meta, ResultSet results, Index i, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue, T obj) throws SQLException, IllegalAccessException {
        DBType type = DBType.from(meta.field.getType());
        Object value = type.getter.get(results, i, cache, subqueryQueue);
        meta.field.set(obj, value);
    }

    private T resultSetToSingle(ResultSet results, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue) throws SQLException {
        return resultSetToSingle(results, new Index(), cache, subqueryQueue);
    }
    T resultSetToSingle(ResultSet results, Index i, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue){
        try{
            int id = results.getInt(i.x++);
            T obj = (T)cache.getOrDefault(meta.clazz, id, null);
            if(obj != null)
                return obj;
            obj = meta.clazz.newInstance();
            cache.put(meta.clazz, id, obj);
            meta.id.field.set(obj, id);
            for(FieldMeta fmeta : meta.simple)
                getAndSet(fmeta, results, i, cache, subqueryQueue, obj);
            for(FieldMeta fmeta : meta.foreign)
                getAndSet(fmeta, results, i, cache, subqueryQueue, obj);

            if(subqueryQueue == null){
                subqueryQueue = new LinkedList<>();
                subqueryQueue.add(obj);
                HashSet done = new HashSet();
                while(true){
                    Object next = subqueryQueue.poll();
                    if(next == null)
                        break;
                    if(done.contains(next))
                        continue;
                    performSubqueries(next, cache, subqueryQueue);
                    done.add(next);
                }
            }else{
                subqueryQueue.add(obj);
            }

            return obj;
        }catch(SQLException | IllegalAccessException | InstantiationException ex){
            throw new RuntimeException(ex);
        }
    }
    private Stream<T> resultSetToMulti(ResultSet results, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue) throws SQLException {
        Stream.Builder<T> builder = Stream.builder();
        while(results.next())
            builder.add(resultSetToSingle(results, cache, subqueryQueue));
        return builder.build();
    }

    private void performSubqueries(Object obj, TupleMap<Class, Integer, Object> cache, Queue<Object> subqueryQueue) throws IllegalAccessException, SQLException {
        for(FieldMeta fmeta : meta.foreignMulti){
            Class[] subTypes = fmeta.getSubTypes();
            PreparedStatement statement = subqueries.get(fmeta).getter;
            statement.setInt(1, (int)meta.id.field.get(obj));
            ResultSet subResults = statement.executeQuery();
            if(fmeta.getType() == Set.class){
                Class valueType = subTypes[0];
                Index index = new Index();
                DBType valueDBType = DBType.from(valueType);
                Set set = new HashSet();
                while(subResults.next()){
                    index.reset();
                    set.add(valueDBType.getter.get(subResults, index, cache, subqueryQueue));
                }
                fmeta.field.set(obj, set);
            }else{
                Class keyClass = subTypes.length == 1 ? int.class : subTypes[0];
                Class valueClass = subTypes.length == 1 ? subTypes[0] : subTypes[1];

                Index index = new Index();
                DBType valueDBType = DBType.from(valueClass);
                DBType keyDBType = DBType.from(keyClass);

                if(fmeta.getType() == Map.class){
                    Map map = new HashMap();
                    while(subResults.next()){
                        index.reset();
                        Object key = keyDBType.getter.get(subResults, index, cache, subqueryQueue);
                        Object value = valueDBType.getter.get(subResults, index, cache, subqueryQueue);
                        map.put(key, value);
                    }
                    fmeta.field.set(obj, map);
                }else{
                    List list = new ArrayList();
                    while(subResults.next()){
                        index.reset();
                        int key = (int)keyDBType.getter.get(subResults, index, cache, subqueryQueue);
                        Object value = valueDBType.getter.get(subResults, index, cache, subqueryQueue);
                        list.add(key, value);
                    }
                    fmeta.field.set(obj, list);
                }
            }
        }
    }

    private static <T> Stream<T> join(Stream<T> stream, Stream<T> ...streams){
        for(Stream<T> stream2 : streams)
            stream = Stream.concat(stream, stream2);
        return stream;
    }
    private static <T> void removeDuplicates(List<T> list){
        Set<T> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
    }
    private static String toSnake(String str){
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
    }
}
