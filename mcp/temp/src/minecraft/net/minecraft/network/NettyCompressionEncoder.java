package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
   private final byte[] field_179302_a = new byte[8192];
   private final Deflater field_179300_b;
   private int field_179301_c;

   public NettyCompressionEncoder(int p_i46005_1_) {
      this.field_179301_c = p_i46005_1_;
      this.field_179300_b = new Deflater();
   }

   protected void encode(ChannelHandlerContext p_encode_1_, ByteBuf p_encode_2_, ByteBuf p_encode_3_) throws Exception {
      int i = p_encode_2_.readableBytes();
      PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
      if (i < this.field_179301_c) {
         packetbuffer.func_150787_b(0);
         packetbuffer.writeBytes(p_encode_2_);
      } else {
         byte[] abyte = new byte[i];
         p_encode_2_.readBytes(abyte);
         packetbuffer.func_150787_b(abyte.length);
         this.field_179300_b.setInput(abyte, 0, i);
         this.field_179300_b.finish();

         while(!this.field_179300_b.finished()) {
            int j = this.field_179300_b.deflate(this.field_179302_a);
            packetbuffer.writeBytes(this.field_179302_a, 0, j);
         }

         this.field_179300_b.reset();
      }

   }

   public void func_179299_a(int p_179299_1_) {
      this.field_179301_c = p_179299_1_;
   }
}
