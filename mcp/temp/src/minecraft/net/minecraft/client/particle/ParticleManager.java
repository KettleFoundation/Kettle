package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleManager {
   private static final ResourceLocation field_110737_b = new ResourceLocation("textures/particle/particles.png");
   protected World field_78878_a;
   private final ArrayDeque<Particle>[][] field_78876_b = new ArrayDeque[4][];
   private final Queue<ParticleEmitter> field_178933_d = Queues.<ParticleEmitter>newArrayDeque();
   private final TextureManager field_78877_c;
   private final Random field_78875_d = new Random();
   private final Map<Integer, IParticleFactory> field_178932_g = Maps.<Integer, IParticleFactory>newHashMap();
   private final Queue<Particle> field_187241_h = Queues.<Particle>newArrayDeque();

   public ParticleManager(World p_i1220_1_, TextureManager p_i1220_2_) {
      this.field_78878_a = p_i1220_1_;
      this.field_78877_c = p_i1220_2_;

      for(int i = 0; i < 4; ++i) {
         this.field_78876_b[i] = new ArrayDeque[2];

         for(int j = 0; j < 2; ++j) {
            this.field_78876_b[i][j] = Queues.newArrayDeque();
         }
      }

      this.func_178930_c();
   }

   private void func_178930_c() {
      this.func_178929_a(EnumParticleTypes.EXPLOSION_NORMAL.func_179348_c(), new ParticleExplosion.Factory());
      this.func_178929_a(EnumParticleTypes.SPIT.func_179348_c(), new ParticleSpit.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_BUBBLE.func_179348_c(), new ParticleBubble.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_SPLASH.func_179348_c(), new ParticleSplash.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_WAKE.func_179348_c(), new ParticleWaterWake.Factory());
      this.func_178929_a(EnumParticleTypes.WATER_DROP.func_179348_c(), new ParticleRain.Factory());
      this.func_178929_a(EnumParticleTypes.SUSPENDED.func_179348_c(), new ParticleSuspend.Factory());
      this.func_178929_a(EnumParticleTypes.SUSPENDED_DEPTH.func_179348_c(), new ParticleSuspendedTown.Factory());
      this.func_178929_a(EnumParticleTypes.CRIT.func_179348_c(), new ParticleCrit.Factory());
      this.func_178929_a(EnumParticleTypes.CRIT_MAGIC.func_179348_c(), new ParticleCrit.MagicFactory());
      this.func_178929_a(EnumParticleTypes.SMOKE_NORMAL.func_179348_c(), new ParticleSmokeNormal.Factory());
      this.func_178929_a(EnumParticleTypes.SMOKE_LARGE.func_179348_c(), new ParticleSmokeLarge.Factory());
      this.func_178929_a(EnumParticleTypes.SPELL.func_179348_c(), new ParticleSpell.Factory());
      this.func_178929_a(EnumParticleTypes.SPELL_INSTANT.func_179348_c(), new ParticleSpell.InstantFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_MOB.func_179348_c(), new ParticleSpell.MobFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_MOB_AMBIENT.func_179348_c(), new ParticleSpell.AmbientMobFactory());
      this.func_178929_a(EnumParticleTypes.SPELL_WITCH.func_179348_c(), new ParticleSpell.WitchFactory());
      this.func_178929_a(EnumParticleTypes.DRIP_WATER.func_179348_c(), new ParticleDrip.WaterFactory());
      this.func_178929_a(EnumParticleTypes.DRIP_LAVA.func_179348_c(), new ParticleDrip.LavaFactory());
      this.func_178929_a(EnumParticleTypes.VILLAGER_ANGRY.func_179348_c(), new ParticleHeart.AngryVillagerFactory());
      this.func_178929_a(EnumParticleTypes.VILLAGER_HAPPY.func_179348_c(), new ParticleSuspendedTown.HappyVillagerFactory());
      this.func_178929_a(EnumParticleTypes.TOWN_AURA.func_179348_c(), new ParticleSuspendedTown.Factory());
      this.func_178929_a(EnumParticleTypes.NOTE.func_179348_c(), new ParticleNote.Factory());
      this.func_178929_a(EnumParticleTypes.PORTAL.func_179348_c(), new ParticlePortal.Factory());
      this.func_178929_a(EnumParticleTypes.ENCHANTMENT_TABLE.func_179348_c(), new ParticleEnchantmentTable.EnchantmentTable());
      this.func_178929_a(EnumParticleTypes.FLAME.func_179348_c(), new ParticleFlame.Factory());
      this.func_178929_a(EnumParticleTypes.LAVA.func_179348_c(), new ParticleLava.Factory());
      this.func_178929_a(EnumParticleTypes.FOOTSTEP.func_179348_c(), new ParticleFootStep.Factory());
      this.func_178929_a(EnumParticleTypes.CLOUD.func_179348_c(), new ParticleCloud.Factory());
      this.func_178929_a(EnumParticleTypes.REDSTONE.func_179348_c(), new ParticleRedstone.Factory());
      this.func_178929_a(EnumParticleTypes.FALLING_DUST.func_179348_c(), new ParticleFallingDust.Factory());
      this.func_178929_a(EnumParticleTypes.SNOWBALL.func_179348_c(), new ParticleBreaking.SnowballFactory());
      this.func_178929_a(EnumParticleTypes.SNOW_SHOVEL.func_179348_c(), new ParticleSnowShovel.Factory());
      this.func_178929_a(EnumParticleTypes.SLIME.func_179348_c(), new ParticleBreaking.SlimeFactory());
      this.func_178929_a(EnumParticleTypes.HEART.func_179348_c(), new ParticleHeart.Factory());
      this.func_178929_a(EnumParticleTypes.BARRIER.func_179348_c(), new Barrier.Factory());
      this.func_178929_a(EnumParticleTypes.ITEM_CRACK.func_179348_c(), new ParticleBreaking.Factory());
      this.func_178929_a(EnumParticleTypes.BLOCK_CRACK.func_179348_c(), new ParticleDigging.Factory());
      this.func_178929_a(EnumParticleTypes.BLOCK_DUST.func_179348_c(), new ParticleBlockDust.Factory());
      this.func_178929_a(EnumParticleTypes.EXPLOSION_HUGE.func_179348_c(), new ParticleExplosionHuge.Factory());
      this.func_178929_a(EnumParticleTypes.EXPLOSION_LARGE.func_179348_c(), new ParticleExplosionLarge.Factory());
      this.func_178929_a(EnumParticleTypes.FIREWORKS_SPARK.func_179348_c(), new ParticleFirework.Factory());
      this.func_178929_a(EnumParticleTypes.MOB_APPEARANCE.func_179348_c(), new ParticleMobAppearance.Factory());
      this.func_178929_a(EnumParticleTypes.DRAGON_BREATH.func_179348_c(), new ParticleDragonBreath.Factory());
      this.func_178929_a(EnumParticleTypes.END_ROD.func_179348_c(), new ParticleEndRod.Factory());
      this.func_178929_a(EnumParticleTypes.DAMAGE_INDICATOR.func_179348_c(), new ParticleCrit.DamageIndicatorFactory());
      this.func_178929_a(EnumParticleTypes.SWEEP_ATTACK.func_179348_c(), new ParticleSweepAttack.Factory());
      this.func_178929_a(EnumParticleTypes.TOTEM.func_179348_c(), new ParticleTotem.Factory());
   }

   public void func_178929_a(int p_178929_1_, IParticleFactory p_178929_2_) {
      this.field_178932_g.put(Integer.valueOf(p_178929_1_), p_178929_2_);
   }

   public void func_178926_a(Entity p_178926_1_, EnumParticleTypes p_178926_2_) {
      this.field_178933_d.add(new ParticleEmitter(this.field_78878_a, p_178926_1_, p_178926_2_));
   }

   public void func_191271_a(Entity p_191271_1_, EnumParticleTypes p_191271_2_, int p_191271_3_) {
      this.field_178933_d.add(new ParticleEmitter(this.field_78878_a, p_191271_1_, p_191271_2_, p_191271_3_));
   }

   @Nullable
   public Particle func_178927_a(int p_178927_1_, double p_178927_2_, double p_178927_4_, double p_178927_6_, double p_178927_8_, double p_178927_10_, double p_178927_12_, int... p_178927_14_) {
      IParticleFactory iparticlefactory = this.field_178932_g.get(Integer.valueOf(p_178927_1_));
      if (iparticlefactory != null) {
         Particle particle = iparticlefactory.func_178902_a(p_178927_1_, this.field_78878_a, p_178927_2_, p_178927_4_, p_178927_6_, p_178927_8_, p_178927_10_, p_178927_12_, p_178927_14_);
         if (particle != null) {
            this.func_78873_a(particle);
            return particle;
         }
      }

      return null;
   }

   public void func_78873_a(Particle p_78873_1_) {
      this.field_187241_h.add(p_78873_1_);
   }

   public void func_78868_a() {
      for(int i = 0; i < 4; ++i) {
         this.func_178922_a(i);
      }

      if (!this.field_178933_d.isEmpty()) {
         List<ParticleEmitter> list = Lists.<ParticleEmitter>newArrayList();

         for(ParticleEmitter particleemitter : this.field_178933_d) {
            particleemitter.func_189213_a();
            if (!particleemitter.func_187113_k()) {
               list.add(particleemitter);
            }
         }

         this.field_178933_d.removeAll(list);
      }

      if (!this.field_187241_h.isEmpty()) {
         for(Particle particle = this.field_187241_h.poll(); particle != null; particle = this.field_187241_h.poll()) {
            int j = particle.func_70537_b();
            int k = particle.func_187111_c() ? 0 : 1;
            if (this.field_78876_b[j][k].size() >= 16384) {
               this.field_78876_b[j][k].removeFirst();
            }

            this.field_78876_b[j][k].add(particle);
         }
      }

   }

   private void func_178922_a(int p_178922_1_) {
      this.field_78878_a.field_72984_F.func_76320_a(String.valueOf(p_178922_1_));

      for(int i = 0; i < 2; ++i) {
         this.field_78878_a.field_72984_F.func_76320_a(String.valueOf(i));
         this.func_187240_a(this.field_78876_b[p_178922_1_][i]);
         this.field_78878_a.field_72984_F.func_76319_b();
      }

      this.field_78878_a.field_72984_F.func_76319_b();
   }

   private void func_187240_a(Queue<Particle> p_187240_1_) {
      if (!p_187240_1_.isEmpty()) {
         Iterator<Particle> iterator = p_187240_1_.iterator();

         while(iterator.hasNext()) {
            Particle particle = iterator.next();
            this.func_178923_d(particle);
            if (!particle.func_187113_k()) {
               iterator.remove();
            }
         }
      }

   }

   private void func_178923_d(final Particle p_178923_1_) {
      try {
         p_178923_1_.func_189213_a();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.func_85055_a(throwable, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.func_85058_a("Particle being ticked");
         final int i = p_178923_1_.func_70537_b();
         crashreportcategory.func_189529_a("Particle", new ICrashReportDetail<String>() {
            public String call() throws Exception {
               return p_178923_1_.toString();
            }
         });
         crashreportcategory.func_189529_a("Particle Type", new ICrashReportDetail<String>() {
            public String call() throws Exception {
               if (i == 0) {
                  return "MISC_TEXTURE";
               } else if (i == 1) {
                  return "TERRAIN_TEXTURE";
               } else {
                  return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
               }
            }
         });
         throw new ReportedException(crashreport);
      }
   }

   public void func_78874_a(Entity p_78874_1_, float p_78874_2_) {
      float f = ActiveRenderInfo.func_178808_b();
      float f1 = ActiveRenderInfo.func_178803_d();
      float f2 = ActiveRenderInfo.func_178805_e();
      float f3 = ActiveRenderInfo.func_178807_f();
      float f4 = ActiveRenderInfo.func_178809_c();
      Particle.field_70556_an = p_78874_1_.field_70142_S + (p_78874_1_.field_70165_t - p_78874_1_.field_70142_S) * (double)p_78874_2_;
      Particle.field_70554_ao = p_78874_1_.field_70137_T + (p_78874_1_.field_70163_u - p_78874_1_.field_70137_T) * (double)p_78874_2_;
      Particle.field_70555_ap = p_78874_1_.field_70136_U + (p_78874_1_.field_70161_v - p_78874_1_.field_70136_U) * (double)p_78874_2_;
      Particle.field_190016_K = p_78874_1_.func_70676_i(p_78874_2_);
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.func_179092_a(516, 0.003921569F);

      for(final int i = 0; i < 3; ++i) {
         for(int j = 0; j < 2; ++j) {
            if (!this.field_78876_b[i][j].isEmpty()) {
               switch(j) {
               case 0:
                  GlStateManager.func_179132_a(false);
                  break;
               case 1:
                  GlStateManager.func_179132_a(true);
               }

               switch(i) {
               case 0:
               default:
                  this.field_78877_c.func_110577_a(field_110737_b);
                  break;
               case 1:
                  this.field_78877_c.func_110577_a(TextureMap.field_110575_b);
               }

               GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator tessellator = Tessellator.func_178181_a();
               BufferBuilder bufferbuilder = tessellator.func_178180_c();
               bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181704_d);

               for(final Particle particle : this.field_78876_b[i][j]) {
                  try {
                     particle.func_180434_a(bufferbuilder, p_78874_1_, p_78874_2_, f, f4, f1, f2, f3);
                  } catch (Throwable throwable) {
                     CrashReport crashreport = CrashReport.func_85055_a(throwable, "Rendering Particle");
                     CrashReportCategory crashreportcategory = crashreport.func_85058_a("Particle being rendered");
                     crashreportcategory.func_189529_a("Particle", new ICrashReportDetail<String>() {
                        public String call() throws Exception {
                           return particle.toString();
                        }
                     });
                     crashreportcategory.func_189529_a("Particle Type", new ICrashReportDetail<String>() {
                        public String call() throws Exception {
                           if (i == 0) {
                              return "MISC_TEXTURE";
                           } else if (i == 1) {
                              return "TERRAIN_TEXTURE";
                           } else {
                              return i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i;
                           }
                        }
                     });
                     throw new ReportedException(crashreport);
                  }
               }

               tessellator.func_78381_a();
            }
         }
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179084_k();
      GlStateManager.func_179092_a(516, 0.1F);
   }

   public void func_78872_b(Entity p_78872_1_, float p_78872_2_) {
      float f = 0.017453292F;
      float f1 = MathHelper.func_76134_b(p_78872_1_.field_70177_z * 0.017453292F);
      float f2 = MathHelper.func_76126_a(p_78872_1_.field_70177_z * 0.017453292F);
      float f3 = -f2 * MathHelper.func_76126_a(p_78872_1_.field_70125_A * 0.017453292F);
      float f4 = f1 * MathHelper.func_76126_a(p_78872_1_.field_70125_A * 0.017453292F);
      float f5 = MathHelper.func_76134_b(p_78872_1_.field_70125_A * 0.017453292F);

      for(int i = 0; i < 2; ++i) {
         Queue<Particle> queue = this.field_78876_b[3][i];
         if (!queue.isEmpty()) {
            Tessellator tessellator = Tessellator.func_178181_a();
            BufferBuilder bufferbuilder = tessellator.func_178180_c();

            for(Particle particle : queue) {
               particle.func_180434_a(bufferbuilder, p_78872_1_, p_78872_2_, f1, f5, f2, f3, f4);
            }
         }
      }

   }

   public void func_78870_a(@Nullable World p_78870_1_) {
      this.field_78878_a = p_78870_1_;

      for(int i = 0; i < 4; ++i) {
         for(int j = 0; j < 2; ++j) {
            this.field_78876_b[i][j].clear();
         }
      }

      this.field_178933_d.clear();
   }

   public void func_180533_a(BlockPos p_180533_1_, IBlockState p_180533_2_) {
      if (p_180533_2_.func_185904_a() != Material.field_151579_a) {
         p_180533_2_ = p_180533_2_.func_185899_b(this.field_78878_a, p_180533_1_);
         int i = 4;

         for(int j = 0; j < 4; ++j) {
            for(int k = 0; k < 4; ++k) {
               for(int l = 0; l < 4; ++l) {
                  double d0 = ((double)j + 0.5D) / 4.0D;
                  double d1 = ((double)k + 0.5D) / 4.0D;
                  double d2 = ((double)l + 0.5D) / 4.0D;
                  this.func_78873_a((new ParticleDigging(this.field_78878_a, (double)p_180533_1_.func_177958_n() + d0, (double)p_180533_1_.func_177956_o() + d1, (double)p_180533_1_.func_177952_p() + d2, d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, p_180533_2_)).func_174846_a(p_180533_1_));
               }
            }
         }

      }
   }

   public void func_180532_a(BlockPos p_180532_1_, EnumFacing p_180532_2_) {
      IBlockState iblockstate = this.field_78878_a.func_180495_p(p_180532_1_);
      if (iblockstate.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
         int i = p_180532_1_.func_177958_n();
         int j = p_180532_1_.func_177956_o();
         int k = p_180532_1_.func_177952_p();
         float f = 0.1F;
         AxisAlignedBB axisalignedbb = iblockstate.func_185900_c(this.field_78878_a, p_180532_1_);
         double d0 = (double)i + this.field_78875_d.nextDouble() * (axisalignedbb.field_72336_d - axisalignedbb.field_72340_a - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.field_72340_a;
         double d1 = (double)j + this.field_78875_d.nextDouble() * (axisalignedbb.field_72337_e - axisalignedbb.field_72338_b - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.field_72338_b;
         double d2 = (double)k + this.field_78875_d.nextDouble() * (axisalignedbb.field_72334_f - axisalignedbb.field_72339_c - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.field_72339_c;
         if (p_180532_2_ == EnumFacing.DOWN) {
            d1 = (double)j + axisalignedbb.field_72338_b - 0.10000000149011612D;
         }

         if (p_180532_2_ == EnumFacing.UP) {
            d1 = (double)j + axisalignedbb.field_72337_e + 0.10000000149011612D;
         }

         if (p_180532_2_ == EnumFacing.NORTH) {
            d2 = (double)k + axisalignedbb.field_72339_c - 0.10000000149011612D;
         }

         if (p_180532_2_ == EnumFacing.SOUTH) {
            d2 = (double)k + axisalignedbb.field_72334_f + 0.10000000149011612D;
         }

         if (p_180532_2_ == EnumFacing.WEST) {
            d0 = (double)i + axisalignedbb.field_72340_a - 0.10000000149011612D;
         }

         if (p_180532_2_ == EnumFacing.EAST) {
            d0 = (double)i + axisalignedbb.field_72336_d + 0.10000000149011612D;
         }

         this.func_78873_a((new ParticleDigging(this.field_78878_a, d0, d1, d2, 0.0D, 0.0D, 0.0D, iblockstate)).func_174846_a(p_180532_1_).func_70543_e(0.2F).func_70541_f(0.6F));
      }
   }

   public String func_78869_b() {
      int i = 0;

      for(int j = 0; j < 4; ++j) {
         for(int k = 0; k < 2; ++k) {
            i += this.field_78876_b[j][k].size();
         }
      }

      return "" + i;
   }
}
