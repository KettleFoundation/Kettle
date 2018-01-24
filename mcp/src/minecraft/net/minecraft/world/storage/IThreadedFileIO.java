package net.minecraft.world.storage;

public interface IThreadedFileIO
{
    /**
     * Writes one queued IO action.
     *  
     * @return true if there are more IO actions to perform afterwards, or false if there are none (and this instance of
     * IThreadedFileIO should be removed from the queued list)
     */
    boolean writeNextIO();
}
