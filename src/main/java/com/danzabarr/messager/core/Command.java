package com.danzabarr.messager.core;

public class Command
{
    public Request request;
    public CommandPattern pattern;
    public String[] arguments;

    public Command(Request request, CommandPattern pattern, String[] arguments)
    {
        this.request = request;
        this.pattern = pattern;
        this.arguments = arguments;
    }

    public String toString()
    {
        String str = '/' + pattern.name + ' ';

        for (int i = 0; i < arguments.length; i++)
        {
            str += '<' + pattern.arguments[i][1] + ">: \"" + arguments[i] + '\"';
            if (i < arguments.length - 1)
                str += ", ";
        }

        return str;
    }
}
