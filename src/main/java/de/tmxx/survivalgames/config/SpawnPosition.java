package de.tmxx.survivalgames.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * <p>
 *     A serialized spawn position used to store locations inside of configs. In contrast to {@link Location} this won't
 *     throw an error if a world is not yet loaded. This is especially useful because we don't want to load all worlds
 *     associated with the maps before we explicitly trigger it to save server resources.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SpawnPosition implements ConfigurationSerializable {
    private final String world;
    private final int x;
    private final int y;
    private final int z;
    private final float yaw;
    private final float pitch;

    /**
     * Create a new spawn position from the specified location.
     *
     * @param location the location
     */
    public SpawnPosition(Location location) {
        world = location.getWorld().getName();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    /**
     * Get the world associated with this spawn position. This may be null if the world is not yet loaded.
     *
     * @return the world or null
     */
    public @Nullable World getWorld() {
        return Bukkit.getWorld(world);
    }

    /**
     * Get the block location from this position. This returns the location with flat x, y and z coordinates. May return
     * null if the associated world is not yet loaded.
     *
     * @return the location or null
     */
    public Location getBlock() {
        World world = getWorld();
        if (world == null) return null;

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Get the centered location from this position. This adds 0.5 to the x and z coordinates. May return null if the
     * associated world is not yet loaded.
     *
     * @return the location or null
     */
    public Location getCentered() {
        World world = getWorld();
        if (world == null) return null;

        return new Location(world, (double) x + 0.5D, y, (double) z + 0.5D, yaw, pitch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "world", world,
                "x", x,
                "y", y,
                "z", z,
                "yaw", yaw,
                "pitch", pitch
        );
    }

    /**
     * See {@link ConfigurationSerializable}.
     */
    public static SpawnPosition deserialize(Map<String, Object> args) {
        return new SpawnPosition(
                (String) args.get("world"),
                (int) args.get("x"),
                (int) args.get("y"),
                (int) args.get("z"),
                (float) (double) args.get("yaw"),
                (float) (double) args.get("pitch")
        );
    }
}
