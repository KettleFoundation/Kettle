package net.minecraft.client.renderer;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;

public class ItemRenderer {
   private static final ResourceLocation field_110931_c = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation field_110929_d = new ResourceLocation("textures/misc/underwater.png");
   private final Minecraft field_78455_a;
   private ItemStack field_187467_d = ItemStack.field_190927_a;
   private ItemStack field_187468_e = ItemStack.field_190927_a;
   private float field_187469_f;
   private float field_187470_g;
   private float field_187471_h;
   private float field_187472_i;
   private final RenderManager field_178111_g;
   private final RenderItem field_178112_h;

   public ItemRenderer(Minecraft p_i1247_1_) {
      this.field_78455_a = p_i1247_1_;
      this.field_178111_g = p_i1247_1_.func_175598_ae();
      this.field_178112_h = p_i1247_1_.func_175599_af();
   }

   public void func_178099_a(EntityLivingBase p_178099_1_, ItemStack p_178099_2_, ItemCameraTransforms.TransformType p_178099_3_) {
      this.func_187462_a(p_178099_1_, p_178099_2_, p_178099_3_, false);
   }

   public void func_187462_a(EntityLivingBase p_187462_1_, ItemStack p_187462_2_, ItemCameraTransforms.TransformType p_187462_3_, boolean p_187462_4_) {
      if (!p_187462_2_.func_190926_b()) {
         Item item = p_187462_2_.func_77973_b();
         Block block = Block.func_149634_a(item);
         GlStateManager.func_179094_E();
         boolean flag = this.field_178112_h.func_175050_a(p_187462_2_) && block.func_180664_k() == BlockRenderLayer.TRANSLUCENT;
         if (flag) {
            GlStateManager.func_179132_a(false);
         }

         this.field_178112_h.func_184392_a(p_187462_2_, p_187462_1_, p_187462_3_, p_187462_4_);
         if (flag) {
            GlStateManager.func_179132_a(true);
         }

         GlStateManager.func_179121_F();
      }
   }

   private void func_178101_a(float p_178101_1_, float p_178101_2_) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179114_b(p_178101_1_, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(p_178101_2_, 0.0F, 1.0F, 0.0F);
      RenderHelper.func_74519_b();
      GlStateManager.func_179121_F();
   }

   private void func_187464_b() {
      AbstractClientPlayer abstractclientplayer = this.field_78455_a.field_71439_g;
      int i = this.field_78455_a.field_71441_e.func_175626_b(new BlockPos(abstractclientplayer.field_70165_t, abstractclientplayer.field_70163_u + (double)abstractclientplayer.func_70047_e(), abstractclientplayer.field_70161_v), 0);
      float f = (float)(i & '\uffff');
      float f1 = (float)(i >> 16);
      OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, f, f1);
   }

   private void func_187458_c(float p_187458_1_) {
      EntityPlayerSP entityplayersp = this.field_78455_a.field_71439_g;
      float f = entityplayersp.field_71164_i + (entityplayersp.field_71155_g - entityplayersp.field_71164_i) * p_187458_1_;
      float f1 = entityplayersp.field_71163_h + (entityplayersp.field_71154_f - entityplayersp.field_71163_h) * p_187458_1_;
      GlStateManager.func_179114_b((entityplayersp.field_70125_A - f) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b((entityplayersp.field_70177_z - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float func_178100_c(float p_178100_1_) {
      float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
      f = MathHelper.func_76131_a(f, 0.0F, 1.0F);
      f = -MathHelper.func_76134_b(f * 3.1415927F) * 0.5F + 0.5F;
      return f;
   }

   private void func_187466_c() {
      if (!this.field_78455_a.field_71439_g.func_82150_aj()) {
         GlStateManager.func_179129_p();
         GlStateManager.func_179094_E();
         GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
         this.func_187455_a(EnumHandSide.RIGHT);
         this.func_187455_a(EnumHandSide.LEFT);
         GlStateManager.func_179121_F();
         GlStateManager.func_179089_o();
      }
   }

   private void func_187455_a(EnumHandSide p_187455_1_) {
      this.field_78455_a.func_110434_K().func_110577_a(this.field_78455_a.field_71439_g.func_110306_p());
      Render<AbstractClientPlayer> render = this.field_178111_g.<AbstractClientPlayer>func_78713_a(this.field_78455_a.field_71439_g);
      RenderPlayer renderplayer = (RenderPlayer)render;
      GlStateManager.func_179094_E();
      float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.func_179114_b(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f * -41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179109_b(f * 0.3F, -1.1F, 0.45F);
      if (p_187455_1_ == EnumHandSide.RIGHT) {
         renderplayer.func_177138_b(this.field_78455_a.field_71439_g);
      } else {
         renderplayer.func_177139_c(this.field_78455_a.field_71439_g);
      }

      GlStateManager.func_179121_F();
   }

   private void func_187465_a(float p_187465_1_, EnumHandSide p_187465_2_, float p_187465_3_, ItemStack p_187465_4_) {
      float f = p_187465_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
      GlStateManager.func_179109_b(f * 0.125F, -0.125F, 0.0F);
      if (!this.field_78455_a.field_71439_g.func_82150_aj()) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179114_b(f * 10.0F, 0.0F, 0.0F, 1.0F);
         this.func_187456_a(p_187465_1_, p_187465_3_, p_187465_2_);
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
      float f1 = MathHelper.func_76129_c(p_187465_3_);
      float f2 = MathHelper.func_76126_a(f1 * 3.1415927F);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * MathHelper.func_76126_a(f1 * 6.2831855F);
      float f5 = -0.3F * MathHelper.func_76126_a(p_187465_3_ * 3.1415927F);
      GlStateManager.func_179109_b(f * f3, f4 - 0.3F * f2, f5);
      GlStateManager.func_179114_b(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
      this.func_187461_a(p_187465_4_);
      GlStateManager.func_179121_F();
   }

   private void func_187463_a(float p_187463_1_, float p_187463_2_, float p_187463_3_) {
      float f = MathHelper.func_76129_c(p_187463_3_);
      float f1 = -0.2F * MathHelper.func_76126_a(p_187463_3_ * 3.1415927F);
      float f2 = -0.4F * MathHelper.func_76126_a(f * 3.1415927F);
      GlStateManager.func_179109_b(0.0F, -f1 / 2.0F, f2);
      float f3 = this.func_178100_c(p_187463_1_);
      GlStateManager.func_179109_b(0.0F, 0.04F + p_187463_2_ * -1.2F + f3 * -0.5F, -0.72F);
      GlStateManager.func_179114_b(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
      this.func_187466_c();
      float f4 = MathHelper.func_76126_a(f * 3.1415927F);
      GlStateManager.func_179114_b(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(2.0F, 2.0F, 2.0F);
      this.func_187461_a(this.field_187467_d);
   }

   private void func_187461_a(ItemStack p_187461_1_) {
      GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179152_a(0.38F, 0.38F, 0.38F);
      GlStateManager.func_179140_f();
      this.field_78455_a.func_110434_K().func_110577_a(field_110931_c);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179109_b(-0.5F, -0.5F, 0.0F);
      GlStateManager.func_179152_a(0.0078125F, 0.0078125F, 0.0078125F);
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      bufferbuilder.func_181662_b(-7.0D, 135.0D, 0.0D).func_187315_a(0.0D, 1.0D).func_181675_d();
      bufferbuilder.func_181662_b(135.0D, 135.0D, 0.0D).func_187315_a(1.0D, 1.0D).func_181675_d();
      bufferbuilder.func_181662_b(135.0D, -7.0D, 0.0D).func_187315_a(1.0D, 0.0D).func_181675_d();
      bufferbuilder.func_181662_b(-7.0D, -7.0D, 0.0D).func_187315_a(0.0D, 0.0D).func_181675_d();
      tessellator.func_78381_a();
      MapData mapdata = Items.field_151098_aY.func_77873_a(p_187461_1_, this.field_78455_a.field_71441_e);
      if (mapdata != null) {
         this.field_78455_a.field_71460_t.func_147701_i().func_148250_a(mapdata, false);
      }

      GlStateManager.func_179145_e();
   }

   private void func_187456_a(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
      boolean flag = p_187456_3_ != EnumHandSide.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = MathHelper.func_76129_c(p_187456_2_);
      float f2 = -0.3F * MathHelper.func_76126_a(f1 * 3.1415927F);
      float f3 = 0.4F * MathHelper.func_76126_a(f1 * 6.2831855F);
      float f4 = -0.4F * MathHelper.func_76126_a(p_187456_2_ * 3.1415927F);
      GlStateManager.func_179109_b(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
      GlStateManager.func_179114_b(f * 45.0F, 0.0F, 1.0F, 0.0F);
      float f5 = MathHelper.func_76126_a(p_187456_2_ * p_187456_2_ * 3.1415927F);
      float f6 = MathHelper.func_76126_a(f1 * 3.1415927F);
      GlStateManager.func_179114_b(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
      AbstractClientPlayer abstractclientplayer = this.field_78455_a.field_71439_g;
      this.field_78455_a.func_110434_K().func_110577_a(abstractclientplayer.func_110306_p());
      GlStateManager.func_179109_b(f * -1.0F, 3.6F, 3.5F);
      GlStateManager.func_179114_b(f * 120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b(f * -135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179109_b(f * 5.6F, 0.0F, 0.0F);
      RenderPlayer renderplayer = (RenderPlayer)this.field_178111_g.func_78713_a(abstractclientplayer);
      GlStateManager.func_179129_p();
      if (flag) {
         renderplayer.func_177138_b(abstractclientplayer);
      } else {
         renderplayer.func_177139_c(abstractclientplayer);
      }

      GlStateManager.func_179089_o();
   }

   private void func_187454_a(float p_187454_1_, EnumHandSide p_187454_2_, ItemStack p_187454_3_) {
      float f = (float)this.field_78455_a.field_71439_g.func_184605_cv() - p_187454_1_ + 1.0F;
      float f1 = f / (float)p_187454_3_.func_77988_m();
      if (f1 < 0.8F) {
         float f2 = MathHelper.func_76135_e(MathHelper.func_76134_b(f / 4.0F * 3.1415927F) * 0.1F);
         GlStateManager.func_179109_b(0.0F, f2, 0.0F);
      }

      float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
      int i = p_187454_2_ == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.func_179109_b(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
      GlStateManager.func_179114_b((float)i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void func_187453_a(EnumHandSide p_187453_1_, float p_187453_2_) {
      int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
      float f = MathHelper.func_76126_a(p_187453_2_ * p_187453_2_ * 3.1415927F);
      GlStateManager.func_179114_b((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
      float f1 = MathHelper.func_76126_a(MathHelper.func_76129_c(p_187453_2_) * 3.1415927F);
      GlStateManager.func_179114_b((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179114_b(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179114_b((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
   }

   private void func_187459_b(EnumHandSide p_187459_1_, float p_187459_2_) {
      int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
      GlStateManager.func_179109_b((float)i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
   }

   public void func_78440_a(float p_78440_1_) {
      AbstractClientPlayer abstractclientplayer = this.field_78455_a.field_71439_g;
      float f = abstractclientplayer.func_70678_g(p_78440_1_);
      EnumHand enumhand = (EnumHand)MoreObjects.firstNonNull(abstractclientplayer.field_184622_au, EnumHand.MAIN_HAND);
      float f1 = abstractclientplayer.field_70127_C + (abstractclientplayer.field_70125_A - abstractclientplayer.field_70127_C) * p_78440_1_;
      float f2 = abstractclientplayer.field_70126_B + (abstractclientplayer.field_70177_z - abstractclientplayer.field_70126_B) * p_78440_1_;
      boolean flag = true;
      boolean flag1 = true;
      if (abstractclientplayer.func_184587_cr()) {
         ItemStack itemstack = abstractclientplayer.func_184607_cu();
         if (itemstack.func_77973_b() == Items.field_151031_f) {
            EnumHand enumhand1 = abstractclientplayer.func_184600_cs();
            flag = enumhand1 == EnumHand.MAIN_HAND;
            flag1 = !flag;
         }
      }

      this.func_178101_a(f1, f2);
      this.func_187464_b();
      this.func_187458_c(p_78440_1_);
      GlStateManager.func_179091_B();
      if (flag) {
         float f3 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
         float f5 = 1.0F - (this.field_187470_g + (this.field_187469_f - this.field_187470_g) * p_78440_1_);
         this.func_187457_a(abstractclientplayer, p_78440_1_, f1, EnumHand.MAIN_HAND, f3, this.field_187467_d, f5);
      }

      if (flag1) {
         float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
         float f6 = 1.0F - (this.field_187472_i + (this.field_187471_h - this.field_187472_i) * p_78440_1_);
         this.func_187457_a(abstractclientplayer, p_78440_1_, f1, EnumHand.OFF_HAND, f4, this.field_187468_e, f6);
      }

      GlStateManager.func_179101_C();
      RenderHelper.func_74518_a();
   }

   public void func_187457_a(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, ItemStack p_187457_6_, float p_187457_7_) {
      boolean flag = p_187457_4_ == EnumHand.MAIN_HAND;
      EnumHandSide enumhandside = flag ? p_187457_1_.func_184591_cq() : p_187457_1_.func_184591_cq().func_188468_a();
      GlStateManager.func_179094_E();
      if (p_187457_6_.func_190926_b()) {
         if (flag && !p_187457_1_.func_82150_aj()) {
            this.func_187456_a(p_187457_7_, p_187457_5_, enumhandside);
         }
      } else if (p_187457_6_.func_77973_b() == Items.field_151098_aY) {
         if (flag && this.field_187468_e.func_190926_b()) {
            this.func_187463_a(p_187457_3_, p_187457_7_, p_187457_5_);
         } else {
            this.func_187465_a(p_187457_7_, enumhandside, p_187457_5_, p_187457_6_);
         }
      } else {
         boolean flag1 = enumhandside == EnumHandSide.RIGHT;
         if (p_187457_1_.func_184587_cr() && p_187457_1_.func_184605_cv() > 0 && p_187457_1_.func_184600_cs() == p_187457_4_) {
            int j = flag1 ? 1 : -1;
            switch(p_187457_6_.func_77975_n()) {
            case NONE:
               this.func_187459_b(enumhandside, p_187457_7_);
               break;
            case EAT:
            case DRINK:
               this.func_187454_a(p_187457_2_, enumhandside, p_187457_6_);
               this.func_187459_b(enumhandside, p_187457_7_);
               break;
            case BLOCK:
               this.func_187459_b(enumhandside, p_187457_7_);
               break;
            case BOW:
               this.func_187459_b(enumhandside, p_187457_7_);
               GlStateManager.func_179109_b((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
               GlStateManager.func_179114_b(-13.935F, 1.0F, 0.0F, 0.0F);
               GlStateManager.func_179114_b((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.func_179114_b((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
               float f5 = (float)p_187457_6_.func_77988_m() - ((float)this.field_78455_a.field_71439_g.func_184605_cv() - p_187457_2_ + 1.0F);
               float f6 = f5 / 20.0F;
               f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;
               if (f6 > 1.0F) {
                  f6 = 1.0F;
               }

               if (f6 > 0.1F) {
                  float f7 = MathHelper.func_76126_a((f5 - 0.1F) * 1.3F);
                  float f3 = f6 - 0.1F;
                  float f4 = f7 * f3;
                  GlStateManager.func_179109_b(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
               }

               GlStateManager.func_179109_b(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
               GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F + f6 * 0.2F);
               GlStateManager.func_179114_b((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
            }
         } else {
            float f = -0.4F * MathHelper.func_76126_a(MathHelper.func_76129_c(p_187457_5_) * 3.1415927F);
            float f1 = 0.2F * MathHelper.func_76126_a(MathHelper.func_76129_c(p_187457_5_) * 6.2831855F);
            float f2 = -0.2F * MathHelper.func_76126_a(p_187457_5_ * 3.1415927F);
            int i = flag1 ? 1 : -1;
            GlStateManager.func_179109_b((float)i * f, f1, f2);
            this.func_187459_b(enumhandside, p_187457_7_);
            this.func_187453_a(enumhandside, p_187457_5_);
         }

         this.func_187462_a(p_187457_1_, p_187457_6_, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
      }

      GlStateManager.func_179121_F();
   }

   public void func_78447_b(float p_78447_1_) {
      GlStateManager.func_179118_c();
      if (this.field_78455_a.field_71439_g.func_70094_T()) {
         IBlockState iblockstate = this.field_78455_a.field_71441_e.func_180495_p(new BlockPos(this.field_78455_a.field_71439_g));
         EntityPlayer entityplayer = this.field_78455_a.field_71439_g;

         for(int i = 0; i < 8; ++i) {
            double d0 = entityplayer.field_70165_t + (double)(((float)((i >> 0) % 2) - 0.5F) * entityplayer.field_70130_N * 0.8F);
            double d1 = entityplayer.field_70163_u + (double)(((float)((i >> 1) % 2) - 0.5F) * 0.1F);
            double d2 = entityplayer.field_70161_v + (double)(((float)((i >> 2) % 2) - 0.5F) * entityplayer.field_70130_N * 0.8F);
            BlockPos blockpos = new BlockPos(d0, d1 + (double)entityplayer.func_70047_e(), d2);
            IBlockState iblockstate1 = this.field_78455_a.field_71441_e.func_180495_p(blockpos);
            if (iblockstate1.func_191058_s()) {
               iblockstate = iblockstate1;
            }
         }

         if (iblockstate.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
            this.func_178108_a(this.field_78455_a.func_175602_ab().func_175023_a().func_178122_a(iblockstate));
         }
      }

      if (!this.field_78455_a.field_71439_g.func_175149_v()) {
         if (this.field_78455_a.field_71439_g.func_70055_a(Material.field_151586_h)) {
            this.func_78448_c(p_78447_1_);
         }

         if (this.field_78455_a.field_71439_g.func_70027_ad()) {
            this.func_78442_d();
         }
      }

      GlStateManager.func_179141_d();
   }

   private void func_178108_a(TextureAtlasSprite p_178108_1_) {
      this.field_78455_a.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      float f = 0.1F;
      GlStateManager.func_179131_c(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.func_179094_E();
      float f1 = -1.0F;
      float f2 = 1.0F;
      float f3 = -1.0F;
      float f4 = 1.0F;
      float f5 = -0.5F;
      float f6 = p_178108_1_.func_94209_e();
      float f7 = p_178108_1_.func_94212_f();
      float f8 = p_178108_1_.func_94206_g();
      float f9 = p_178108_1_.func_94210_h();
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      bufferbuilder.func_181662_b(-1.0D, -1.0D, -0.5D).func_187315_a((double)f7, (double)f9).func_181675_d();
      bufferbuilder.func_181662_b(1.0D, -1.0D, -0.5D).func_187315_a((double)f6, (double)f9).func_181675_d();
      bufferbuilder.func_181662_b(1.0D, 1.0D, -0.5D).func_187315_a((double)f6, (double)f8).func_181675_d();
      bufferbuilder.func_181662_b(-1.0D, 1.0D, -0.5D).func_187315_a((double)f7, (double)f8).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_78448_c(float p_78448_1_) {
      this.field_78455_a.func_110434_K().func_110577_a(field_110929_d);
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      float f = this.field_78455_a.field_71439_g.func_70013_c();
      GlStateManager.func_179131_c(f, f, f, 0.5F);
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179094_E();
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -this.field_78455_a.field_71439_g.field_70177_z / 64.0F;
      float f8 = this.field_78455_a.field_71439_g.field_70125_A / 64.0F;
      bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
      bufferbuilder.func_181662_b(-1.0D, -1.0D, -0.5D).func_187315_a((double)(4.0F + f7), (double)(4.0F + f8)).func_181675_d();
      bufferbuilder.func_181662_b(1.0D, -1.0D, -0.5D).func_187315_a((double)(0.0F + f7), (double)(4.0F + f8)).func_181675_d();
      bufferbuilder.func_181662_b(1.0D, 1.0D, -0.5D).func_187315_a((double)(0.0F + f7), (double)(0.0F + f8)).func_181675_d();
      bufferbuilder.func_181662_b(-1.0D, 1.0D, -0.5D).func_187315_a((double)(4.0F + f7), (double)(0.0F + f8)).func_181675_d();
      tessellator.func_78381_a();
      GlStateManager.func_179121_F();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
   }

   private void func_78442_d() {
      Tessellator tessellator = Tessellator.func_178181_a();
      BufferBuilder bufferbuilder = tessellator.func_178180_c();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.func_179143_c(519);
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      float f = 1.0F;

      for(int i = 0; i < 2; ++i) {
         GlStateManager.func_179094_E();
         TextureAtlasSprite textureatlassprite = this.field_78455_a.func_147117_R().func_110572_b("minecraft:blocks/fire_layer_1");
         this.field_78455_a.func_110434_K().func_110577_a(TextureMap.field_110575_b);
         float f1 = textureatlassprite.func_94209_e();
         float f2 = textureatlassprite.func_94212_f();
         float f3 = textureatlassprite.func_94206_g();
         float f4 = textureatlassprite.func_94210_h();
         float f5 = -0.5F;
         float f6 = 0.5F;
         float f7 = -0.5F;
         float f8 = 0.5F;
         float f9 = -0.5F;
         GlStateManager.func_179109_b((float)(-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.func_179114_b((float)(i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         bufferbuilder.func_181662_b(-0.5D, -0.5D, -0.5D).func_187315_a((double)f2, (double)f4).func_181675_d();
         bufferbuilder.func_181662_b(0.5D, -0.5D, -0.5D).func_187315_a((double)f1, (double)f4).func_181675_d();
         bufferbuilder.func_181662_b(0.5D, 0.5D, -0.5D).func_187315_a((double)f1, (double)f3).func_181675_d();
         bufferbuilder.func_181662_b(-0.5D, 0.5D, -0.5D).func_187315_a((double)f2, (double)f3).func_181675_d();
         tessellator.func_78381_a();
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179143_c(515);
   }

   public void func_78441_a() {
      this.field_187470_g = this.field_187469_f;
      this.field_187472_i = this.field_187471_h;
      EntityPlayerSP entityplayersp = this.field_78455_a.field_71439_g;
      ItemStack itemstack = entityplayersp.func_184614_ca();
      ItemStack itemstack1 = entityplayersp.func_184592_cb();
      if (entityplayersp.func_184838_M()) {
         this.field_187469_f = MathHelper.func_76131_a(this.field_187469_f - 0.4F, 0.0F, 1.0F);
         this.field_187471_h = MathHelper.func_76131_a(this.field_187471_h - 0.4F, 0.0F, 1.0F);
      } else {
         float f = entityplayersp.func_184825_o(1.0F);
         this.field_187469_f += MathHelper.func_76131_a((Objects.equals(this.field_187467_d, itemstack) ? f * f * f : 0.0F) - this.field_187469_f, -0.4F, 0.4F);
         this.field_187471_h += MathHelper.func_76131_a((float)(Objects.equals(this.field_187468_e, itemstack1) ? 1 : 0) - this.field_187471_h, -0.4F, 0.4F);
      }

      if (this.field_187469_f < 0.1F) {
         this.field_187467_d = itemstack;
      }

      if (this.field_187471_h < 0.1F) {
         this.field_187468_e = itemstack1;
      }

   }

   public void func_187460_a(EnumHand p_187460_1_) {
      if (p_187460_1_ == EnumHand.MAIN_HAND) {
         this.field_187469_f = 0.0F;
      } else {
         this.field_187471_h = 0.0F;
      }

   }
}
