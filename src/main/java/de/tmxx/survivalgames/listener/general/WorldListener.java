package de.tmxx.survivalgames.listener.general;

import de.tmxx.survivalgames.listener.RegisterAlways;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@RegisterAlways
public class WorldListener implements Listener {
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.getCause().equals(WeatherChangeEvent.Cause.NATURAL));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        world.getEntities().forEach(Entity::remove);
        world.setTime(7000);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setStorm(false);
        world.setThundering(false);
    }
}
