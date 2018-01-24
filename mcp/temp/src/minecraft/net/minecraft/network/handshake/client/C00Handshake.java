package net.minecraft.network.handshake.client;

import java.io.IOException;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

public class C00Handshake implements Packet<INetHandlerHandshakeServer> {
   private int field_149600_a;
   private String field_149598_b;
   private int field_149599_c;
   private EnumConnectionState field_149597_d;

   public C00Handshake() {
   }

   public C00Handshake(String p_i47613_1_, int p_i47613_2_, EnumConnectionState p_i47613_3_) {
      this.field_149600_a = 340;
      this.field_149598_b = p_i47613_1_;
      this.field_149599_c = p_i47613_2_;
      this.field_149597_d = p_i47613_3_;
   }

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_149600_a = p_148837_1_.func_150792_a();
      this.field_149598_b = p_148837_1_.func_150789_c(255);
      this.field_149599_c = p_148837_1_.readUnsignedShort();
      this.field_149597_d = EnumConnectionState.func_150760_a(p_148837_1_.func_150792_a());
   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.func_150787_b(this.field_149600_a);
      p_148840_1_.func_180714_a(this.field_149598_b);
      p_148840_1_.writeShort(this.field_149599_c);
      p_148840_1_.func_150787_b(this.field_149597_d.func_150759_c());
   }

   public void func_148833_a(INetHandlerHandshakeServer p_148833_1_) {
      p_148833_1_.func_147383_a(this);
   }

   public EnumConnectionState func_149594_c() {
      return this.field_149597_d;
   }

   public int func_149595_d() {
      return this.field_149600_a;
   }
}
