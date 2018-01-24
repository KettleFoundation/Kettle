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

public class ServerRecipeBookHelper {
   private final Logger field_194330_a = LogManager.getLogger();
   private final RecipeItemHelper field_194331_b = new RecipeItemHelper();
   private EntityPlayerMP field_194332_c;
   private IRecipe field_194333_d;
   private boolean field_194334_e;
   private InventoryCraftResult field_194335_f;
   private InventoryCrafting field_194336_g;
   private List<Slot> field_194337_h;

   public void func_194327_a(EntityPlayerMP p_194327_1_, @Nullable IRecipe p_194327_2_, boolean p_194327_3_) {
      if (p_194327_2_ != null && p_194327_1_.func_192037_E().func_193830_f(p_194327_2_)) {
         this.field_194332_c = p_194327_1_;
         this.field_194333_d = p_194327_2_;
         this.field_194334_e = p_194327_3_;
         this.field_194337_h = p_194327_1_.field_71070_bA.field_75151_b;
         Container container = p_194327_1_.field_71070_bA;
         this.field_194335_f = null;
         this.field_194336_g = null;
         if (container instanceof ContainerWorkbench) {
            this.field_194335_f = ((ContainerWorkbench)container).field_75160_f;
            this.field_194336_g = ((ContainerWorkbench)container).field_75162_e;
         } else if (container instanceof ContainerPlayer) {
            this.field_194335_f = ((ContainerPlayer)container).field_75179_f;
            this.field_194336_g = ((ContainerPlayer)container).field_75181_e;
         }

         if (this.field_194335_f != null && this.field_194336_g != null) {
            if (this.func_194328_c() || p_194327_1_.func_184812_l_()) {
               this.field_194331_b.func_194119_a();
               p_194327_1_.field_71071_by.func_194016_a(this.field_194331_b, false);
               this.field_194336_g.func_194018_a(this.field_194331_b);
               if (this.field_194331_b.func_194116_a(p_194327_2_, (IntList)null)) {
                  this.func_194329_b();
               } else {
                  this.func_194326_a();
                  p_194327_1_.field_71135_a.func_147359_a(new SPacketPlaceGhostRecipe(p_194327_1_.field_71070_bA.field_75152_c, p_194327_2_));
               }

               p_194327_1_.field_71071_by.func_70296_d();
            }
         }
      }
   }

   private void func_194326_a() {
      InventoryPlayer inventoryplayer = this.field_194332_c.field_71071_by;

      for(int i = 0; i < this.field_194336_g.func_70302_i_(); ++i) {
         ItemStack itemstack = this.field_194336_g.func_70301_a(i);
         if (!itemstack.func_190926_b()) {
            while(itemstack.func_190916_E() > 0) {
               int j = inventoryplayer.func_70432_d(itemstack);
               if (j == -1) {
                  j = inventoryplayer.func_70447_i();
               }

               ItemStack itemstack1 = itemstack.func_77946_l();
               itemstack1.func_190920_e(1);
               inventoryplayer.func_191971_c(j, itemstack1);
               this.field_194336_g.func_70298_a(i, 1);
            }
         }
      }

      this.field_194336_g.func_174888_l();
      this.field_194335_f.func_174888_l();
   }

   private void func_194329_b() {
      boolean flag = this.field_194333_d.func_77569_a(this.field_194336_g, this.field_194332_c.field_70170_p);
      int i = this.field_194331_b.func_194114_b(this.field_194333_d, (IntList)null);
      if (flag) {
         boolean flag1 = true;

         for(int j = 0; j < this.field_194336_g.func_70302_i_(); ++j) {
            ItemStack itemstack = this.field_194336_g.func_70301_a(j);
            if (!itemstack.func_190926_b() && Math.min(i, itemstack.func_77976_d()) > itemstack.func_190916_E()) {
               flag1 = false;
            }
         }

         if (flag1) {
            return;
         }
      }

      int i1 = this.func_194324_a(i, flag);
      IntList intlist = new IntArrayList();
      if (this.field_194331_b.func_194118_a(this.field_194333_d, intlist, i1)) {
         int j1 = i1;
         IntListIterator intlistiterator = intlist.iterator();

         while(intlistiterator.hasNext()) {
            int k = ((Integer)intlistiterator.next()).intValue();
            int l = RecipeItemHelper.func_194115_b(k).func_77976_d();
            if (l < j1) {
               j1 = l;
            }
         }

         if (this.field_194331_b.func_194118_a(this.field_194333_d, intlist, j1)) {
            this.func_194326_a();
            this.func_194323_a(j1, intlist);
         }
      }
   }

   private int func_194324_a(int p_194324_1_, boolean p_194324_2_) {
      int i = 1;
      if (this.field_194334_e) {
         i = p_194324_1_;
      } else if (p_194324_2_) {
         i = 64;

         for(int j = 0; j < this.field_194336_g.func_70302_i_(); ++j) {
            ItemStack itemstack = this.field_194336_g.func_70301_a(j);
            if (!itemstack.func_190926_b() && i > itemstack.func_190916_E()) {
               i = itemstack.func_190916_E();
            }
         }

         if (i < 64) {
            ++i;
         }
      }

      return i;
   }

   private void func_194323_a(int p_194323_1_, IntList p_194323_2_) {
      int i = this.field_194336_g.func_174922_i();
      int j = this.field_194336_g.func_174923_h();
      if (this.field_194333_d instanceof ShapedRecipes) {
         ShapedRecipes shapedrecipes = (ShapedRecipes)this.field_194333_d;
         i = shapedrecipes.func_192403_f();
         j = shapedrecipes.func_192404_g();
      }

      int j1 = 1;
      Iterator<Integer> iterator = p_194323_2_.iterator();

      for(int k = 0; k < this.field_194336_g.func_174922_i() && j != k; ++k) {
         for(int l = 0; l < this.field_194336_g.func_174923_h(); ++l) {
            if (i == l || !iterator.hasNext()) {
               j1 += this.field_194336_g.func_174922_i() - l;
               break;
            }

            Slot slot = this.field_194337_h.get(j1);
            ItemStack itemstack = RecipeItemHelper.func_194115_b(((Integer)iterator.next()).intValue());
            if (itemstack.func_190926_b()) {
               ++j1;
            } else {
               for(int i1 = 0; i1 < p_194323_1_; ++i1) {
                  this.func_194325_a(slot, itemstack);
               }

               ++j1;
            }
         }

         if (!iterator.hasNext()) {
            break;
         }
      }

   }

   private void func_194325_a(Slot p_194325_1_, ItemStack p_194325_2_) {
      InventoryPlayer inventoryplayer = this.field_194332_c.field_71071_by;
      int i = inventoryplayer.func_194014_c(p_194325_2_);
      if (i != -1) {
         ItemStack itemstack = inventoryplayer.func_70301_a(i).func_77946_l();
         if (!itemstack.func_190926_b()) {
            if (itemstack.func_190916_E() > 1) {
               inventoryplayer.func_70298_a(i, 1);
            } else {
               inventoryplayer.func_70304_b(i);
            }

            itemstack.func_190920_e(1);
            if (p_194325_1_.func_75211_c().func_190926_b()) {
               p_194325_1_.func_75215_d(itemstack);
            } else {
               p_194325_1_.func_75211_c().func_190917_f(1);
            }

         }
      }
   }

   private boolean func_194328_c() {
      InventoryPlayer inventoryplayer = this.field_194332_c.field_71071_by;

      for(int i = 0; i < this.field_194336_g.func_70302_i_(); ++i) {
         ItemStack itemstack = this.field_194336_g.func_70301_a(i);
         if (!itemstack.func_190926_b()) {
            int j = inventoryplayer.func_70432_d(itemstack);
            if (j == -1) {
               j = inventoryplayer.func_70447_i();
            }

            if (j == -1) {
               return false;
            }
         }
      }

      return true;
   }
}
