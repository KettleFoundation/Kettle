package net.minecraft.client.resources;

import java.util.List;

public interface IReloadableResourceManager extends IResourceManager
{
    /**
     * Releases all current resource packs, loads the given list, then triggers all listeners
     */
    void reloadResources(List<IResourcePack> resourcesPacksList);

    /**
     * Registers a listener to be invoked every time the resource manager reloads. NOTE: The listener is immediately
     * invoked once when it is registered.
     */
    void registerReloadListener(IResourceManagerReloadListener reloadListener);
}
