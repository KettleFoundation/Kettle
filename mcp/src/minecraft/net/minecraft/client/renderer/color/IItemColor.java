package net.minecraft.client.renderer.color;

import net.minecraft.item.ItemStack;

public interface IItemColor
{
    int colorMultiplier(ItemStack stack, int tintIndex);
}
