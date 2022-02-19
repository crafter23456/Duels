package net.Duels.api;

import net.Duels.Duel;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;

public class CitizenDataStore implements NPCDataStore {
	public void clearData(NPC npc) {
	}

	public int createUniqueNPCId(NPCRegistry npcRegistry) {
		return Duel.getNms().getCitizenID();
	}

	public void loadInto(NPCRegistry npcRegistry) {
	}

	public void saveToDisk() {
	}

	public void saveToDiskImmediate() {
	}

	public void store(NPC npc) {
	}

	public void storeAll(NPCRegistry npcRegistry) {
	}

	public void reloadFromSource() {
	}
}
