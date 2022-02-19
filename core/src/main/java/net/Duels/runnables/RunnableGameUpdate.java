package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.arenas.Arena;

public class RunnableGameUpdate extends BukkitRunnable {
	
	public void run() {
		for (Arena arena : Duel.getArenaManager().getArenas()) {
			arena.onUpdate();
		}
	}
	
}
