package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketPlaceRecipe implements Packet<INetHandlerPlayServer> {
   private int field_194320_a;
   private IRecipe field_194321_b;
   private boolean field_194322_c;

   public CPacketPlaceRecipe() {
   }

   public CPacketPlaceRecipe(int p_i47614_1_, IRecipe p_i47614_2_, boolean p_i47614_3_) {
      this.field_194320_a = p_i47614_1_;
      this.field_194321_b = p_i47614_2_;
      this.field_194322_c = p_i47614_3_;
   }

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_194320_a = p_148837_1_.readByte();
      this.field_194321_b = CraftingManager.func_193374_a(p_148837_1_.func_150792_a());
      this.field_194322_c = p_148837_1_.readBoolean();
   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.field_194320_a);
      p_148840_1_.func_150787_b(CraftingManager.func_193375_a(this.field_194321_b));
      p_148840_1_.writeBoolean(this.field_194322_c);
   }

   public void func_148833_a(INetHandlerPlayServer p_148833_1_) {
      p_148833_1_.func_194308_a(this);
   }

   public int func_194318_a() {
      return this.field_194320_a;
   }

   public IRecipe func_194317_b() {
      return this.field_194321_b;
   }

   public boolean func_194319_c() {
      return this.field_194322_c;
   }
}
