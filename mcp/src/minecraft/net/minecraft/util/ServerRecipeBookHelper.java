package net.minecraft.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerRecipeBookHelper
{
    private final Logger field_194330_a = LogManager.getLogger();
    private final RecipeItemHelper field_194331_b = new RecipeItemHelper();
    private EntityPlayerMP field_194332_c;
    private IRecipe field_194333_d;
    private boolean field_194334_e;
    private InventoryCraftResult field_194335_f;
    private InventoryCrafting field_194336_g;
    private List<Slot> field_194337_h;

    public void func_194327_a(EntityPlayerMP p_194327_1_, @Nullable IRecipe p_194327_2_, boolean p_194327_3_)
    {
        if (p_194327_2_ != null && p_194327_1_.getRecipeBook().isUnlocked(p_194327_2_))
        {
            this.field_194332_c = p_194327_1_;
            this.field_194333_d = p_194327_2_;
            this.field_194334_e = p_194327_3_;
            this.field_194337_h = p_194327_1_.openContainer.inventorySlots;
            Container container = p_194327_1_.openContainer;
            this.field_194335_f = null;
            this.field_194336_g = null;

            if (container instanceof ContainerWorkbench)
            {
                this.field_194335_f = ((ContainerWorkbench)container).craftResult;
                this.field_194336_g = ((ContainerWorkbench)container).craftMatrix;
            }
            else if (container instanceof ContainerPlayer)
            {
                this.field_194335_f = ((ContainerPlayer)container).craftResult;
                this.field_194336_g = ((ContainerPlayer)container).craftMatrix;
            }

            if (this.field_194335_f != null && this.field_194336_g != null)
            {
                if (this.func_194328_c() || p_194327_1_.isCreative())
                {
                    this.field_194331_b.clear();
                    p_194327_1_.inventory.fillStackedContents(this.field_194331_b, false);
                    this.field_194336_g.fillStackedContents(this.field_194331_b);

                    if (this.field_194331_b.canCraft(p_194327_2_, (IntList)null))
                    {
                        this.func_194329_b();
                    }
                    else
                    {
                        this.func_194326_a();
                        p_194327_1_.connection.sendPacket(new SPacketPlaceGhostRecipe(p_194327_1_.openContainer.windowId, p_194327_2_));
                    }

                    p_194327_1_.inventory.markDirty();
                }
            }
        }
    }

    private void func_194326_a()
    {
        InventoryPlayer inventoryplayer = this.field_194332_c.inventory;

        for (int i = 0; i < this.field_194336_g.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.field_194336_g.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                while (itemstack.getCount() > 0)
                {
                    int j = inventoryplayer.storeItemStack(itemstack);

                    if (j == -1)
                    {
                        j = inventoryplayer.getFirstEmptyStack();
                    }

                    ItemStack itemstack1 = itemstack.copy();
                    itemstack1.setCount(1);
                    inventoryplayer.add(j, itemstack1);
                    this.field_194336_g.decrStackSize(i, 1);
                }
            }
        }

        this.field_194336_g.clear();
        this.field_194335_f.clear();
    }

    private void func_194329_b()
    {
        boolean flag = this.field_194333_d.matches(this.field_194336_g, this.field_194332_c.world);
        int i = this.field_194331_b.getBiggestCraftableStack(this.field_194333_d, (IntList)null);

        if (flag)
        {
            boolean flag1 = true;

            for (int j = 0; j < this.field_194336_g.getSizeInventory(); ++j)
            {
                ItemStack itemstack = this.field_194336_g.getStackInSlot(j);

                if (!itemstack.isEmpty() && Math.min(i, itemstack.getMaxStackSize()) > itemstack.getCount())
                {
                    flag1 = false;
                }
            }

            if (flag1)
            {
                return;
            }
        }

        int i1 = this.func_194324_a(i, flag);
        IntList intlist = new IntArrayList();

        if (this.field_194331_b.canCraft(this.field_194333_d, intlist, i1))
        {
            int j1 = i1;
            IntListIterator intlistiterator = intlist.iterator();

            while (intlistiterator.hasNext())
            {
                int k = ((Integer)intlistiterator.next()).intValue();
                int l = RecipeItemHelper.unpack(k).getMaxStackSize();

                if (l < j1)
                {
                    j1 = l;
                }
            }

            if (this.field_194331_b.canCraft(this.field_194333_d, intlist, j1))
            {
                this.func_194326_a();
                this.func_194323_a(j1, intlist);
            }
        }
    }

    private int func_194324_a(int p_194324_1_, boolean p_194324_2_)
    {
        int i = 1;

        if (this.field_194334_e)
        {
            i = p_194324_1_;
        }
        else if (p_194324_2_)
        {
            i = 64;

            for (int j = 0; j < this.field_194336_g.getSizeInventory(); ++j)
            {
                ItemStack itemstack = this.field_194336_g.getStackInSlot(j);

                if (!itemstack.isEmpty() && i > itemstack.getCount())
                {
                    i = itemstack.getCount();
                }
            }

            if (i < 64)
            {
                ++i;
            }
        }

        return i;
    }

    private void func_194323_a(int p_194323_1_, IntList p_194323_2_)
    {
        int i = this.field_194336_g.getWidth();
        int j = this.field_194336_g.getHeight();

        if (this.field_194333_d instanceof ShapedRecipes)
        {
            ShapedRecipes shapedrecipes = (ShapedRecipes)this.field_194333_d;
            i = shapedrecipes.getWidth();
            j = shapedrecipes.getHeight();
        }

        int j1 = 1;
        Iterator<Integer> iterator = p_194323_2_.iterator();

        for (int k = 0; k < this.field_194336_g.getWidth() && j != k; ++k)
        {
            for (int l = 0; l < this.field_194336_g.getHeight(); ++l)
            {
                if (i == l || !iterator.hasNext())
                {
                    j1 += this.field_194336_g.getWidth() - l;
                    break;
                }

                Slot slot = this.field_194337_h.get(j1);
                ItemStack itemstack = RecipeItemHelper.unpack(((Integer)iterator.next()).intValue());

                if (itemstack.isEmpty())
                {
                    ++j1;
                }
                else
                {
                    for (int i1 = 0; i1 < p_194323_1_; ++i1)
                    {
                        this.func_194325_a(slot, itemstack);
                    }

                    ++j1;
                }
            }

            if (!iterator.hasNext())
            {
                break;
            }
        }
    }

    private void func_194325_a(Slot p_194325_1_, ItemStack p_194325_2_)
    {
        InventoryPlayer inventoryplayer = this.field_194332_c.inventory;
        int i = inventoryplayer.findSlotMatchingUnusedItem(p_194325_2_);

        if (i != -1)
        {
            ItemStack itemstack = inventoryplayer.getStackInSlot(i).copy();

            if (!itemstack.isEmpty())
            {
                if (itemstack.getCount() > 1)
                {
                    inventoryplayer.decrStackSize(i, 1);
                }
                else
                {
                    inventoryplayer.removeStackFromSlot(i);
                }

                itemstack.setCount(1);

                if (p_194325_1_.getStack().isEmpty())
                {
                    p_194325_1_.putStack(itemstack);
                }
                else
                {
                    p_194325_1_.getStack().grow(1);
                }
            }
        }
    }

    private boolean func_194328_c()
    {
        InventoryPlayer inventoryplayer = this.field_194332_c.inventory;

        for (int i = 0; i < this.field_194336_g.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.field_194336_g.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                int j = inventoryplayer.storeItemStack(itemstack);

                if (j == -1)
                {
                    j = inventoryplayer.getFirstEmptyStack();
                }

                if (j == -1)
                {
                    return false;
                }
            }
        }

        return true;
    }
}
