package net.Duels.scoreboard;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.impl.*;

public class ScoreboardManager {
	public static void firstScoreboard(PlayerObject playerObject) {
		if (playerObject.isOffline()) {
			return;
		}
		if (playerObject.getSetupData() != null) {
			new SetupScoreboard().setScoreboard(playerObject, true);
		} else if (Duel.getMainConfig().isOptionUseLobbyScoreboard() && !playerObject.inArena()) {
			new LobbyScoreboard().setScoreboard(playerObject, true);
		} else if (playerObject.inArena()) {
			if (playerObject.isSpectator()) {
				new SpectatorScoreboard().setScoreboard(playerObject, true);
				return;
			}
			if (playerObject.getArena().getArenaState() == Arena.ArenaState.WAIT) {
				new WaitScoreboard().setScoreboard(playerObject, true);
			} else {
				new GameScoreboard().setScoreboard(playerObject, true);
			}
		}
	}

	public static void updateScoreboard(PlayerObject playerObject) {
		if (playerObject.isOffline()) {
			return;
		}
		if (playerObject.getSetupData() != null) {
			new SetupScoreboard().setScoreboard(playerObject, false);
		} else if (Duel.getMainConfig().isOptionUseLobbyScoreboard() && !playerObject.inArena()) {
			new LobbyScoreboard().setScoreboard(playerObject, false);
		} else if (playerObject.inArena()) {
			if (playerObject.isSpectator()) {
				new SpectatorScoreboard().setScoreboard(playerObject, false);
				return;
			}
			if (playerObject.getArena().getArenaState() == Arena.ArenaState.WAIT) {
				new WaitScoreboard().setScoreboard(playerObject, false);
			} else {
				new GameScoreboard().setScoreboard(playerObject, false);
			}
		}
	}
}
