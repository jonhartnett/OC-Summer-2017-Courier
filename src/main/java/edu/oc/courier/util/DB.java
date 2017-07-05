package edu.oc.courier.util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import edu.oc.courier.data.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

public class DB {
    private static Connection conn = null;

    static{
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setDatabaseName("oc-summer-2017-courier");
            dataSource.setServerName("localhost");
            dataSource.setPortNumber(3306);
            dataSource.setUser("application");
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        DBType.addEnumType(RouteCondition.class);
        DBType.addEnumType(UserType.class);

        Table.create(
            Table.from(Client.class),
            Table.from(Courier.class),
            Table.from(Invoice.class),
            Table.from(Node.class),
            Table.from(SystemInfo.class),
            Table.from(Ticket.class),
            Table.from(User.class)
        );
    }

    public static Connection connection(){
        return conn;
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<T> query(Class<T> resultClass, String query, Object ...args){
        try{
            PreparedStatement state = DB.connection().prepareStatement(query);
            Index i = new Index();
            for(Object arg : args){
                DBType type = DBType.from(arg.getClass());
                type.setter.set(state, i, arg);
            }
            ResultSet results = state.executeQuery();
            Stream.Builder<T> builder = Stream.builder();
            DBType resultType = DBType.from(resultClass);
            while(results.next()){
                i.reset();
                builder.add((T)resultType.getter.get(results, i, null, null));
            }
            return builder.build();
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
    }
}
