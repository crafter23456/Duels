package net.Duels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Duels.arenas.Arena;

public class DuelArenaQuitEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Arena arena;
	private final Player player;

	public DuelArenaQuitEvent(Arena arena, Player player) {
		this.arena = arena;
		this.player = player;
	}

	public HandlerList getHandlers() {
		return DuelArenaQuitEvent.handlers;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Player getPlayer() {
		return this.player;
	}
}
