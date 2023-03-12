package com.danzabarr.messager.server;

import java.util.Objects;

public class User
{
    public static final int GUEST = 0;
    public static final int USER = 1;
    public static final int ADMIN = 10;

    public int id;
    public String username;
    public String password;
    public int permissionLevel = GUEST;

    public User(int id, String name, String password)
    {
        this.id = id;
        this.username = name;
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "[" + id + "] " + username + " (" + password + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && username.equals(user.username);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, username);
    }
}
