package net.Duels.api.impls;

import org.bukkit.entity.Player;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.Duels.Duel;
import net.Duels.player.PlayerObject;

public class MVdWKillAPI implements PlaceholderReplacer {

	@Override
	public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
		Player player = event.getPlayer();
		if (player == null) {
			return "<PLAYER NULL>";
		}

		PlayerObject playerObject = Duel.getPlayerController().getPlayer(player.getUniqueId());
		if (playerObject == null) {
			return "<DATA OBJECT NULL>";
		}

		return "" + playerObject.getKills();
	}

}
