package net.Duels.scoreboard.impl;

import java.text.SimpleDateFormat;
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
import net.Duels.utility.NMSUtils;
import net.Duels.utility.RankUtils;
import net.Duels.utility.TextUtils;

public class GameScoreboard extends BaseScoreboard {

	@Override
	public void setScoreboard(PlayerObject playerObject, boolean reset) {
		Scoreboard scoreboard = playerObject.getPlayer().getScoreboard();
		ScoreboardConfig.ScoreboardData inGameData = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.IN_GAME);
		ScoreboardConfig.ScoreboardData endingData = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.ENDING);
		Arena arena = playerObject.getArena();
		if (scoreboard == null || reset) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		}
		Objective objective = scoreboard.getObjective("game");
		Objective htobjective = scoreboard.getObjective("HealthTab");
		Objective hobjective = scoreboard.getObjective("health");
		Team myTeam = scoreboard.getTeam("001myTeam");
		Team otherTeam = scoreboard.getTeam("002otherTeam");
		
		if (objective == null) {
			objective = scoreboard.registerNewObjective("game", "dummy");
			objective.setDisplayName(ChatColor.translateAlternateColorCodes('&',
					(arena.getArenaState() == Arena.ArenaState.PLAY) ? inGameData.getTitle() : endingData.getTitle()));
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
		
		if (myTeam == null) {
			myTeam = scoreboard.registerNewTeam("001myTeam");
			myTeam.setPrefix(Duel.getMessageConfig().getString("tablist.prefixes.ingame-myTeam"));
			myTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
			for (PlayerObject teamPlayerObject : arena.getMyTeam(playerObject)) {
				myTeam.addEntry(teamPlayerObject.getName());
			}
		}
		if (otherTeam == null) {
			otherTeam = scoreboard.registerNewTeam("002otherTeam");
			otherTeam.setPrefix(Duel.getMessageConfig().getString("tablist.prefixes.ingame-enemyTeam"));
			otherTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);
			for (PlayerObject teamPlayerObject : arena.getOtherTeam(playerObject)) {
				otherTeam.addEntry(teamPlayerObject.getName());
			}
		}
		for (PlayerObject target : arena.getPlayers()) {
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
				(arena.getArenaState() == Arena.ArenaState.PLAY) ? inGameData.getLines() : endingData.getLines());
		SimpleDateFormat format = new SimpleDateFormat(
				Duel.getMessageConfig().getString("arenas.scoreboard.time-format"));
		Date date = new Date();
		for (String line : lines) {
			line = ChatColor.translateAlternateColorCodes('&', line);
			if (arena.getArenaState() == Arena.ArenaState.PLAY) {
				if (!inGameData.isText_Adornment()) {
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
					.replace("<enemy>",
							(arena.getEnemy(playerObject) != null) ? arena.getEnemy(playerObject).getName()
									: (ChatColor.RED + "NO ENEMY"))
					.replace("<kills>",
							String.valueOf(arena.getKills().getOrDefault(playerObject.getUniqueId(), 0)))
					.replace("<time>", arena.ingameTime())
					.replace("<kit>", (playerObject.getKitSelected() != null) ? playerObject.getKitSelected()
							: "Empty Kit Selected");
			if (line.isEmpty()) {
				sc.blank();
			} else {
				sc.add(line);
			}
		}
		sc.updateScoreboard();
		playerObject.getPlayer().setScoreboard(sc.getScoreboard());
	}

}
