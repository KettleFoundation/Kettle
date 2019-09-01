package org.kettlemc.kettle.mixin.plugin;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kettlemc.kettle.config.KettleModConfig;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class KettlePlugin implements IMixinConfigPlugin {
    private static final Logger logger = LogManager.getLogger("Kettle");
    public static Boolean ENABLE_ILLEGAL_THREAD_ACCESS_WARNINGS = false;
    private KettleModConfig config;
    private boolean sponge;

    @Override
    public void onLoad(String mixinPackage) {
        logger.debug("Loading server configuration");
        config = KettleModConfig.loadConfig();

        if (!config.enableKettle) {
            logger.warn("Kettle has been disabled through the configuration, no plugins will be loaded.");
        }

        ENABLE_ILLEGAL_THREAD_ACCESS_WARNINGS = config.enableIllegalThreadAccessWarnings;

        try {
            Class.forName("org.spongepowered.mod.SpongeCoreMod");
            sponge = true;
        } catch (Exception e) {
            sponge = false;
        }

        if (sponge) {
            logger.error("Sponge has been detected! Kettle cannot be run side-by-side with Sponge at this time, please remove it and continue.");
        }
    }

    @Override
    public String getRefMapperConfig() {
        if (Launch.blackboard.get("fml.deobfuscatedEnvironment") == Boolean.TRUE) {
            return null;
        }

        return "mixins.kettle.refmap.json";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!config.enableKettle) {
            return false;
        }

        if (sponge) {
            logger.debug("SpongeForge is not detected. loading mixin '{}'", mixinClassName);
            return true;
        }

        if (targetClassName.startsWith("net.minecraft.client") && MixinEnvironment.getCurrentEnvironment().getSide() != MixinEnvironment.Side.CLIENT) {
            logger.debug("You are in a client-side environment, and Kettle cannot be run client-side, no patches will be made");
            return false;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}