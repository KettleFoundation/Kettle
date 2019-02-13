/*
 * Copyright (c) 2017 Daniel Ennis (Aikar) MIT License
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Fired anytime the server is about to merge 2 experience orbs into one
 */
public class ExperienceOrbMergeEvent extends EntityEvent implements Cancellable {
    private final ExperienceOrb mergeTarget;
    private final ExperienceOrb mergeSource;

    public ExperienceOrbMergeEvent(ExperienceOrb mergeTarget, ExperienceOrb mergeSource) {
        super(mergeTarget);
        this.mergeTarget = mergeTarget;
        this.mergeSource = mergeSource;
    }

    /**
     * @return The orb that will absorb the other experience orb
     */
    public ExperienceOrb getMergeTarget() {
        return mergeTarget;
    }

    /**
     * @return The orb that is subject to being removed and merged into the target orb
     */
    public ExperienceOrb getMergeSource() {
        return mergeSource;
    }

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @param cancel true if you wish to cancel this event, and prevent the orbs from merging
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
