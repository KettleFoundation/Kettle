package net.minecraft.world.gen.structure;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;

public class StructureBoundingBox {
   public int field_78897_a;
   public int field_78895_b;
   public int field_78896_c;
   public int field_78893_d;
   public int field_78894_e;
   public int field_78892_f;

   public StructureBoundingBox() {
   }

   public StructureBoundingBox(int[] p_i43000_1_) {
      if (p_i43000_1_.length == 6) {
         this.field_78897_a = p_i43000_1_[0];
         this.field_78895_b = p_i43000_1_[1];
         this.field_78896_c = p_i43000_1_[2];
         this.field_78893_d = p_i43000_1_[3];
         this.field_78894_e = p_i43000_1_[4];
         this.field_78892_f = p_i43000_1_[5];
      }

   }

   public static StructureBoundingBox func_78887_a() {
      return new StructureBoundingBox(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
   }

   public static StructureBoundingBox func_175897_a(int p_175897_0_, int p_175897_1_, int p_175897_2_, int p_175897_3_, int p_175897_4_, int p_175897_5_, int p_175897_6_, int p_175897_7_, int p_175897_8_, EnumFacing p_175897_9_) {
      switch(p_175897_9_) {
      case NORTH:
         return new StructureBoundingBox(p_175897_0_ + p_175897_3_, p_175897_1_ + p_175897_4_, p_175897_2_ - p_175897_8_ + 1 + p_175897_5_, p_175897_0_ + p_175897_6_ - 1 + p_175897_3_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_5_);
      case SOUTH:
         return new StructureBoundingBox(p_175897_0_ + p_175897_3_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_5_, p_175897_0_ + p_175897_6_ - 1 + p_175897_3_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_8_ - 1 + p_175897_5_);
      case WEST:
         return new StructureBoundingBox(p_175897_0_ - p_175897_8_ + 1 + p_175897_5_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_3_, p_175897_0_ + p_175897_5_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_6_ - 1 + p_175897_3_);
      case EAST:
         return new StructureBoundingBox(p_175897_0_ + p_175897_5_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_3_, p_175897_0_ + p_175897_8_ - 1 + p_175897_5_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_6_ - 1 + p_175897_3_);
      default:
         return new StructureBoundingBox(p_175897_0_ + p_175897_3_, p_175897_1_ + p_175897_4_, p_175897_2_ + p_175897_5_, p_175897_0_ + p_175897_6_ - 1 + p_175897_3_, p_175897_1_ + p_175897_7_ - 1 + p_175897_4_, p_175897_2_ + p_175897_8_ - 1 + p_175897_5_);
      }
   }

   public static StructureBoundingBox func_175899_a(int p_175899_0_, int p_175899_1_, int p_175899_2_, int p_175899_3_, int p_175899_4_, int p_175899_5_) {
      return new StructureBoundingBox(Math.min(p_175899_0_, p_175899_3_), Math.min(p_175899_1_, p_175899_4_), Math.min(p_175899_2_, p_175899_5_), Math.max(p_175899_0_, p_175899_3_), Math.max(p_175899_1_, p_175899_4_), Math.max(p_175899_2_, p_175899_5_));
   }

   public StructureBoundingBox(StructureBoundingBox p_i2031_1_) {
      this.field_78897_a = p_i2031_1_.field_78897_a;
      this.field_78895_b = p_i2031_1_.field_78895_b;
      this.field_78896_c = p_i2031_1_.field_78896_c;
      this.field_78893_d = p_i2031_1_.field_78893_d;
      this.field_78894_e = p_i2031_1_.field_78894_e;
      this.field_78892_f = p_i2031_1_.field_78892_f;
   }

   public StructureBoundingBox(int p_i2032_1_, int p_i2032_2_, int p_i2032_3_, int p_i2032_4_, int p_i2032_5_, int p_i2032_6_) {
      this.field_78897_a = p_i2032_1_;
      this.field_78895_b = p_i2032_2_;
      this.field_78896_c = p_i2032_3_;
      this.field_78893_d = p_i2032_4_;
      this.field_78894_e = p_i2032_5_;
      this.field_78892_f = p_i2032_6_;
   }

   public StructureBoundingBox(Vec3i p_i45626_1_, Vec3i p_i45626_2_) {
      this.field_78897_a = Math.min(p_i45626_1_.func_177958_n(), p_i45626_2_.func_177958_n());
      this.field_78895_b = Math.min(p_i45626_1_.func_177956_o(), p_i45626_2_.func_177956_o());
      this.field_78896_c = Math.min(p_i45626_1_.func_177952_p(), p_i45626_2_.func_177952_p());
      this.field_78893_d = Math.max(p_i45626_1_.func_177958_n(), p_i45626_2_.func_177958_n());
      this.field_78894_e = Math.max(p_i45626_1_.func_177956_o(), p_i45626_2_.func_177956_o());
      this.field_78892_f = Math.max(p_i45626_1_.func_177952_p(), p_i45626_2_.func_177952_p());
   }

   public StructureBoundingBox(int p_i2033_1_, int p_i2033_2_, int p_i2033_3_, int p_i2033_4_) {
      this.field_78897_a = p_i2033_1_;
      this.field_78896_c = p_i2033_2_;
      this.field_78893_d = p_i2033_3_;
      this.field_78892_f = p_i2033_4_;
      this.field_78895_b = 1;
      this.field_78894_e = 512;
   }

   public boolean func_78884_a(StructureBoundingBox p_78884_1_) {
      return this.field_78893_d >= p_78884_1_.field_78897_a && this.field_78897_a <= p_78884_1_.field_78893_d && this.field_78892_f >= p_78884_1_.field_78896_c && this.field_78896_c <= p_78884_1_.field_78892_f && this.field_78894_e >= p_78884_1_.field_78895_b && this.field_78895_b <= p_78884_1_.field_78894_e;
   }

   public boolean func_78885_a(int p_78885_1_, int p_78885_2_, int p_78885_3_, int p_78885_4_) {
      return this.field_78893_d >= p_78885_1_ && this.field_78897_a <= p_78885_3_ && this.field_78892_f >= p_78885_2_ && this.field_78896_c <= p_78885_4_;
   }

   public void func_78888_b(StructureBoundingBox p_78888_1_) {
      this.field_78897_a = Math.min(this.field_78897_a, p_78888_1_.field_78897_a);
      this.field_78895_b = Math.min(this.field_78895_b, p_78888_1_.field_78895_b);
      this.field_78896_c = Math.min(this.field_78896_c, p_78888_1_.field_78896_c);
      this.field_78893_d = Math.max(this.field_78893_d, p_78888_1_.field_78893_d);
      this.field_78894_e = Math.max(this.field_78894_e, p_78888_1_.field_78894_e);
      this.field_78892_f = Math.max(this.field_78892_f, p_78888_1_.field_78892_f);
   }

   public void func_78886_a(int p_78886_1_, int p_78886_2_, int p_78886_3_) {
      this.field_78897_a += p_78886_1_;
      this.field_78895_b += p_78886_2_;
      this.field_78896_c += p_78886_3_;
      this.field_78893_d += p_78886_1_;
      this.field_78894_e += p_78886_2_;
      this.field_78892_f += p_78886_3_;
   }

   public boolean func_175898_b(Vec3i p_175898_1_) {
      return p_175898_1_.func_177958_n() >= this.field_78897_a && p_175898_1_.func_177958_n() <= this.field_78893_d && p_175898_1_.func_177952_p() >= this.field_78896_c && p_175898_1_.func_177952_p() <= this.field_78892_f && p_175898_1_.func_177956_o() >= this.field_78895_b && p_175898_1_.func_177956_o() <= this.field_78894_e;
   }

   public Vec3i func_175896_b() {
      return new Vec3i(this.field_78893_d - this.field_78897_a, this.field_78894_e - this.field_78895_b, this.field_78892_f - this.field_78896_c);
   }

   public int func_78883_b() {
      return this.field_78893_d - this.field_78897_a + 1;
   }

   public int func_78882_c() {
      return this.field_78894_e - this.field_78895_b + 1;
   }

   public int func_78880_d() {
      return this.field_78892_f - this.field_78896_c + 1;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x0", this.field_78897_a).add("y0", this.field_78895_b).add("z0", this.field_78896_c).add("x1", this.field_78893_d).add("y1", this.field_78894_e).add("z1", this.field_78892_f).toString();
   }

   public NBTTagIntArray func_151535_h() {
      return new NBTTagIntArray(new int[]{this.field_78897_a, this.field_78895_b, this.field_78896_c, this.field_78893_d, this.field_78894_e, this.field_78892_f});
   }
}
