package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient> {
   private int field_194314_a;
   private IRecipe field_194315_b;

   public SPacketPlaceGhostRecipe() {
   }

   public SPacketPlaceGhostRecipe(int p_i47615_1_, IRecipe p_i47615_2_) {
      this.field_194314_a = p_i47615_1_;
      this.field_194315_b = p_i47615_2_;
   }

   public IRecipe func_194311_a() {
      return this.field_194315_b;
   }

   public int func_194313_b() {
      return this.field_194314_a;
   }

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_194314_a = p_148837_1_.readByte();
      this.field_194315_b = CraftingManager.func_193374_a(p_148837_1_.func_150792_a());
   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.field_194314_a);
      p_148840_1_.func_150787_b(CraftingManager.func_193375_a(this.field_194315_b));
   }

   public void func_148833_a(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.func_194307_a(this);
   }
}
