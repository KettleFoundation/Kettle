package net.minecraft.command;

public class InvalidBlockStateException extends CommandException
{
    public InvalidBlockStateException()
    {
        this("commands.generic.blockstate.invalid");
    }

    public InvalidBlockStateException(String message, Object... objects)
    {
        super(message, objects);
    }

    public synchronized Throwable fillInStackTrace()
    {
        return this;
    }
}
