package com.danzabarr.messager.server;

import com.mysql.cj.util.StringUtils;
import org.mindrot.main.BCrypt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database extends MySQLConnection
{
    public Database() throws SQLException
    {
        super();
    }

    public User registerUser(String username, String password)
        throws SQLException
    {
        password = BCrypt.hashpw(password, BCrypt.gensalt());

        {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO user (username, password)  VALUES (?,?)");
            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            stmt.close();
        }

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE username = ? LIMIT 1");
        stmt.setString(1, username);

        ResultSet result = stmt.executeQuery();

        if (!result.next())
            throw new SQLException("User not added to the database.");

        User user = construct(result);
        stmt.close();

        return user;
    }

    private static User construct(ResultSet resultSet)
            throws SQLException
    {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        String password =resultSet.getString("password");

        return new User(id, username, password);
    }

    public User getUser(String field, String value) throws SQLException
    {
        String query = "SELECT * from user where " + field + " = '" + value + "' LIMIT 1";
        ResultSet resultSet = executeQuery(query);

        if (!resultSet.next())
            throw new SQLException("User not found.");


        User user = construct(resultSet);

        return user;
    }

    public User getUser(int id) throws SQLException
    {
        String query = "SELECT * from user where id = '" + id + "' LIMIT 1";
        ResultSet resultSet = executeQuery(query);

        if (!resultSet.next())
            throw new SQLException("User not found.");

        User user = construct(resultSet);

        return user;
    }

    public User getUser(String username) throws SQLException
    {
        String query = "SELECT * from user where username = '" + username + "' LIMIT 1";
        ResultSet resultSet = executeQuery(query);

        if (!resultSet.next())
            throw new SQLException("User not found.");

        int id = resultSet.getInt("id");
        String password = resultSet.getString("password");

        return new User(id, username, password);
    }

    public User getUserCheckPassword(String username, String password) throws SQLException
    {
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user WHERE username = ? LIMIT 1");
        stmt.setString(1, username);

        ResultSet result = stmt.executeQuery();

        if (!result.next())
            throw new SQLException("A user with that name does not exist.");

        if (!BCrypt.checkpw(password, result.getString("password")))
            throw new SQLException("Password is incorrect.");

        User user = construct(result);

        return user;
    }

    public boolean userExists(String username) throws SQLException
    {
        String query = "SELECT 1 FROM user WHERE username = '" + username + "' LIMIT 1";

        ResultSet resultSet = executeQuery(query);

        boolean exists = resultSet.next();
        resultSet.close();

        return exists;
    }

    public boolean userExists(int id) throws SQLException
    {
        String query = "SELECT 1 FROM user WHERE id = '" + id + "' LIMIT 1";

        ResultSet resultSet = executeQuery(query);

        boolean exists = resultSet.next();
        resultSet.close();

        return exists;
    }

    public void changeUsername(String oldUsername, String newUsername) throws SQLException
    {
        if (!validateUsername(newUsername))
            throw new SQLException("Username is invalid.");

        if (userExists(newUsername))
            throw new SQLException("A user with that name already exists.");

        getUser(oldUsername);
        String query = "UPDATE user SET username = '" + newUsername + "' WHERE username = '" + oldUsername + "'";
        executeUpdate(query);
    }

    public void setUsername(int id, String username) throws SQLException
    {
        if (!validateUsername(username))
            throw new SQLException("Username is invalid.");

        if (userExists(username))
            throw new SQLException("A user with that name already exists.");

        getUser(id);
        String query = "UPDATE user SET username = '" + username + "' WHERE id = '" + id + "'";
        executeUpdate(query);
    }

    public void changePassword(String username, String password)
            throws SQLException
    {
        if (!validatePassword(password))
            throw new SQLException("Password is invalid.");

        if (!userExists(username))
            throw new SQLException("User does not exist.");

        String salt = BCrypt.gensalt();
        String query = "UPDATE user SET password = '" + password + "', salt = '"+ salt + "' WHERE username = '" + username + "'";
        executeUpdate(query);
    }

    public void setPassword(int id, String password) throws SQLException
    {
        if (!validatePassword(password))
            throw new SQLException("Password is invalid.");

        if (!userExists(id))
            throw new SQLException("User does not exist.");

        String query = "UPDATE user SET password = '" + password + "' WHERE id = '" + id + "'";
        executeUpdate(query);
    }

    public boolean checkPassword(String username, String password) throws SQLException
    {
        String query1 = "SELECT * from user where username = '" + username + "' LIMIT 1";
        ResultSet resultSet1 = executeQuery(query1);

        if (!resultSet1.next())
        {
            resultSet1.close();
            throw new SQLException("User not found.");
        }

        resultSet1.close();

        String query2 = "SELECT * from user where username = '" + username + "' and password = '" + password + "' LIMIT 1";
        ResultSet resultSet2 = executeQuery(query2);

        if (!resultSet2.next())
        {
            resultSet2.close();
            return false;
        }
        resultSet2.close();

        return true;
    }

    public static boolean validateUsername(String username)
    {
        return !StringUtils.isNullOrEmpty(username) && isAlphanumeric(username);
    }

    /*
        TODO: Need client-side validation too.
     */
    public static boolean validatePassword(String password)
    {
        return password != null;
    }

    public static boolean isAlphanumeric(String str)
    {
        char[] charArray = str.toCharArray();
        for (char c : charArray)
        {
            if (!Character.isLetterOrDigit(c))
                return false;
        }
        return true;
    }
}
