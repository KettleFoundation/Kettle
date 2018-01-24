package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemShulkerBox extends ItemBlock
{
    public ItemShulkerBox(Block blockInstance)
    {
        super(blockInstance);
        this.setMaxStackSize(1);
    }
}
