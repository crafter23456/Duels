package net.Duels.arenas;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.config.BaseConfig;
import net.Duels.utility.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class ArenaManager extends BaseConfig {

	@Getter
	public List<Arena> arenas;

	public ArenaManager(JavaPlugin plugin) {
		super(plugin, "arenas.yml");
	}

	@Override
	public void load() {
		this.arenas = new LinkedList<>();
		if (!this.getConfig().contains("arenas")) {
			return;
		}
		ConfigurationSection arenaSection = this.getConfig().getConfigurationSection("arenas");
		if (arenaSection == null) {
			return;
		}
		for (String name : arenaSection.getKeys(false)) {
			ConfigurationSection gameSection = arenaSection.getConfigurationSection(name);
			if (!gameSection.contains("enable")) {
				continue;
			}
			if (!gameSection.contains("waitingLocation")) {
				continue;
			}
			if (!gameSection.contains("spectatorLocation")) {
				continue;
			}
			if (!gameSection.contains("spawn1")) {
				continue;
			}
			if (!gameSection.contains("spawn2")) {
				continue;
			}
			if (!gameSection.getBoolean("enable")) {
				continue;
			}
            if (!gameSection.contains("max-build-y")) {
                continue;
            }
			if (!gameSection.contains("name")) {
				continue;
			}

			try {
				Location waitingLocation = ConfigUtils.stringToLocation(gameSection.getString("waitingLocation"));
				Location spectatorLocation = ConfigUtils.stringToLocation(gameSection.getString("spectatorLocation"));
				Location spawn1 = ConfigUtils.stringToLocation(gameSection.getString("spawn1"));
				Location spawn2 = ConfigUtils.stringToLocation(gameSection.getString("spawn2"));
                double y = gameSection.getDouble("max-build-y");
				String mapName = gameSection.getString("name");
				Arena arena = new Arena(name, waitingLocation, spectatorLocation, spawn1, spawn2, y, mapName);
				this.arenas.add(arena);
				Duel.log(Duel.LOG_LEVEL.INFO, "Arena " + name + " loaded!");
			} catch (Exception e) {
				Duel.log(Duel.LOG_LEVEL.ERROR, "An exception occurred while trying loading arenas!");
			}
		}
	}

	public void shutdown() {
		for (Arena arena : this.arenas) {
			arena.shutdown();
		}
	}

	public Arena getArena(String name) {
		for (Arena arena : this.arenas) {
			if (arena.getName().equalsIgnoreCase(name)) {
				return arena;
			}
		}
		return null;
	}

	public boolean contains(String name) {
		for (Arena arena : this.arenas) {
			if (arena.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public Arena createArena(String name, Location waitingLocation, Location spectatorLocation, Location spawn1,
			Location spawn2, double minPlayerY) {
		if (this.getArena(name) != null) {
			return null;
		}
		this.getConfig().set("arenas." + name + ".enable", true);
		this.getConfig().set("arenas." + name + ".waitingLocation", ConfigUtils.locationToString(waitingLocation));
		this.getConfig().set("arenas." + name + ".spectatorLocation", ConfigUtils.locationToString(spectatorLocation));
		this.getConfig().set("arenas." + name + ".spawn1", ConfigUtils.locationToString(spawn1));
		this.getConfig().set("arenas." + name + ".spawn2", ConfigUtils.locationToString(spawn2));
        this.getConfig().set("arenas." + name + ".max-build-y", minPlayerY);
		this.getConfig().set("arenas." + name + ".name", name);
		this.save();
		Arena arena = new Arena(name, waitingLocation, spectatorLocation, spawn1, spawn2, minPlayerY, name);
		this.arenas.add(arena);
		return arena;
	}

	public boolean deleteArena(String name) {
		if (this.getArena(name) == null) {
			return false;
		}
		Arena arena = this.getArena(name);
		if (arena.getArenaState() != Arena.ArenaState.WAIT) {
			return false;
		}
		this.getConfig().set("arenas." + name, null);
		this.arenas.remove(arena);
		this.save();
		return true;
	}

	public int getPlayerSize() {
		int size = 0;
		for (Arena arena : this.arenas) {
			size += arena.getPlayers().size();
		}
		return size;
	}
}
