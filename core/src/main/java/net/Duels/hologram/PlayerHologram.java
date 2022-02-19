package net.Duels.hologram;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.config.impl.HologramConfig.HologramData;
import net.Duels.config.impl.HologramConfig.HologramType;
import net.Duels.hologram.impls.LeaderboardHologram;
import net.Duels.hologram.impls.MyStatsHologram;
import net.Duels.player.PlayerObject;

public class PlayerHologram {

	@Getter
	private final UUID uuid;

	@Getter
	private final Map<HologramType, HologramGroup> holograms = new LinkedHashMap<>();

	public PlayerHologram(UUID uuid) {
		this.uuid = uuid;
		this.loadGroups();
	}

	public void loadGroups() {
		for (HologramType type : HologramType.values()) {
			HologramData data = Duel.getHologramConfig().getHolograms().getOrDefault(type, null);
			if (data == null) {
				continue;
			}

			HologramGroup group = new HologramGroup();
			for (Location location : data.getLocations()) {
				if (type == HologramType.MY_STATS) {
					group.addHologram(location, new MyStatsHologram(this.uuid, data.getLines(), location));
				} else {
					group.addHologram(location, new LeaderboardHologram(this.uuid, data.getLines(), location, type));
				}
			}

			this.holograms.put(type, group);
		}
	}

	public void spawnHologram() {
		this.holograms.forEach((type, group) -> group.getValueAll().forEach(hologram -> hologram.spawnHologram()));
	}

	public void destoryHologram() {
		this.holograms.forEach((type, group) -> group.getValueAll().forEach(hologram -> hologram.destoryHologram()));
	}

	public void updateHologram() {
		this.holograms.forEach((type, group) -> group.getValueAll().forEach(hologram -> hologram.updateHologram()));
	}

	public void destoryAndUpdate() {
		this.destoryHologram();
		this.spawnHologram();
	}

	public PlayerObject getPlayerObject() {
		return Duel.getPlayerController().getPlayer(this.uuid);
	}

}
