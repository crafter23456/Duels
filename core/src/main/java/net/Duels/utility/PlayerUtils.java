package net.Duels.utility;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.Duels.Duel;

public class PlayerUtils {
	
	public static void teleportToLobby(Player player) {
		if (Duel.getMainConfig().isOptionJoinTeleport()) {
			Location location = Duel.getMainConfig().getLobby();
			if (location == null) {
				if (player.hasPermission("duel.admin")) {
					player.sendMessage(Duel.getMessageConfig().getString("errors.not-set-lobby"));
				}
			} else {
				player.teleport(Duel.getMainConfig().getLobby().clone());
			}
		}
	}
}
