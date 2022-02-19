package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;

public class RunnablePlayerSave extends BukkitRunnable {
	
	public void run() {
		for (PlayerObject playerObject : Duel.getPlayerController().getAll()) {
			Duel.getDataStorage().savePlayer(playerObject);
		}
	}
	
}
