package net.Duels.npc;

import lombok.Getter;

public enum NPCType {
	STATS_NPC("statsNPC"), PLAY_NPC("playNPC"), ACHIEVEMENT_NPC("achievementNPC");

	@Getter
	private final String identifier;

	private NPCType(String identifier) {
		this.identifier = identifier;
	}
}
