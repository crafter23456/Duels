package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;

public class RunnableSignUpdate extends BukkitRunnable {
	
	public void run() {
		Duel.getSignConfig().update();
	}
	
}
