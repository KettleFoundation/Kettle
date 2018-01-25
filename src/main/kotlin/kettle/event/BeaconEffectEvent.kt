package kettle.event

/**
 * Add new event from PaperSpigot, BeaconEffectEvent.
 * Called when an effect is being applied to a player.
 */

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.block.BlockEvent
import org.bukkit.potion.PotionEffect

class BeaconEffectEvent(block: Block,
        /**
         * Gets the effect being applied.
         * @return Potion effect
         */
                        /**
                         * Sets the potion effect that will be applied.
                         *
                         * @param effect Potion effect
                         */
                        var effect: PotionEffect?,
                        /**
                         * Gets the player who the potion effect is being applied to.
                         *
                         * @return Affected player
                         */
                        val player: Player,
                        /**
                         * Gets whether the effect is a primary beacon effect.
                         *
                         * @return true if this event represents a primary effect
                         */
                        val isPrimary: Boolean) : BlockEvent(block), Cancellable {
    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = true
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }


}
