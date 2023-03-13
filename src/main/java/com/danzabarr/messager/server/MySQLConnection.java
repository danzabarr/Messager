package com.danzabarr.messager.server;

import java.sql.*;
import java.util.Properties;

public class MySQLConnection
{
    protected Connection connection;

    public MySQLConnection(String host_name, int port, String database, String user_name, String password)
            throws SQLException
    {
        //Class.forName("com.mysql.cj.jdbc.Driver");

        //System.setProperty("javax.net.ssl.trustStore", KEY_STORE_FILE_PATH);
        //System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASS);

        String url = "jdbc:mysql://" + host_name + ":" + port + "/" + database;

        Properties properties = new Properties();
        properties.setProperty("user", user_name);
        properties.setProperty("password", password);
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("useSSL", "false");
        properties.setProperty("sslMode", "VERIFY_IDENTITY");
        properties.setProperty("verifyServerCertificate", "false");

        connection = DriverManager.getConnection(url, properties);
        System.out.println("Connected to database: " + database);

    }

    /**
     * REMEMBER TO CLOSE THE RESULT SET AFTER USE!
     * @param query
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery(String query) throws SQLException
    {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        return rs;
    }

    public int executeUpdate(String query) throws SQLException
    {
        Statement stmt = connection.createStatement();
        int rs = stmt.executeUpdate(query);

        return rs;
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException
    {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnCount = rsmd.getColumnCount();
        int pad = 16;

        for (int i = 1; i <= columnCount; i++)
            System.out.print(padRight(rsmd.getColumnName(i), pad));

        System.out.println();

        while (resultSet.next())
        {
            for (int i = 1; i <= columnCount; i++)
                System.out.print(padRight(resultSet.getString(i), pad));
            System.out.println();
        }
    }
}
