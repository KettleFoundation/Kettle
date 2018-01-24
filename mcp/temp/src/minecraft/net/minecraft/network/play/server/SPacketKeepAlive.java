package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class SPacketKeepAlive implements Packet<INetHandlerPlayClient> {
   private long field_149136_a;

   public SPacketKeepAlive() {
   }

   public SPacketKeepAlive(long p_i46942_1_) {
      this.field_149136_a = p_i46942_1_;
   }

   public void func_148833_a(INetHandlerPlayClient p_148833_1_) {
      p_148833_1_.func_147272_a(this);
   }

   public void func_148837_a(PacketBuffer p_148837_1_) throws IOException {
      this.field_149136_a = p_148837_1_.readLong();
   }

   public void func_148840_b(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeLong(this.field_149136_a);
   }

   public long func_149134_c() {
      return this.field_149136_a;
   }
}
