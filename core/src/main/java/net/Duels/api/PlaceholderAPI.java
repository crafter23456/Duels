package net.Duels.api;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.Duels.Duel;
import net.Duels.player.PlayerObject;
import net.Duels.utility.RankUtils;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPI extends PlaceholderExpansion {
	
	public boolean persist() {
		return true;
	}

	public boolean canRegister() {
		return true;
	}

	public @NotNull String getAuthor() {
		return Duel.getInstance().getDescription().getAuthors().toString();
	}

	public @NotNull String getIdentifier() {
		return "duel";
	}

	public @NotNull String getVersion() {
		return Duel.getInstance().getDescription().getVersion();
	}

	public String onPlaceholderRequest(Player player, @NotNull String identifier) {
		if (player == null) {
			return "<PLAYER NULL>";
		}
		if (identifier.equalsIgnoreCase("rank")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return RankUtils.getRank(po.getScore()) + RankUtils.getRankProcces(po.getScore());
		}
		if (identifier.equalsIgnoreCase("kills")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getKills());
		}
		if (identifier.equalsIgnoreCase("wins")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getWins());
		}
		if (identifier.equalsIgnoreCase("deaths")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getDeaths());
		}
		if (identifier.equalsIgnoreCase("loses")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getLose());
		}
		if (identifier.equalsIgnoreCase("score")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getScore());
		}
		if (identifier.equalsIgnoreCase("coins")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getCoin());
		}
		if (identifier.equalsIgnoreCase("xp")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getXp());
		}
		if (identifier.equalsIgnoreCase("kit")) {
			PlayerObject po = Duel.getPlayerController().getPlayer(player.getUniqueId());
			return String.valueOf(po.getKitSelected());
		}
		return "<INVALID REQUEST>";
	}
	
}
