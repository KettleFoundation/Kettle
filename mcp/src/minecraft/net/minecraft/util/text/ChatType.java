package net.minecraft.util.text;

public enum ChatType
{
    CHAT((byte)0),
    SYSTEM((byte)1),
    GAME_INFO((byte)2);

    private final byte id;

    private ChatType(byte id)
    {
        this.id = id;
    }

    public byte getId()
    {
        return this.id;
    }

    public static ChatType byId(byte idIn)
    {
        for (ChatType chattype : values())
        {
            if (idIn == chattype.id)
            {
                return chattype;
            }
        }

        return CHAT;
    }
}
