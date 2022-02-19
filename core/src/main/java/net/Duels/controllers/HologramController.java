package net.Duels.controllers;

import java.util.LinkedHashMap;
import java.util.UUID;
import org.bukkit.entity.Player;
import lombok.Getter;
import net.Duels.Duel;
import net.Duels.hologram.PlayerHologram;

public class HologramController {

	@Getter
	private final LinkedHashMap<UUID, PlayerHologram> playerHolograms = new LinkedHashMap<>();

	public HologramController() {
		Duel.getInstance().getServer().getOnlinePlayers().forEach(player -> this.addPlayerHologram(player));
	}

	public void addPlayerHologram(Player player) {
		UUID uuid = player.getUniqueId();
		if (this.playerHolograms.containsKey(uuid)) {
			return;
		}
		
		PlayerHologram playerHologram = new PlayerHologram(uuid);
		playerHologram.spawnHologram();
		this.playerHolograms.put(uuid, playerHologram);
	}

	public void removePlayerHologram(Player player) {
		UUID uuid = player.getUniqueId();

		this.destoryHologram(uuid);
		this.playerHolograms.remove(uuid);
	}

	public void destoryHologram(UUID uuid) {
		PlayerHologram hologram = this.playerHolograms.getOrDefault(uuid, null);
		if (hologram == null) {
			return;
		}

		hologram.destoryHologram();
	}

	public void updateAll() {
		this.playerHolograms.forEach((player, hologram) -> hologram.updateHologram());
	}

	public void destoryAll() {
		this.playerHolograms.forEach((player, hologram) -> hologram.destoryHologram());
	}

	public void destoryAndUpdateAll() {
		this.playerHolograms.forEach((player, hologram) -> hologram.destoryAndUpdate());
	}
	
	public void remappingAll() {
		Duel.getInstance().getServer().getOnlinePlayers().forEach(player -> {
			this.removePlayerHologram(player);
			this.addPlayerHologram(player);
		});
	}

	public PlayerHologram getHologram(UUID uuid) {
		return this.playerHolograms.getOrDefault(uuid, null);
	}

}
