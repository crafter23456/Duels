package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;

public class RunnableBossBarUpdate extends BukkitRunnable {
	
	public void run() {
		Duel.getPlayerController().getAll().forEach(playerObject -> Duel.getBossbar().update(playerObject.getPlayer()));
	}
}
