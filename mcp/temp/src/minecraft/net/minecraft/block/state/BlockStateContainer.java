package net.minecraft.block.state;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MapPopulator;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Cartesian;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStateContainer {
   private static final Pattern field_185921_a = Pattern.compile("^[a-z0-9_]+$");
   private static final Function<IProperty<?>, String> field_177626_b = new Function<IProperty<?>, String>() {
      @Nullable
      public String apply(@Nullable IProperty<?> p_apply_1_) {
         return p_apply_1_ == null ? "<NULL>" : p_apply_1_.func_177701_a();
      }
   };
   private final Block field_177627_c;
   private final ImmutableSortedMap<String, IProperty<?>> field_177624_d;
   private final ImmutableList<IBlockState> field_177625_e;

   public BlockStateContainer(Block p_i45663_1_, IProperty<?>... p_i45663_2_) {
      this.field_177627_c = p_i45663_1_;
      Map<String, IProperty<?>> map = Maps.<String, IProperty<?>>newHashMap();

      for(IProperty<?> iproperty : p_i45663_2_) {
         func_185919_a(p_i45663_1_, iproperty);
         map.put(iproperty.func_177701_a(), iproperty);
      }

      this.field_177624_d = ImmutableSortedMap.copyOf(map);
      Map<Map<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation> map2 = Maps.<Map<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation>newLinkedHashMap();
      List<BlockStateContainer.StateImplementation> list1 = Lists.<BlockStateContainer.StateImplementation>newArrayList();

      for(List<Comparable<?>> list : Cartesian.func_179321_a(this.func_177620_e())) {
         Map<IProperty<?>, Comparable<?>> map1 = MapPopulator.<IProperty<?>, Comparable<?>>func_179400_b(this.field_177624_d.values(), list);
         BlockStateContainer.StateImplementation blockstatecontainer$stateimplementation = new BlockStateContainer.StateImplementation(p_i45663_1_, ImmutableMap.copyOf(map1));
         map2.put(map1, blockstatecontainer$stateimplementation);
         list1.add(blockstatecontainer$stateimplementation);
      }

      for(BlockStateContainer.StateImplementation blockstatecontainer$stateimplementation1 : list1) {
         blockstatecontainer$stateimplementation1.func_177235_a(map2);
      }

      this.field_177625_e = ImmutableList.copyOf(list1);
   }

   public static <T extends Comparable<T>> String func_185919_a(Block p_185919_0_, IProperty<T> p_185919_1_) {
      String s = p_185919_1_.func_177701_a();
      if (!field_185921_a.matcher(s).matches()) {
         throw new IllegalArgumentException("Block: " + p_185919_0_.getClass() + " has invalidly named property: " + s);
      } else {
         for(T t : p_185919_1_.func_177700_c()) {
            String s1 = p_185919_1_.func_177702_a(t);
            if (!field_185921_a.matcher(s1).matches()) {
               throw new IllegalArgumentException("Block: " + p_185919_0_.getClass() + " has property: " + s + " with invalidly named value: " + s1);
            }
         }

         return s;
      }
   }

   public ImmutableList<IBlockState> func_177619_a() {
      return this.field_177625_e;
   }

   private List<Iterable<Comparable<?>>> func_177620_e() {
      List<Iterable<Comparable<?>>> list = Lists.<Iterable<Comparable<?>>>newArrayList();
      ImmutableCollection<IProperty<?>> immutablecollection = this.field_177624_d.values();
      UnmodifiableIterator unmodifiableiterator = immutablecollection.iterator();

      while(unmodifiableiterator.hasNext()) {
         IProperty<?> iproperty = (IProperty)unmodifiableiterator.next();
         list.add(iproperty.func_177700_c());
      }

      return list;
   }

   public IBlockState func_177621_b() {
      return (IBlockState)this.field_177625_e.get(0);
   }

   public Block func_177622_c() {
      return this.field_177627_c;
   }

   public Collection<IProperty<?>> func_177623_d() {
      return this.field_177624_d.values();
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("block", Block.field_149771_c.func_177774_c(this.field_177627_c)).add("properties", Iterables.transform(this.field_177624_d.values(), field_177626_b)).toString();
   }

   @Nullable
   public IProperty<?> func_185920_a(String p_185920_1_) {
      return (IProperty)this.field_177624_d.get(p_185920_1_);
   }

   static class StateImplementation extends BlockStateBase {
      private final Block field_177239_a;
      private final ImmutableMap<IProperty<?>, Comparable<?>> field_177237_b;
      private ImmutableTable<IProperty<?>, Comparable<?>, IBlockState> field_177238_c;

      private StateImplementation(Block p_i45660_1_, ImmutableMap<IProperty<?>, Comparable<?>> p_i45660_2_) {
         this.field_177239_a = p_i45660_1_;
         this.field_177237_b = p_i45660_2_;
      }

      public Collection<IProperty<?>> func_177227_a() {
         return Collections.<IProperty<?>>unmodifiableCollection(this.field_177237_b.keySet());
      }

      public <T extends Comparable<T>> T func_177229_b(IProperty<T> p_177229_1_) {
         Comparable<?> comparable = (Comparable)this.field_177237_b.get(p_177229_1_);
         if (comparable == null) {
            throw new IllegalArgumentException("Cannot get property " + p_177229_1_ + " as it does not exist in " + this.field_177239_a.func_176194_O());
         } else {
            return (T)(p_177229_1_.func_177699_b().cast(comparable));
         }
      }

      public <T extends Comparable<T>, V extends T> IBlockState func_177226_a(IProperty<T> p_177226_1_, V p_177226_2_) {
         Comparable<?> comparable = (Comparable)this.field_177237_b.get(p_177226_1_);
         if (comparable == null) {
            throw new IllegalArgumentException("Cannot set property " + p_177226_1_ + " as it does not exist in " + this.field_177239_a.func_176194_O());
         } else if (comparable == p_177226_2_) {
            return this;
         } else {
            IBlockState iblockstate = (IBlockState)this.field_177238_c.get(p_177226_1_, p_177226_2_);
            if (iblockstate == null) {
               throw new IllegalArgumentException("Cannot set property " + p_177226_1_ + " to " + p_177226_2_ + " on block " + Block.field_149771_c.func_177774_c(this.field_177239_a) + ", it is not an allowed value");
            } else {
               return iblockstate;
            }
         }
      }

      public ImmutableMap<IProperty<?>, Comparable<?>> func_177228_b() {
         return this.field_177237_b;
      }

      public Block func_177230_c() {
         return this.field_177239_a;
      }

      public boolean equals(Object p_equals_1_) {
         return this == p_equals_1_;
      }

      public int hashCode() {
         return this.field_177237_b.hashCode();
      }

      public void func_177235_a(Map<Map<IProperty<?>, Comparable<?>>, BlockStateContainer.StateImplementation> p_177235_1_) {
         if (this.field_177238_c != null) {
            throw new IllegalStateException();
         } else {
            Table<IProperty<?>, Comparable<?>, IBlockState> table = HashBasedTable.<IProperty<?>, Comparable<?>, IBlockState>create();
            UnmodifiableIterator unmodifiableiterator = this.field_177237_b.entrySet().iterator();

            while(unmodifiableiterator.hasNext()) {
               Entry<IProperty<?>, Comparable<?>> entry = (Entry)unmodifiableiterator.next();
               IProperty<?> iproperty = (IProperty)entry.getKey();

               for(Comparable<?> comparable : iproperty.func_177700_c()) {
                  if (comparable != entry.getValue()) {
                     table.put(iproperty, comparable, p_177235_1_.get(this.func_177236_b(iproperty, comparable)));
                  }
               }
            }

            this.field_177238_c = ImmutableTable.copyOf(table);
         }
      }

      private Map<IProperty<?>, Comparable<?>> func_177236_b(IProperty<?> p_177236_1_, Comparable<?> p_177236_2_) {
         Map<IProperty<?>, Comparable<?>> map = Maps.<IProperty<?>, Comparable<?>>newHashMap(this.field_177237_b);
         map.put(p_177236_1_, p_177236_2_);
         return map;
      }

      public Material func_185904_a() {
         return this.field_177239_a.func_149688_o(this);
      }

      public boolean func_185913_b() {
         return this.field_177239_a.func_149730_j(this);
      }

      public boolean func_189884_a(Entity p_189884_1_) {
         return this.field_177239_a.func_189872_a(this, p_189884_1_);
      }

      public int func_185891_c() {
         return this.field_177239_a.func_149717_k(this);
      }

      public int func_185906_d() {
         return this.field_177239_a.func_149750_m(this);
      }

      public boolean func_185895_e() {
         return this.field_177239_a.func_149751_l(this);
      }

      public boolean func_185916_f() {
         return this.field_177239_a.func_149710_n(this);
      }

      public MapColor func_185909_g(IBlockAccess p_185909_1_, BlockPos p_185909_2_) {
         return this.field_177239_a.func_180659_g(this, p_185909_1_, p_185909_2_);
      }

      public IBlockState func_185907_a(Rotation p_185907_1_) {
         return this.field_177239_a.func_185499_a(this, p_185907_1_);
      }

      public IBlockState func_185902_a(Mirror p_185902_1_) {
         return this.field_177239_a.func_185471_a(this, p_185902_1_);
      }

      public boolean func_185917_h() {
         return this.field_177239_a.func_149686_d(this);
      }

      public boolean func_191057_i() {
         return this.field_177239_a.func_190946_v(this);
      }

      public EnumBlockRenderType func_185901_i() {
         return this.field_177239_a.func_149645_b(this);
      }

      public int func_185889_a(IBlockAccess p_185889_1_, BlockPos p_185889_2_) {
         return this.field_177239_a.func_185484_c(this, p_185889_1_, p_185889_2_);
      }

      public float func_185892_j() {
         return this.field_177239_a.func_185485_f(this);
      }

      public boolean func_185898_k() {
         return this.field_177239_a.func_149637_q(this);
      }

      public boolean func_185915_l() {
         return this.field_177239_a.func_149721_r(this);
      }

      public boolean func_185897_m() {
         return this.field_177239_a.func_149744_f(this);
      }

      public int func_185911_a(IBlockAccess p_185911_1_, BlockPos p_185911_2_, EnumFacing p_185911_3_) {
         return this.field_177239_a.func_180656_a(this, p_185911_1_, p_185911_2_, p_185911_3_);
      }

      public boolean func_185912_n() {
         return this.field_177239_a.func_149740_M(this);
      }

      public int func_185888_a(World p_185888_1_, BlockPos p_185888_2_) {
         return this.field_177239_a.func_180641_l(this, p_185888_1_, p_185888_2_);
      }

      public float func_185887_b(World p_185887_1_, BlockPos p_185887_2_) {
         return this.field_177239_a.func_176195_g(this, p_185887_1_, p_185887_2_);
      }

      public float func_185903_a(EntityPlayer p_185903_1_, World p_185903_2_, BlockPos p_185903_3_) {
         return this.field_177239_a.func_180647_a(this, p_185903_1_, p_185903_2_, p_185903_3_);
      }

      public int func_185893_b(IBlockAccess p_185893_1_, BlockPos p_185893_2_, EnumFacing p_185893_3_) {
         return this.field_177239_a.func_176211_b(this, p_185893_1_, p_185893_2_, p_185893_3_);
      }

      public EnumPushReaction func_185905_o() {
         return this.field_177239_a.func_149656_h(this);
      }

      public IBlockState func_185899_b(IBlockAccess p_185899_1_, BlockPos p_185899_2_) {
         return this.field_177239_a.func_176221_a(this, p_185899_1_, p_185899_2_);
      }

      public AxisAlignedBB func_185918_c(World p_185918_1_, BlockPos p_185918_2_) {
         return this.field_177239_a.func_180640_a(this, p_185918_1_, p_185918_2_);
      }

      public boolean func_185894_c(IBlockAccess p_185894_1_, BlockPos p_185894_2_, EnumFacing p_185894_3_) {
         return this.field_177239_a.func_176225_a(this, p_185894_1_, p_185894_2_, p_185894_3_);
      }

      public boolean func_185914_p() {
         return this.field_177239_a.func_149662_c(this);
      }

      @Nullable
      public AxisAlignedBB func_185890_d(IBlockAccess p_185890_1_, BlockPos p_185890_2_) {
         return this.field_177239_a.func_180646_a(this, p_185890_1_, p_185890_2_);
      }

      public void func_185908_a(World p_185908_1_, BlockPos p_185908_2_, AxisAlignedBB p_185908_3_, List<AxisAlignedBB> p_185908_4_, @Nullable Entity p_185908_5_, boolean p_185908_6_) {
         this.field_177239_a.func_185477_a(this, p_185908_1_, p_185908_2_, p_185908_3_, p_185908_4_, p_185908_5_, p_185908_6_);
      }

      public AxisAlignedBB func_185900_c(IBlockAccess p_185900_1_, BlockPos p_185900_2_) {
         return this.field_177239_a.func_185496_a(this, p_185900_1_, p_185900_2_);
      }

      public RayTraceResult func_185910_a(World p_185910_1_, BlockPos p_185910_2_, Vec3d p_185910_3_, Vec3d p_185910_4_) {
         return this.field_177239_a.func_180636_a(this, p_185910_1_, p_185910_2_, p_185910_3_, p_185910_4_);
      }

      public boolean func_185896_q() {
         return this.field_177239_a.func_185481_k(this);
      }

      public Vec3d func_191059_e(IBlockAccess p_191059_1_, BlockPos p_191059_2_) {
         return this.field_177239_a.func_190949_e(this, p_191059_1_, p_191059_2_);
      }

      public boolean func_189547_a(World p_189547_1_, BlockPos p_189547_2_, int p_189547_3_, int p_189547_4_) {
         return this.field_177239_a.func_189539_a(this, p_189547_1_, p_189547_2_, p_189547_3_, p_189547_4_);
      }

      public void func_189546_a(World p_189546_1_, BlockPos p_189546_2_, Block p_189546_3_, BlockPos p_189546_4_) {
         this.field_177239_a.func_189540_a(this, p_189546_1_, p_189546_2_, p_189546_3_, p_189546_4_);
      }

      public boolean func_191058_s() {
         return this.field_177239_a.func_176214_u(this);
      }

      public BlockFaceShape func_193401_d(IBlockAccess p_193401_1_, BlockPos p_193401_2_, EnumFacing p_193401_3_) {
         return this.field_177239_a.func_193383_a(p_193401_1_, this, p_193401_2_, p_193401_3_);
      }
   }
}
