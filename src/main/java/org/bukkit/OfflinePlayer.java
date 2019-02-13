package org.bukkit;

import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

public interface OfflinePlayer extends ServerOperator, AnimalTamer, ConfigurationSerializable {

    /**
     * Checks if this player is currently online
     *
     * @return true if they are online
     */
    public boolean isOnline();

    /**
     * Returns the name of this player
     * <p>
     * Names are no longer unique past a single game session. For persistent storage
     * it is recommended that you use {@link #getUniqueId()} instead.
     *
     * @return Player name or null if we have not seen a name for this player yet
     */
    public String getName();

    /**
     * Returns the UUID of this player
     *
     * @return Player UUID
     */
    public UUID getUniqueId();

    /**
     * Checks if this player is banned or not
     *
     * @return true if banned, otherwise false
     */
    public boolean isBanned();
    // Paper start

    /**
     * Permanently Bans this player from the server
     *
     * @param reason Reason for Ban
     * @return Ban Entry
     */
    public default BanEntry banPlayer(String reason) {
        return banPlayer(reason, null, null);
    }

    /**
     * Permanently Bans this player from the server
     * @param reason Reason for Ban
     * @param source Source of the ban, or null for default
     * @return Ban Entry
     */
    public default BanEntry banPlayer(String reason, String source) {
        return banPlayer(reason, null, source);
    }

    /**
     * Bans this player from the server
     * @param reason Reason for Ban
     * @param expires When to expire the ban
     * @return Ban Entry
     */
    public default BanEntry banPlayer(String reason, java.util.Date expires) {
        return banPlayer(reason, expires, null);
    }

    /**
     * Bans this player from the server
     * @param reason Reason for Ban
     * @param expires When to expire the ban
     * @param source Source of the ban or null for default
     * @return Ban Entry
     */
    public default BanEntry banPlayer(String reason, java.util.Date expires, String source) {
        return banPlayer(reason, expires, source, true);
    }
    public default BanEntry banPlayer(String reason, java.util.Date expires, String source, boolean kickIfOnline) {
        BanEntry banEntry = Bukkit.getServer().getBanList(BanList.Type.NAME).addBan(getName(), reason, expires, source);
        if (kickIfOnline && isOnline()) {
            getPlayer().kickPlayer(reason);
        }
        return banEntry;
    }
    // Paper end

    /**
     * Checks if this player is whitelisted or not
     *
     * @return true if whitelisted
     */
    public boolean isWhitelisted();

    /**
     * Sets if this player is whitelisted or not
     *
     * @param value true if whitelisted
     */
    public void setWhitelisted(boolean value);

    /**
     * Gets a {@link Player} object that this represents, if there is one
     * <p>
     * If the player is online, this will return that player. Otherwise,
     * it will return null.
     *
     * @return Online player
     */
    public Player getPlayer();

    /**
     * Gets the first date and time that this player was witnessed on this
     * server.
     * <p>
     * If the player has never played before, this will return 0. Otherwise,
     * it will be the amount of milliseconds since midnight, January 1, 1970
     * UTC.
     *
     * @return Date of first log-in for this player, or 0
     */
    public long getFirstPlayed();

    /**
     * Gets the last date and time that this player was witnessed on this
     * server.
     * <p>
     * If the player has never played before, this will return 0. Otherwise,
     * it will be the amount of milliseconds since midnight, January 1, 1970
     * UTC.
     *
     * @return Date of last log-in for this player, or 0
     * @deprecated The API contract is ambiguous and the implementation may or may not return the correct value given this API ambiguity. It is instead recommended use {@link #getLastLogin()} or {@link #getLastSeen()} depending on your needs.
     */
    @Deprecated
    public long getLastPlayed();

    /**
     * Checks if this player has played on this server before.
     *
     * @return True if the player has played before, otherwise false
     */
    public boolean hasPlayedBefore();

    /**
     * Gets the Location where the player will spawn at their bed, null if
     * they have not slept in one or their current bed spawn is invalid.
     *
     * @return Bed Spawn Location if bed exists, otherwise null.
     */
    public Location getBedSpawnLocation();

    // Paper start

    /**
     * Gets the last date and time that this player logged into the server.
     * <p>
     * If the player has never played before, this will return 0. Otherwise,
     * it will be the amount of milliseconds since midnight, January 1, 1970
     * UTC.
     *
     * @return last login time
     */
    public long getLastLogin();

    /**
     * Gets the last date and time that this player was seen on the server.
     * <p>
     * If the player has never played before, this will return 0. If the
     * player is currently online, this will return the current time.
     * Otherwise it will be the amount of milliseconds since midnight,
     * January 1, 1970 UTC.
     *
     * @return last seen time
     */
    public long getLastSeen();
    // Paper end

}
