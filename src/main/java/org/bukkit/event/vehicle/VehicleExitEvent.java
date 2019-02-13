package org.bukkit.event.vehicle;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Raised when a living entity exits a vehicle.
 */
public class VehicleExitEvent extends VehicleEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final LivingEntity exited;
    private final boolean isCancellable; // Paper

    public VehicleExitEvent(Vehicle vehicle, LivingEntity exited, boolean isCancellable) { // Paper
        super(vehicle);
        this.exited = exited;
        // Paper start
        this.isCancellable = isCancellable;
    }

    public VehicleExitEvent(final Vehicle vehicle, final LivingEntity exited) {
        this(vehicle, exited, true);
        // Paper end
    }

    /**
     * Get the living entity that exited the vehicle.
     *
     * @return The entity.
     */
    public LivingEntity getExited() {
        return exited;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        // Paper start
        if (cancel && !isCancellable) {
            return;
        }
        this.cancelled = cancel;
    }

    public boolean isCancellable() {
        return isCancellable;
        // paper end
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
