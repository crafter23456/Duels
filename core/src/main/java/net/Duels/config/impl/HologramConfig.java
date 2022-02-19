package net.Duels.config.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.Duel.LOG_LEVEL;
import net.Duels.config.BaseConfig;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.LocationUtils;

/**
 * 2020-11-16 ~ 2020-11-17
 * 
 * @author ProCooldaemon_
 */
public class HologramConfig extends BaseConfig {

	@Getter
	private final Map<HologramType, HologramData> holograms = new LinkedHashMap<>();

	public HologramConfig(JavaPlugin plugin) {
		super(plugin, "holograms.yml");
	}

	@Override
	public void load() {
		this.loadSection(HologramType.MY_STATS, "holograms.stats");
		this.loadSection(HologramType.LEADERBOARD_KILLS, "holograms.leaderboards.KILLS");
		this.loadSection(HologramType.LEADERBOARD_WINS, "holograms.leaderboards.WINS");
		this.loadSection(HologramType.LEADERBOARD_WINSTREAK, "holograms.leaderboards.WINSTREAK");
		this.loadSection(HologramType.LEADERBOARD_BESTSTREAK, "holograms.leaderboards.BESTSTREAK");
		this.loadSection(HologramType.LEADERBOARD_SCORE, "holograms.leaderboards.SCORE");
		this.loadSection(HologramType.LEADERBOARD_COIN, "holograms.leaderboards.COIN");
	}

	public void saveForHolograms() {
		this.saveSection(HologramType.MY_STATS, "holograms.stats");
		this.saveSection(HologramType.LEADERBOARD_KILLS, "holograms.leaderboards.KILLS");
		this.saveSection(HologramType.LEADERBOARD_WINS, "holograms.leaderboards.WINS");
		this.saveSection(HologramType.LEADERBOARD_WINSTREAK, "holograms.leaderboards.WINSTREAK");
		this.saveSection(HologramType.LEADERBOARD_BESTSTREAK, "holograms.leaderboards.BESTSTREAK");
		this.saveSection(HologramType.LEADERBOARD_SCORE, "holograms.leaderboards.SCORE");
		this.saveSection(HologramType.LEADERBOARD_COIN, "holograms.leaderboards.COIN");
		this.save();
	}

	public void addLocation(HologramType type, Location location) {
		this.holograms.get(type).addLocation(location);
		this.saveForHolograms();

		Duel.getHologramController().destoryAndUpdateAll();
	}

	public void removeLocation(HologramType type, Location location) {
		this.holograms.get(type).removeLocation(location);
		this.saveForHolograms();

		Duel.getHologramController().destoryAndUpdateAll();
	}

	public boolean containsLocation(HologramType type, Location location) {
		return this.holograms.get(type).containsLocation(location);
	}

	public HologramType findLocationType(Location location) {
		HologramType type = null;
		for (HologramType allType : HologramType.values()) {
			HologramData data = this.holograms.getOrDefault(allType, null);
			if (data == null) {
				continue;
			}

			if (data.getLocations().stream().anyMatch(targetLocation -> targetLocation.equals(location))) {
				type = allType;
				break;
			}
		}
		return type;
	}

	public List<Location> getAllLocations() {
		List<Location> locations = new LinkedList<>();
		for (HologramData data : this.holograms.values()) {
			locations.addAll(data.getLocations());
		}
		return locations;
	}

	private boolean loadSection(HologramType type, String path) {
		ConfigurationSection section = this.getSection(path);
		if (section == null) {
			return false;
		}

		List<String> lines = section.getStringList("lines");
		List<String> stringLocations = section.getStringList("locations");
		List<String> replacedLines = new LinkedList<>(ChatUtils.colorTranslate(lines));
		List<Location> locations = new LinkedList<>();

		stringLocations.forEach(stringLocation -> locations.add(LocationUtils.StringToLocation(stringLocation)));

		this.holograms.put(type, new HologramData(replacedLines, locations));
		return true;
	}

	private void saveSection(HologramType type, String path) {
		ConfigurationSection section = this.getSection(path);
		if (section == null) {
			return;
		}

		HologramData data = this.holograms.getOrDefault(type, null);
		if (data == null) {
			Duel.log(LOG_LEVEL.DEBUG,
					"An error occurred while saving the holographic section: Holographic data could not be found.");
			return;
		}

		List<String> locations = new LinkedList<>();
		data.getLocations().forEach(location -> locations.add(LocationUtils.LocationToString(location)));

		section.set("locations", locations);
	}

	private ConfigurationSection getSection(String path) {
		ConfigurationSection section = this.getConfig().getConfigurationSection(path);
		if (section == null) {
			Duel.log(LOG_LEVEL.ERROR, "Invaild Hologram Section (Nulled): " + path);
			return null;
		}
		if (!(section.contains("lines") && section.contains("locations"))) {
			Duel.log(LOG_LEVEL.ERROR, "Invalid Hologram Section: " + path);
			return null;
		}

		return section;
	}

	public enum HologramType {
		MY_STATS, LEADERBOARD_KILLS, LEADERBOARD_WINS, LEADERBOARD_WINSTREAK, LEADERBOARD_BESTSTREAK, LEADERBOARD_SCORE, LEADERBOARD_COIN;
	}

	public static class HologramData {

		@Getter
		private final List<String> lines;

		@Getter
		private final List<Location> locations;

		public HologramData(List<String> lines, List<Location> locations) {
			this.lines = lines;
			this.locations = locations;
		}

		public void addLocation(Location location) {
			this.locations.add(location);
		}

		public void removeLocation(Location location) {
			this.locations.remove(location);
		}

		public boolean containsLocation(Location location) {
			return this.locations.contains(location);
		}

	}

}
