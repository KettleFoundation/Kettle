package org.bukkit.event.entity;

import java.util.Collections;
import org.bukkit.entity.Entity;
import com.destroystokyo.paper.event.entity.EntityZapEvent;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Stores data for pigs being zapped
 */
public class PigZapEvent extends EntityZapEvent implements Cancellable {
    //private static final HandlerList handlers = new HandlerList();
    private boolean canceled;
    private final PigZombie pigzombie;
    private final LightningStrike bolt;

    public PigZapEvent(final Pig pig, final LightningStrike bolt, final PigZombie pigzombie) {
        super(pig, bolt, pigzombie);
        this.bolt = bolt;
        this.pigzombie = pigzombie;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    @Override
    public Pig getEntity() {
        return (Pig) entity;
    }

    /**
     * Gets the bolt which is striking the pig.
     *
     * @return lightning entity
     */
    public LightningStrike getLightning() {
        return bolt;
    }

    /**
     * Gets the zombie pig that will replace the pig, provided the event is
     * not cancelled first.
     *
     * @return resulting entity
     * @deprecated use {@link EntityTransformEvent#getTransformedEntity()}
     */
    @Deprecated
    public PigZombie getPigZombie() {
        return pigzombie;
    }

    // Paper start
    /*
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    */
    // Paper end
}
