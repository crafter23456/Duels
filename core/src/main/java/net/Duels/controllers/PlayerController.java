package net.Duels.controllers;

import java.util.*;

import org.bukkit.World;
import org.bukkit.entity.Player;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;

public class PlayerController {
	
	private final Map<UUID, PlayerObject> playerMap;

	public PlayerController() {
		this.playerMap = new LinkedHashMap<>();
		this.load();
	}

	private void load() {
		Duel.getInstance().getServer().getOnlinePlayers().forEach(player -> this.addPlayer(player.getUniqueId()));
	}
	
	public boolean canRegister(UUID uuid) {
		Player player = Duel.getInstance().getServer().getPlayer(uuid);
		World world = player.getWorld();
		
		for (String worldName : Duel.getMainConfig().getBlacklistWorlds()) {
			if (world.getName().equalsIgnoreCase(worldName)) {
				return false;
			}
		}
		
		return true;
	}

	public PlayerObject addPlayer(UUID uuid) {
		if (!this.canRegister(uuid)) {
			return null;
		}
		
		if (this.playerMap.containsKey(uuid)) {
			return this.playerMap.get(uuid);
		}
		
		PlayerObject object = new PlayerObject(uuid);
		this.playerMap.put(uuid, object);
		return object;
	}

	public void removePlayer(UUID uuid) {
		this.savePlayer(uuid);
		this.playerMap.remove(uuid);
	}

	public boolean containsPlayer(UUID uuid) {
		return this.playerMap.containsKey(uuid);
	}

	public void savePlayer(UUID uuid) {
		if (!this.playerMap.containsKey(uuid)) {
			return;
		}
		Duel.getDataStorage().savePlayer(this.playerMap.get(uuid));
	}

	public PlayerObject getPlayer(UUID uuid) {
		return this.playerMap.getOrDefault(uuid, null);
	}

	public Collection<PlayerObject> getAll() {
		return this.playerMap.values();
	}

	public void shutdown() {
		this.playerMap.keySet().forEach(this::savePlayer);
	} 
	
}
