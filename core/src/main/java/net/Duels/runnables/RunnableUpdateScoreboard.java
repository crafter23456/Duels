package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;

public class RunnableUpdateScoreboard extends BukkitRunnable {
	
	public void run() {
		for (PlayerObject playerObject : Duel.getPlayerController().getAll()) {
			ScoreboardManager.updateScoreboard(playerObject);
		}
	}
	
}
