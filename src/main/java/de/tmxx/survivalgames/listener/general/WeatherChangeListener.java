package de.tmxx.survivalgames.listener.general;

import de.tmxx.survivalgames.listener.RegisterAlways;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@RegisterAlways
public class WeatherChangeListener implements Listener {
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.getCause().equals(WeatherChangeEvent.Cause.NATURAL));
    }
}
