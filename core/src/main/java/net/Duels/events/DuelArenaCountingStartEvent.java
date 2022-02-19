package net.Duels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Duels.arenas.Arena;

public class DuelArenaCountingStartEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Arena arena;

	public DuelArenaCountingStartEvent(Arena arena) {
		this.arena = arena;
	}

	public HandlerList getHandlers() {
		return DuelArenaCountingStartEvent.handlers;
	}

	public Arena getArena() {
		return this.arena;
	}
}
