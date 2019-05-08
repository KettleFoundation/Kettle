package kettlefoundation.kettle.nms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import kettlefoundation.kettle.nms.utils.RemapUtils;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.transformer.MavenShade;

/**
 * MappingLoader
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/05/2019 - 08:50 AM
 */
public class MappingLoader {

  private static final String CRAFTBUKKIT_PREFIX = "org/bukkit/craftbukkit";

  private static void loadNMSMappings(JarMapping jarMapping, String mappingsVersion) throws IOException {
    Map<String, String> relocations = new HashMap<>();
    relocations.put("net.minecraft.server", "net.minecraft.server." + mappingsVersion);
    jarMapping.loadMappings(new BufferedReader(new InputStreamReader(MappingLoader.class.getClassLoader().getResourceAsStream("mappings/" + mappingsVersion + "/cbMappings.srg"))), new MavenShade(relocations), null, false);
  }

  public static JarMapping loadMappings() {
    JarMapping jarMapping = new JarMapping();
    try {
      jarMapping.packages.put(CRAFTBUKKIT_PREFIX + "/libs/com/google/gson", "com/google/gson");
      jarMapping.packages.put(CRAFTBUKKIT_PREFIX + "/libs/it/unimi/dsi/fastutil", "it/unimi/dsi/fastutil");
      jarMapping.packages.put(CRAFTBUKKIT_PREFIX + "/libs/jline", "jline");
      jarMapping.packages.put(CRAFTBUKKIT_PREFIX + "/libs/joptsimple", "joptsimple");
      jarMapping.methods.put("org/bukkit/Bukkit/getOnlinePlayers ()[Lorg/bukkit/entity/Player;", "getOnlinePlayers_1710");

      loadNMSMappings(jarMapping, RemapUtils.NMS_VERSION);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return jarMapping;
  }
}
