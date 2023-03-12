package com.danzabarr.messager.core;

public class CommandPattern
{
    public final String pattern;
    public final int permissionLevel;
    public final String name;
    public final String[][] arguments;

    public CommandPattern(String pattern, int permissionLevel)
    {
        this.pattern = pattern;
        pattern += ' ';
        int firstSpace = pattern.indexOf(' ');
        String name = pattern.substring(0, firstSpace);

        String argsString = pattern.trim();
        argsString = argsString.substring(Math.min(firstSpace, argsString.length())).trim();

        String[] arguments = argsString.isEmpty() ? new String[0] : argsString.split("\s+");
        this.arguments = new String[arguments.length][];
        for (int i = 0; i < arguments.length; i++)
            this.arguments[i] = arguments[i].split(":", 2);

        this.permissionLevel = permissionLevel;
        this.name = name;
    }

    @Override
    public String toString()
    {
        return pattern;
    }

    public String[] parseArguments(String input)
            throws NoLeadingSlashException, CommandNotRecognisedException, InvalidArgumentCountException
    {
        if (!input.startsWith("/"))
            throw new NoLeadingSlashException();

        input += ' ';
        int firstSpace = input.indexOf(' ');
        String name = input.substring(1, firstSpace);

        if (!name.equals(this.name))
            throw new CommandNotRecognisedException(name);

        String argsString = input.trim();
        argsString = argsString.substring(Math.min(firstSpace, argsString.length())).trim();

        String[] arguments = argsString.isEmpty() ? new String[0] : argsString.split("\s+", this.arguments.length);

        if (arguments.length != this.arguments.length)
            throw new InvalidArgumentCountException(argsString.length(), this.arguments.length);

        if (arguments.length > 0 && arguments[arguments.length - 1].isBlank())
            throw new InvalidArgumentCountException(argsString.length(), this.arguments.length);

        return arguments;
    }
}
