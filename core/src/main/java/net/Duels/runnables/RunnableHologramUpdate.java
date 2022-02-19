package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;

public class RunnableHologramUpdate extends BukkitRunnable {
	
	public void run() {
		Duel.getHologramController().updateAll();
	}
	
}
