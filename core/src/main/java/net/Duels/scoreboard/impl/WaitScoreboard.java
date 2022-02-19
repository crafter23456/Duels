package net.Duels.scoreboard.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.config.impl.ScoreboardConfig;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.BaseScoreboard;
import net.Duels.scoreboard.SmartScoreboard;
import net.Duels.utility.Pair;
import net.Duels.utility.NMSUtils;
import net.Duels.utility.RankUtils;
import net.Duels.utility.TextUtils;

public class WaitScoreboard extends BaseScoreboard {

	@Override
	public void setScoreboard(PlayerObject playerObject, boolean reset) {
		Scoreboard scoreboard = playerObject.getPlayer().getScoreboard();
		ScoreboardConfig.ScoreboardData waitData = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.WAITING_PLAYER);
		ScoreboardConfig.ScoreboardData countData = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.COUNTING);
		Arena arena = playerObject.getArena();
		if (scoreboard == null || reset) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		}
		Objective objective = scoreboard.getObjective("game");
		if (objective == null) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			objective = scoreboard.registerNewObjective("game", "dummy");
			objective.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					arena.isCounting() ? countData.getTitle() : waitData.getTitle()));
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		SmartScoreboard sc = new SmartScoreboard(scoreboard, objective);
		int ping = NMSUtils.getPing(playerObject.getPlayer());
		List<String> lines = new LinkedList<>(arena.isCounting() ? countData.getLines() : waitData.getLines());
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
		Date date = new Date();
		for (String line : lines) {
			line = ChatColor.translateAlternateColorCodes('&', line);
			if (arena.isCounting()) {
				if (!countData.isText_Adornment()) {
					break;
				}
			} else if (!waitData.isText_Adornment()) {
				break;
			}
			line = TextUtils.replaceText(line);
			line = line.replace("<ping>", String.valueOf(ping))
					.replace("<player>", playerObject.getName()).replace("<displayname>", playerObject.getDisplayName())
					.replace("<kills>", String.valueOf(playerObject.getKills()))
					.replace("<deaths>", String.valueOf(playerObject.getDeaths()))
					.replace("<wins>", String.valueOf(playerObject.getWins()))
					.replace("<lose>", String.valueOf(playerObject.getLose()))
					.replace("<score>", String.valueOf(playerObject.getScore()))
					.replace("<beststreaks>", String.valueOf(playerObject.getBestStreak()))
					.replace("<online>",
							String.valueOf(Duel.getInstance().getServer().getOnlinePlayers().size()))
					.replace("<rank>",
							RankUtils.getRank(playerObject.getScore())
									+ RankUtils.getRankProcces(playerObject.getScore()))
					.replace("<date>", format.format(date)).replace("<map>", arena.getDisplayName())
					.replace("<on>", String.valueOf(arena.getPlayers().size()))
					.replace("<max>", String.valueOf(arena.getMaxPlayerSize()))
					.replace("<server>", Duel.getInstance().getServer().getMotd())
					.replace("<time>", String.valueOf(arena.getMaxCount() - arena.getCount()));
			if (line.isEmpty()) {
				sc.blank();
			} else {
				sc.add(line);
			}
		}
		sc.updateScoreboard();
		playerObject.getPlayer().setScoreboard(sc.getScoreboard());
		if (Duel.getMainConfig().isOptionUseLobbyTablistName()) {
			playerObject.getPlayer().setPlayerListName(Duel.getMessageConfig().getString("tablist.waiting-displayName",
					Arrays.asList(new Pair<>("%%rank%%", RankUtils.getRank(playerObject.getScore())),
							new Pair<>("%%player%%", playerObject.getName()))));
		}
	}
}
