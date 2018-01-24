package net.minecraft.potion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;

public class PotionType {
   private static final ResourceLocation field_185177_b = new ResourceLocation("empty");
   public static final RegistryNamespacedDefaultedByKey<ResourceLocation, PotionType> field_185176_a = new RegistryNamespacedDefaultedByKey<ResourceLocation, PotionType>(field_185177_b);
   private static int field_185178_c;
   private final String field_185179_d;
   private final ImmutableList<PotionEffect> field_185180_e;

   @Nullable
   public static PotionType func_185168_a(String p_185168_0_) {
      return field_185176_a.func_82594_a(new ResourceLocation(p_185168_0_));
   }

   public PotionType(PotionEffect... p_i46739_1_) {
      this((String)null, p_i46739_1_);
   }

   public PotionType(@Nullable String p_i46740_1_, PotionEffect... p_i46740_2_) {
      this.field_185179_d = p_i46740_1_;
      this.field_185180_e = ImmutableList.copyOf(p_i46740_2_);
   }

   public String func_185174_b(String p_185174_1_) {
      return this.field_185179_d == null ? p_185174_1_ + ((ResourceLocation)field_185176_a.func_177774_c(this)).func_110623_a() : p_185174_1_ + this.field_185179_d;
   }

   public List<PotionEffect> func_185170_a() {
      return this.field_185180_e;
   }

   public static void func_185175_b() {
      func_185173_a("empty", new PotionType(new PotionEffect[0]));
      func_185173_a("water", new PotionType(new PotionEffect[0]));
      func_185173_a("mundane", new PotionType(new PotionEffect[0]));
      func_185173_a("thick", new PotionType(new PotionEffect[0]));
      func_185173_a("awkward", new PotionType(new PotionEffect[0]));
      func_185173_a("night_vision", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76439_r, 3600)}));
      func_185173_a("long_night_vision", new PotionType("night_vision", new PotionEffect[]{new PotionEffect(MobEffects.field_76439_r, 9600)}));
      func_185173_a("invisibility", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76441_p, 3600)}));
      func_185173_a("long_invisibility", new PotionType("invisibility", new PotionEffect[]{new PotionEffect(MobEffects.field_76441_p, 9600)}));
      func_185173_a("leaping", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76430_j, 3600)}));
      func_185173_a("long_leaping", new PotionType("leaping", new PotionEffect[]{new PotionEffect(MobEffects.field_76430_j, 9600)}));
      func_185173_a("strong_leaping", new PotionType("leaping", new PotionEffect[]{new PotionEffect(MobEffects.field_76430_j, 1800, 1)}));
      func_185173_a("fire_resistance", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76426_n, 3600)}));
      func_185173_a("long_fire_resistance", new PotionType("fire_resistance", new PotionEffect[]{new PotionEffect(MobEffects.field_76426_n, 9600)}));
      func_185173_a("swiftness", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76424_c, 3600)}));
      func_185173_a("long_swiftness", new PotionType("swiftness", new PotionEffect[]{new PotionEffect(MobEffects.field_76424_c, 9600)}));
      func_185173_a("strong_swiftness", new PotionType("swiftness", new PotionEffect[]{new PotionEffect(MobEffects.field_76424_c, 1800, 1)}));
      func_185173_a("slowness", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76421_d, 1800)}));
      func_185173_a("long_slowness", new PotionType("slowness", new PotionEffect[]{new PotionEffect(MobEffects.field_76421_d, 4800)}));
      func_185173_a("water_breathing", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76427_o, 3600)}));
      func_185173_a("long_water_breathing", new PotionType("water_breathing", new PotionEffect[]{new PotionEffect(MobEffects.field_76427_o, 9600)}));
      func_185173_a("healing", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76432_h, 1)}));
      func_185173_a("strong_healing", new PotionType("healing", new PotionEffect[]{new PotionEffect(MobEffects.field_76432_h, 1, 1)}));
      func_185173_a("harming", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76433_i, 1)}));
      func_185173_a("strong_harming", new PotionType("harming", new PotionEffect[]{new PotionEffect(MobEffects.field_76433_i, 1, 1)}));
      func_185173_a("poison", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76436_u, 900)}));
      func_185173_a("long_poison", new PotionType("poison", new PotionEffect[]{new PotionEffect(MobEffects.field_76436_u, 1800)}));
      func_185173_a("strong_poison", new PotionType("poison", new PotionEffect[]{new PotionEffect(MobEffects.field_76436_u, 432, 1)}));
      func_185173_a("regeneration", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76428_l, 900)}));
      func_185173_a("long_regeneration", new PotionType("regeneration", new PotionEffect[]{new PotionEffect(MobEffects.field_76428_l, 1800)}));
      func_185173_a("strong_regeneration", new PotionType("regeneration", new PotionEffect[]{new PotionEffect(MobEffects.field_76428_l, 450, 1)}));
      func_185173_a("strength", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76420_g, 3600)}));
      func_185173_a("long_strength", new PotionType("strength", new PotionEffect[]{new PotionEffect(MobEffects.field_76420_g, 9600)}));
      func_185173_a("strong_strength", new PotionType("strength", new PotionEffect[]{new PotionEffect(MobEffects.field_76420_g, 1800, 1)}));
      func_185173_a("weakness", new PotionType(new PotionEffect[]{new PotionEffect(MobEffects.field_76437_t, 1800)}));
      func_185173_a("long_weakness", new PotionType("weakness", new PotionEffect[]{new PotionEffect(MobEffects.field_76437_t, 4800)}));
      func_185173_a("luck", new PotionType("luck", new PotionEffect[]{new PotionEffect(MobEffects.field_188425_z, 6000)}));
      field_185176_a.func_177776_a();
   }

   protected static void func_185173_a(String p_185173_0_, PotionType p_185173_1_) {
      field_185176_a.func_177775_a(field_185178_c++, new ResourceLocation(p_185173_0_), p_185173_1_);
   }

   public boolean func_185172_c() {
      if (!this.field_185180_e.isEmpty()) {
         UnmodifiableIterator unmodifiableiterator = this.field_185180_e.iterator();

         while(unmodifiableiterator.hasNext()) {
            PotionEffect potioneffect = (PotionEffect)unmodifiableiterator.next();
            if (potioneffect.func_188419_a().func_76403_b()) {
               return true;
            }
         }
      }

      return false;
   }
}
