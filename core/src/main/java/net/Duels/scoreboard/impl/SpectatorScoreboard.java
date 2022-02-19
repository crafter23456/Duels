package net.Duels.scoreboard.impl;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

public class SpectatorScoreboard extends BaseScoreboard {

	@Override
	public void setScoreboard(PlayerObject playerObject, boolean reset) {
		Scoreboard scoreboard = playerObject.getPlayer().getScoreboard();
		ScoreboardConfig.ScoreboardData spectatorData = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.SPECTATOR);
		ScoreboardConfig.ScoreboardData endingData = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.ENDING);
		Arena arena = playerObject.getArena();
		if (scoreboard == null || reset) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		}
		Objective objective = scoreboard.getObjective("game");
		Objective htobjective = scoreboard.getObjective("HealthTab");
		Objective hobjective = scoreboard.getObjective("health");
		Team otherTeam = scoreboard.getTeam("001otherTeam");
		if (objective == null) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			objective = scoreboard.registerNewObjective("game", "dummy");
			objective.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					(arena.getArenaState() == Arena.ArenaState.PLAY) ? spectatorData.getTitle()
							: endingData.getTitle()));
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		if (hobjective == null) {
			hobjective = scoreboard.registerNewObjective("health", "health");
			hobjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
			hobjective.setDisplayName("§c \u2764");
		}

		if (htobjective == null) {
			htobjective = scoreboard.registerNewObjective("HealthTab", "dummy");
			htobjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
			htobjective.setDisplayName("§e");
		}

		if (otherTeam == null) {
			otherTeam = scoreboard.registerNewTeam("001otherTeam");
			otherTeam.setPrefix(String.valueOf(ChatColor.RED));
			otherTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
			for (PlayerObject teamPlayerObject : arena.getPlayers()) {
				otherTeam.addEntry(teamPlayerObject.getName());
			}
		}
		for (PlayerObject target : arena.getSpectators()) {
			double h = target.getPlayer().getHealth();
			if (target.getPlayer().hasPotionEffect(PotionEffectType.ABSORPTION)) {
				h += Duel.getNms().getAbsorptionHearts(target.getPlayer());
			}
			htobjective.getScore(target.getName()).setScore((int) h);
			hobjective.getScore(target.getName()).setScore((int) h);
		}
		SmartScoreboard sc = new SmartScoreboard(scoreboard, objective);
		int ping = NMSUtils.getPing(playerObject.getPlayer());
		List<String> lines = new LinkedList<>(
				(arena.getArenaState() == Arena.ArenaState.PLAY) ? spectatorData.getLines() : endingData.getLines());
		SimpleDateFormat format = new SimpleDateFormat(
				Duel.getMessageConfig().getString("arenas.scoreboard.time-format"));
		Date date = new Date();
		for (String line : lines) {
			line = ChatColor.translateAlternateColorCodes('&', line);
			if (arena.getArenaState() == Arena.ArenaState.PLAY) {
				if (!spectatorData.isText_Adornment()) {
					break;
				}
			} else if (!endingData.isText_Adornment()) {
				break;
			}
			line = TextUtils.replaceText(line);
			line = line.replace("<ping>", String.valueOf(ping))
					.replace("<player>", playerObject.getName()).replace("<displayname>", playerObject.getDisplayName())
					.replace("<deaths>", String.valueOf(playerObject.getDeaths()))
					.replace("<wins>", String.valueOf(playerObject.getWins()))
					.replace("<lose>", String.valueOf(playerObject.getLose()))
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
					.replace("<you>", playerObject.getPlayer().getName())
					.replace("<team_a>",
							(arena.getAPlayers().size() >= 1) ? arena.getAPlayers().get(0).getName()
									: (ChatColor.RED + "NO TEAM"))
					.replace("<team_b>",
							(arena.getBPlayers().size() >= 1) ? arena.getBPlayers().get(0).getName()
									: (ChatColor.RED + "NO TEAM"))
					.replace("<kills>", String.valueOf(arena.getKills().getOrDefault(playerObject.getUniqueId(), 0)))
					.replace("<time>", arena.ingameTime());
			if (line.isEmpty()) {
				sc.blank();
			} else {
				sc.add(line);
			}
		}
		sc.updateScoreboard();
		playerObject.getPlayer().setScoreboard(sc.getScoreboard());
		if (Duel.getMainConfig().isOptionUseLobbyTablistName()) {
			playerObject.getPlayer().setPlayerListName(Duel.getMessageConfig().getString("tablist.spectator-displayName",
					Arrays.asList(new Pair<>("%%rank%%", RankUtils.getRank(playerObject.getScore())),
							new Pair<>("%%player%%", playerObject.getName()))));
		}
	}
}