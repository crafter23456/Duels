package net.Duels.scoreboard.impl;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import net.Duels.Duel;
import net.Duels.config.impl.ScoreboardConfig;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.BaseScoreboard;
import net.Duels.scoreboard.SmartScoreboard;
import net.Duels.utility.APIUtils;
import net.Duels.utility.Pair;
import net.Duels.utility.NMSUtils;
import net.Duels.utility.RankUtils;
import net.Duels.utility.TextUtils;

public class LobbyScoreboard extends BaseScoreboard {

	@Override
	public void setScoreboard(PlayerObject playerObject, boolean reset) {
		Scoreboard scoreboard = playerObject.getPlayer().getScoreboard();
		ScoreboardConfig.ScoreboardData data = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.LOBBY);
		if (scoreboard == null || reset) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		}
		Objective objective = scoreboard.getObjective("game");
		if (objective == null) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			objective = scoreboard.registerNewObjective("game", "dummy");
			objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', data.getTitle()));
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		SmartScoreboard sc = new SmartScoreboard(scoreboard, objective);
		int ping = NMSUtils.getPing(playerObject.getPlayer());
		for (String line : data.getLines()) {
			if (APIUtils.isPlaceholderAPI()) {
				line = PlaceholderAPI.setPlaceholders(playerObject.getPlayer(),
						ChatColor.translateAlternateColorCodes('&', line));
			} else {
				line = ChatColor.translateAlternateColorCodes('&', line);
			}
			if (data.isText_Adornment()) {
				line = TextUtils.replaceText(line);
			}
			line = line.replace("<ping>", String.valueOf(ping))
					.replace("<player>", playerObject.getName()).replace("<displayname>", playerObject.getDisplayName())
					.replace("<kills>", String.valueOf(playerObject.getKills()))
					.replace("<deaths>", String.valueOf(playerObject.getDeaths()))
					.replace("<wins>", String.valueOf(playerObject.getWins()))
					.replace("<lose>", String.valueOf(playerObject.getLose()))
					.replace("<score>", String.valueOf(playerObject.getScore()))
					.replace("<coins>", String.valueOf(playerObject.getCoin()))
					.replace("<xp>", String.valueOf(playerObject.getXp()))
					.replace("<beststreaks>", String.valueOf(playerObject.getBestStreak()))
					.replace("<online>",
							String.valueOf(Duel.getInstance().getServer().getOnlinePlayers().size()))
					.replace("<rank>", RankUtils.getRank(playerObject.getScore())
							+ RankUtils.getRankProcces(playerObject.getScore()));
			if (line.isEmpty()) {
				sc.blank();
			} else {
				sc.add(line);
			}
		}

		sc.updateScoreboard();
		playerObject.getPlayer().setScoreboard(sc.getScoreboard());

		if (Duel.getMainConfig().isOptionUseLobbyTablistName()) {
			playerObject.getPlayer().setPlayerListName(Duel.getMessageConfig().getString("tablist.lobby-displayName",
					Arrays.asList(
							new Pair<>("%%rank%%", RankUtils.getRank(playerObject.getScore())),
							new Pair<>("%%player%%", playerObject.getName()))));
		}
	}

}
