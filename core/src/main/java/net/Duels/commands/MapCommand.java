package net.Duels.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;
import org.jetbrains.annotations.NotNull;

public class MapCommand implements CommandExecutor {

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-console"));
			return true;
		}

		Player player = (Player) sender;
		if (!player.hasPermission("duel.command.map")) {
			player.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return true;
		}

		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			sender.sendMessage(Duel.getMessageConfig().getString("errors.blacklist-world-command"));
			return true;
		}

		if (playerObject.inArena()) {
			player.sendMessage(Duel.getMessageConfig().getString("arenas.ingame.map").replace("%%map%%",
					playerObject.getArena().getDisplayName()));
		} else {
			player.sendMessage(Duel.getMessageConfig().getString("errors.in-game"));
		}

		return true;
	}

}
