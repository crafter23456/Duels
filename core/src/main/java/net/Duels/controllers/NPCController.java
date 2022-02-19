package net.Duels.controllers;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.npc.DuelNPC;
import net.Duels.npc.NPCType;
import net.Duels.utility.LocationUtils;

public class NPCController {

	@Getter
	private final Duel plugin;

	@Getter
	private final Map<NPCType, List<DuelNPC>> npcMap = new LinkedHashMap<>();

	public NPCController(Duel plugin) {
		this.plugin = plugin;
		this.load();
	}

	private void load() {
		for (NPCType type : NPCType.values()) {
			List<DuelNPC> NPCList = new LinkedList<>();
			ConfigurationSection NPCSection = Duel.getNpcConfig().getConfig()
					.getConfigurationSection("npcs." + type.getIdentifier());

			if (NPCSection != null) {
				for (String id : NPCSection.getKeys(false)) {
					Location location = LocationUtils.StringToLocation(NPCSection.getString(id));
					if (location == null) {
						continue;
					}
					NPCList.add(new DuelNPC(id, type, location.clone()));
				}
			}
			this.npcMap.put(type, NPCList);
		}

		for (NPCType type : NPCType.values()) {
			for (DuelNPC duelNPC : this.npcMap.get(type)) {
				duelNPC.spawn();
			}
		}
	}

	public void shutdown() {
		NPCType[] values;
		for (int length = (values = NPCType.values()).length, i = 0; i < length; ++i) {
			NPCType type = values[i];
			for (DuelNPC duelNPC : this.npcMap.get(type)) {
				duelNPC.remove();
			}
		}
	}

	public boolean contains(NPCType type, String id) {
		for (DuelNPC duelNPC : this.npcMap.get(type)) {
			if (duelNPC.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public void addNPC(NPCType type, String id, Location location) {
		if (this.contains(type, id)) {
			return;
		}

		String loc = LocationUtils.LocationToString(location);
		DuelNPC duelNPC = new DuelNPC(id, type, location);
		Duel.getNpcConfig().getConfig().set("npcs." + type.getIdentifier() + "." + id, loc);
		Duel.getNpcConfig().save();
		duelNPC.spawn();
		this.npcMap.get(type).add(duelNPC);
	}

	public void removeNPC(NPCType type, String id) {
		if (!this.contains(type, id)) {
			return;
		}
		DuelNPC duelNPC = this.getNPC(type, id);
		if (duelNPC == null) {
			return;
		}
		duelNPC.remove();
		this.npcMap.get(type).remove(duelNPC);
		Duel.getNpcConfig().getConfig().set("npcs." + type.getIdentifier() + "." + id, null);
		Duel.getNpcConfig().save();
	}

	public DuelNPC getNPC(NPCType type, String id) {
		for (DuelNPC duelNPC : this.npcMap.get(type)) {
			if (duelNPC.getId().equals(id)) {
				return duelNPC;
			}
		}
		return null;
	}

}
