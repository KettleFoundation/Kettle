package org.kettlemc.kettle;

import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        name = Constants.NAME,
        modid = Constants.ID,
        version = Constants.MOD_VERSION,
        acceptedMinecraftVersions = "1.12.2",
        acceptableRemoteVersions = "*"
)
public class KettleMod {
    public static final Logger LOGGER = LogManager.getLogger("Kettle");
}
