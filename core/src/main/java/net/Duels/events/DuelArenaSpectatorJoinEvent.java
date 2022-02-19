package net.Duels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.Duels.arenas.Arena;

public class DuelArenaSpectatorJoinEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Arena arena;
	private final Player player;
	private boolean cancel;

	public DuelArenaSpectatorJoinEvent(Arena arena, Player player) {
		this.arena = arena;
		this.player = player;
		this.cancel = false;
	}

	public boolean isCancelled() {
		return this.cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public HandlerList getHandlers() {
		return DuelArenaSpectatorJoinEvent.handlers;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Player getPlayer() {
		return this.player;
	}
}
