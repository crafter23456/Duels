package net.Duels.utility;

import org.bukkit.plugin.Plugin;

import net.Duels.Duel;

public class APIUtils {
	
	public static boolean isCitizens() {
		Plugin plugin = Duel.getInstance().getServer().getPluginManager().getPlugin("Citizens");
		return plugin != null && plugin.isEnabled();
	}

	public static boolean isMVdWPlaceholderAPI() {
		Plugin plugin = Duel.getInstance().getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI");
		return plugin != null && plugin.isEnabled();
	}

	public static boolean isPlaceholderAPI() {
		Plugin plugin = Duel.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI");
		return plugin != null && plugin.isEnabled();
	}
	
}
