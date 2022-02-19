package net.Duels.scoreboard.impl;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.Duels.Duel;
import net.Duels.config.impl.ScoreboardConfig;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.BaseScoreboard;
import net.Duels.scoreboard.SmartScoreboard;
import net.Duels.utility.Pair;
import net.Duels.utility.NMSUtils;
import net.Duels.utility.RankUtils;
import net.Duels.utility.TextUtils;

public class SetupScoreboard extends BaseScoreboard {

	@Override
	public void setScoreboard(PlayerObject playerObject, boolean reset) {
		Scoreboard scoreboard = playerObject.getPlayer().getScoreboard();
		ScoreboardConfig.ScoreboardData data = Duel.getScoreboardConfig().getScoreboards()
				.get(ScoreboardConfig.ScoreboardType.SETUP);
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
		String name = ChatColor.RED + "<NONE>";
		if (playerObject.getSetupData() != null && playerObject.getSetupData().getName() != null) {
			name = ChatColor.GREEN + playerObject.getSetupData().getName();
		}
		String check_wait_location = ChatColor.GRAY + "NOT CHECK";
		if (playerObject.getSetupData() != null) {
			if (playerObject.getSetupData().getWaitingLocation() != null) {
				check_wait_location = ChatColor.GREEN + "CHECK";
			} else {
				check_wait_location = ChatColor.RED + "NOT SET";
			}
		}
		String check_spec_location = ChatColor.GRAY + "NOT CHECK";
		if (playerObject.getSetupData() != null) {
			if (playerObject.getSetupData().getSpectatorLocation() != null) {
				check_spec_location = ChatColor.GREEN + "CHECK";
			} else {
				check_spec_location = ChatColor.RED + "NOT SET";
			}
		}
		String check_spawn1_location = ChatColor.GRAY + "NOT CHECK";
		if (playerObject.getSetupData() != null) {
			if (playerObject.getSetupData().getSpawn1() != null) {
				check_spawn1_location = ChatColor.GREEN + "CHECK";
			} else {
				check_spawn1_location = ChatColor.RED + "NOT SET";
			}
		}
		String check_spawn2_location = ChatColor.GRAY + "NOT CHECK";
		if (playerObject.getSetupData() != null) {
			if (playerObject.getSetupData().getSpawn2() != null) {
				check_spawn2_location = ChatColor.GREEN + "CHECK";
			} else {
				check_spawn2_location = ChatColor.RED + "NOT SET";
			}
		}
		String check_max_build_y_location = ChatColor.GRAY + "NOT CHECK";
		if (playerObject.getSetupData() != null) {
			if (playerObject.getSetupData().getMaxBuildY() != -999.0) {
				check_max_build_y_location = ChatColor.GREEN + "CHECK";
			} else {
				check_max_build_y_location = ChatColor.RED + "NOT SET";
			}
		}
		String check_all = ChatColor.RED + "NOT COMPLETE";
		if (playerObject.getSetupData() != null && playerObject.getSetupData().compile()) {
			check_all = ChatColor.GREEN + "COMPLETE";
		}
		for (String line : data.getLines()) {
			line = ChatColor.translateAlternateColorCodes('&', line);
			if (data.isText_Adornment()) {
				line = TextUtils.replaceText(line);
			}
			line = line.replace("<ping>", String.valueOf(ping))
					.replace("<player>", playerObject.getName()).replace("<displayname>", playerObject.getDisplayName())
					.replace("<online>",
							String.valueOf(Duel.getInstance().getServer().getOnlinePlayers().size()))
					.replace("<name>", name).replace("<check_wait_location>", check_wait_location)
					.replace("<check_spec_location>", check_spec_location)
					.replace("<check_spawn1_location>", check_spawn1_location)
					.replace("<check_spawn2_location>", check_spawn2_location).replace("<check_max_build_y_location>", check_max_build_y_location).replace("<check_all>", check_all);
			if (line.isEmpty()) {
				sc.blank();
			} else {
				sc.add(line);
			}
		}
		sc.updateScoreboard();
		playerObject.getPlayer().setScoreboard(sc.getScoreboard());
		if (Duel.getMainConfig().isOptionUseLobbyTablistName()) {
			playerObject.getPlayer().setPlayerListName(Duel.getMessageConfig().getString("tablist.setup-displayName",
					Arrays.asList(new Pair<>("%%rank%%", RankUtils.getRank(playerObject.getScore())),
							new Pair<>("%%player%%", playerObject.getName()))));
		}
	}
}
