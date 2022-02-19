package net.Duels.api;

import org.bukkit.entity.Player;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;

public class DuelsAPI {
	public static Duel getPlugin() {
		return Duel.getInstance();
	}

	public static int getKills(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getKills();
	}

	public static int getWins(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getWins();
	}

	public static int getDeaths(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getDeaths();
	}

	public static int getScore(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getScore();
	}

	public static int getBestStreak(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getBestStreak();
	}

	public static int getCoins(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getCoin();
	}

	public static int getXp(Player p) {
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(p.getUniqueId());
		return playerObject.getXp();
	}
}
