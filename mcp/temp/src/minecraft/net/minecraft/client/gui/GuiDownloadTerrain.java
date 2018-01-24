package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiDownloadTerrain extends GuiScreen {
   public void func_73866_w_() {
      this.field_146292_n.clear();
   }

   public void func_73863_a(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.func_146278_c(0);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("multiplayer.downloadingTerrain"), this.field_146294_l / 2, this.field_146295_m / 2 - 50, 16777215);
      super.func_73863_a(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   public boolean func_73868_f() {
      return false;
   }
}
