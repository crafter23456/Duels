package net.Duels.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Duels.arenas.Arena;

public class DuelArenaCountingEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private final Arena arena;
	private final int remaining;

	public DuelArenaCountingEvent(Arena arena, int remaining) {
		this.arena = arena;
		this.remaining = remaining;
	}

	public HandlerList getHandlers() {
		return DuelArenaCountingEvent.handlers;
	}

	public Arena getArena() {
		return this.arena;
	}
	
	public int getRemaining() {
		return remaining;
	}
}
