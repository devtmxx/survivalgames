package de.tmxx.survivalgames.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Project: survivalgames
 * 11.02.2025
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

    public SpawnPosition(Location location) {
        world = location.getWorld().getName();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public Location getExact() {
        return new Location(
                getWorld(),
                x,
                y,
                z,
                yaw,
                pitch
        );
    }

    public Location getCentered() {
        return new Location(
                getWorld(),
                (double) x + 0.5D,
                y,
                (double) z + 0.5D,
                yaw,
                pitch
        );
    }

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
