package cn.org.y24.interfaces;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDBManager<T> {
    protected Connection connection;

    public BaseDBManager(Connection connection) {
        this.connection = connection;
    }

    public abstract boolean execute(T type);

    protected List<String[]> executeQuerySQL(String SQLStatement) {
        Statement statement;
        List<String[]> result = new ArrayList<>();
        try {
            statement = connection.createStatement();
            ResultSet tables = statement.executeQuery(SQLStatement);
            ResultSetMetaData tablesMetaData = tables.getMetaData();
            int count = tablesMetaData.getColumnCount();
            String[] labelName = new String[count];
            for (int i = 1; i <= count; i++) {
                labelName[i - 1] = tablesMetaData.getColumnLabel(i);
            }
            result.add(labelName);
            while (tables.next()) {
                String[] value = new String[count];
                for (int i = 1; i <= count; i++) {
                    value[i - 1] = tables.getString(i);
                }
                result.add(value);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected int executeUpdateSQL(String SQLStatement) {
        Statement statement;
        int result;
        try {
            statement = connection.createStatement();
            result = statement.executeUpdate(SQLStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }

}
