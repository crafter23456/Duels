package net.Duels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Duels.arenas.Arena;

public class DuelArenaCountingCancelEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Arena arena;

	public DuelArenaCountingCancelEvent(Arena arena) {
		this.arena = arena;
	}

	public HandlerList getHandlers() {
		return DuelArenaCountingCancelEvent.handlers;
	}

	public Arena getArena() {
		return this.arena;
	}
}
