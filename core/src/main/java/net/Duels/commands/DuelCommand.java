package net.Duels.commands;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.arenas.Arena.ArenaState;
import net.Duels.config.impl.HologramConfig.HologramData;
import net.Duels.config.impl.HologramConfig.HologramType;
import net.Duels.datastorage.DataStorage;
import net.Duels.hologram.HologramObject;
import net.Duels.menus.PlayGUI;
import net.Duels.menus.StatsGUI;
import net.Duels.npc.NPCType;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;
import net.Duels.utility.APIUtils;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.Pair;
import net.Duels.utility.EventUtils;
import net.Duels.utility.KitUtils;
import net.Duels.utility.ValueUtils;
import org.jetbrains.annotations.NotNull;

public class DuelCommand implements CommandExecutor, TabCompleter {

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-console"));
			return true;
		}

		if (!sender.hasPermission("duel.command.default")) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return true;
		}

		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			sender.sendMessage(Duel.getMessageConfig().getString("errors.blacklist-world-command"));
			return true;
		}

		if (args.length <= 0) {
			this.help(sender);
			return true;
		}

		String subCommand = args[0];
		if (subCommand.equalsIgnoreCase("setup")) {
			this.setup_help(sender);
			return true;
		} else if (subCommand.equalsIgnoreCase("admin")) {
			this.admin_help(sender);
			return true;
		} else if (subCommand.equalsIgnoreCase("user")) {
			this.user_help(sender);
			return true;
		} else if (subCommand.equalsIgnoreCase("hologram")) {
			this.hologram_help(sender);
			return true;
		} else if (subCommand.equalsIgnoreCase("worldteleport")) {
			if (!sender.hasPermission("duel.command.createhologram")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length < 2) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate(
						"  &b\u25b6 &7/1vs1 worldteleport " + Duel.getMessageConfig().getString("commands.descriptions.admin.world-teleport")));
				sender.sendMessage("");
				return true;
			}
			if (Bukkit.getWorld(args[1]) == null) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.world-no-exist"));
				return true;
			}
			playerObject.getPlayer().teleport(Bukkit.getWorld(args[1]).getSpawnLocation());
			sender.sendMessage(Duel.getMessageConfig().getString("commands.world-teleport").replace("%%world%%", args[1]));
			return true;
		} else if (subCommand.equalsIgnoreCase("createHologram")) {
			if (!sender.hasPermission("duel.command.createhologram")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (args.length <= 1) {
				this.sendHologramType(sender);
				return true;
			}

			HologramType type;
			try {
				type = HologramType.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				this.sendHologramType(sender);
				return true;
			}

			double stack = 0.0D;
			HologramData data = Duel.getHologramConfig().getHolograms().getOrDefault(type, null);
			if (data == null) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-found-hologram-data"));
				return true;
			}

			for (String line : data.getLines()) {
				if (!HologramObject.isValidLine(line)) {
					continue;
				}

				Pair<Double, String> value = HologramObject.lineToData(line);
				stack += value.getA();
			}

			Duel.getHologramConfig().addLocation(type, player.getLocation().clone().add(0.0d, -1.75d + stack, 0.0d));
			Duel.getHologramController().remappingAll();

			sender.sendMessage(Duel.getMessageConfig().getString("commands.stats-hologram-added"));
			return true;
		} else if (subCommand.equalsIgnoreCase("deleteHologram")) {
			if (!sender.hasPermission("duel.command.deletehologram")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (args.length <= 1) {
				this.sendHologramType(sender);
				return true;
			}

			HologramType type;
			try {
				type = HologramType.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				this.sendHologramType(sender);
				return true;
			}

			Location playerLocation = player.getLocation();
			Location finalLocation = null;
			double distance = 0.0;
			for (Location hologramLocation : Duel.getHologramConfig().getAllLocations()) {
				if (!hologramLocation.getWorld().getUID().equals(playerLocation.getWorld().getUID())) {
					continue;
				}

				double tempDistance = playerLocation.distance(hologramLocation);
				if (tempDistance <= 16.0) {
					if (finalLocation != null) {
						if (tempDistance >= distance) {
							continue;
						}
						finalLocation = hologramLocation;
						distance = tempDistance;
					} else {
						finalLocation = hologramLocation;
						distance = tempDistance;
					}
				}
			}

			if (finalLocation == null || !Duel.getHologramConfig().containsLocation(type, finalLocation)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-contains-near-hologram"));
				return true;
			}

			/*
			 * HologramType type = Duel.getHologramConfig().findLocationType(finalLocation);
			 * if (type == null) { sender.sendMessage(Duel.getMessageConfig().getString(
			 * "errors.not-found-hologram-type")); return true; }
			 */

			Duel.getHologramConfig().removeLocation(type, finalLocation);
			Duel.getHologramController().remappingAll();

			sender.sendMessage(Duel.getMessageConfig().getString("commands.stats-hologram-deleted"));
			return true;
		} else if (subCommand.equalsIgnoreCase("updatehologram")) {
			if (!sender.hasPermission("duel.command.updatehologram")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			Duel.getHologramController().updateAll();
			sender.sendMessage(Duel.getMessageConfig().getString("commands.stats-hologram-updated"));
			return true;
		} else if (subCommand.equalsIgnoreCase("hologramForceUpdate")) {
			if (!sender.hasPermission("duel.command.hologramForceUpdate")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			Duel.getHologramController().destoryAndUpdateAll();
			sender.sendMessage(Duel.getMessageConfig().getString("commands.stats-hologram-forcibly-updated"));
			return true;
		} else if (subCommand.equalsIgnoreCase("draw")) {
			if (!sender.hasPermission("duel.command.draw")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate(
						"  &b\u25b6 &7/1vs1 draw " + Duel.getMessageConfig().getString("commands.descriptions.admin.draw")));
				sender.sendMessage("");
				return true;
			}

			Arena a = Duel.getArenaManager().getArena(args[1]);
			if (a == null) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-name"));
				return true;
			}

			Arena arena = playerObject.getArena();
			if (arena.getArenaState() == ArenaState.PLAY) {
				int cachedSize = arena.getMaxPlayerSize();
				arena.setMaxPlayerSize(arena.getPlayers().size());
				arena.setCount(arena.getMaxCount() - 3);

				new BukkitRunnable() {
					public void run() {
						arena.setMaxPlayerSize(cachedSize);
					}
				}.runTaskLater(Duel.getInstance(), 80L);

				sender.sendMessage(Duel.getMessageConfig().getString("commands.arena-drawn"));
			} else {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-playing-state"));
			}

			return true;
		} else if (subCommand.equalsIgnoreCase("spectate")) {
			if (!sender.hasPermission("duel.command.spectate")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (playerObject.isSpectator() || playerObject.inArena()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.spectator-already-belong"));
				return true;
			}

			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 spectate "
						+ Duel.getMessageConfig().getString("commands.descriptions.user.spectate")));
				sender.sendMessage("");
				return true;
			}

			Arena arena = Duel.getArenaManager().getArena(args[1]);
			if (arena == null) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-name"));
				return true;
			}

			arena.addSpectator(playerObject);
			return true;
		} else if (subCommand.equalsIgnoreCase("setlobby")) {
			if (!sender.hasPermission("duel.command.setlobby")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			Duel.getMainConfig().setLobby(player.getLocation());
			sender.sendMessage(Duel.getMessageConfig().getString("commands.set-lobby"));
			return true;
		} else if (subCommand.equalsIgnoreCase("addstatsnpc")) {
			if (!sender.hasPermission("duel.command.addstatsnpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}

			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addstatsnpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.addplaynpc")));
				sender.sendMessage("");
				return true;
			}

			String id = args[1];
			if (Duel.getNpcController().contains(NPCType.STATS_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.contains-stats-npc"));
				return true;
			}

			Duel.getNpcController().addNPC(NPCType.STATS_NPC, id, player.getLocation());
			sender.sendMessage(Duel.getMessageConfig().getString("commands.stats-npc-added").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("addplaynpc")) {
			if (!sender.hasPermission("duel.command.addplaynpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}

			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addplaynpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.addplaynpc")));
				sender.sendMessage("");
				return true;
			}

			String id = args[1];
			if (Duel.getNpcController().contains(NPCType.PLAY_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.contains-play-npc"));
				return true;
			}

			Duel.getNpcController().addNPC(NPCType.PLAY_NPC, id, player.getLocation());
			sender.sendMessage(Duel.getMessageConfig().getString("commands.play-npc-added").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("addtrailshopnpc")) {
			if (!sender.hasPermission("duel.command.addtrailshopnpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}

			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addtrailshopnpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.addtrailshopnpc")));
				sender.sendMessage("");
				return true;
			}

			String id = args[1];
			if (Duel.getNpcController().contains(NPCType.TRAIL_SHOP_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.contains-trailshop-npc"));
				return true;
			}

			Duel.getNpcController().addNPC(NPCType.TRAIL_SHOP_NPC, id, player.getLocation());
			sender.sendMessage(Duel.getMessageConfig().getString("commands.trailshop-npc-added").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("addachievementnpc")) {
			if (!sender.hasPermission("duel.command.addachievementnpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addachievementnpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.addachievementnpc")));
				sender.sendMessage("");
				return true;
			}
			String id = args[1];
			if (Duel.getNpcController().contains(NPCType.ACHIEVEMENT_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.contains-achievement-npc"));
				return true;
			}
			Duel.getNpcController().addNPC(NPCType.ACHIEVEMENT_NPC, id, player.getLocation());
			sender.sendMessage(
					Duel.getMessageConfig().getString("commands.achievement-npc-added").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("removestatsnpc")) {
			if (!sender.hasPermission("duel.command.removestatsnpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removestatsnpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.removestatsnpc")));
				sender.sendMessage("");
				return true;
			}
			String id = args[1];
			if (!Duel.getNpcController().contains(NPCType.STATS_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-contains-stats-npc"));
				return true;
			}
			Duel.getNpcController().removeNPC(NPCType.STATS_NPC, id);
			sender.sendMessage(Duel.getMessageConfig().getString("commands.stats-npc-removed").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("removeplaynpc")) {
			if (!sender.hasPermission("duel.command.removeplaynpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removeplaynpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.removeplaynpc")));
				sender.sendMessage("");
				return true;
			}
			String id = args[1];
			if (!Duel.getNpcController().contains(NPCType.PLAY_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-contains-play-npc"));
				return true;
			}
			Duel.getNpcController().removeNPC(NPCType.PLAY_NPC, id);
			sender.sendMessage(Duel.getMessageConfig().getString("commands.play-npc-removed").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("removetrailshopnpc")) {
			if (!sender.hasPermission("duel.command.removetrailshopnpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removetrailshopnpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.removetrailshopnpc")));
				sender.sendMessage("");
				return true;
			}
			String id = args[1];
			if (!Duel.getNpcController().contains(NPCType.TRAIL_SHOP_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-contains-trailshop-npc"));
				return true;
			}
			Duel.getNpcController().removeNPC(NPCType.TRAIL_SHOP_NPC, id);
			sender.sendMessage(
					Duel.getMessageConfig().getString("commands.trailshop-npc-removed").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("removeachievementnpc")) {
			if (!sender.hasPermission("duel.command.removeachievementnpc")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (!Duel.getMainConfig().isCitizensAPI() || !APIUtils.isCitizens()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.api-citizen"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removeachievmentnpc "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.removeachievementnpc")));
				sender.sendMessage("");
				return true;
			}
			String id = args[1];
			if (!Duel.getNpcController().contains(NPCType.ACHIEVEMENT_NPC, id)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.not-contains-achievement-npc"));
				return true;
			}
			Duel.getNpcController().removeNPC(NPCType.ACHIEVEMENT_NPC, id);
			sender.sendMessage(
					Duel.getMessageConfig().getString("commands.achievement-npc-removed").replace("%%id%%", id));
			return true;
		} else if (subCommand.equalsIgnoreCase("create")) {
			if (!sender.hasPermission("duel.command.create")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 create "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.create")));
				sender.sendMessage("");
				return true;
			}
			String arenaName = args[1];
			if (Duel.getArenaManager().contains(arenaName)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-name"));
				return true;
			}
			playerObject.setSetupData(new PlayerObject.SETUP_DATA());
			playerObject.getSetupData().setName(arenaName);
			ScoreboardManager.firstScoreboard(playerObject);
			KitUtils.setupItem(player, playerObject);
			sender.sendMessage(Duel.getMessageConfig().getString("arenas.arena-ready-to-create"));
			return true;
		} else if (subCommand.equalsIgnoreCase("delete")) {
			if (!sender.hasPermission("duel.command.delete")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 delete "
						+ Duel.getMessageConfig().getString("commands.descriptions.setup.delete")));
				sender.sendMessage("");
				return true;
			}
			String arenaName = args[1];
			if (!Duel.getArenaManager().contains(arenaName)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-found"));
				return true;
			}
			Arena arena2 = Duel.getArenaManager().getArena(arenaName);
			if (arena2.getArenaState() != Arena.ArenaState.WAIT) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-delete-status"));
				return true;
			}
			for (PlayerObject inPlayer : arena2.getPlayers()) {
				arena2.removePlayer(inPlayer);
				inPlayer.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.arena-delete-kick"));
			}
			Duel.getArenaManager().deleteArena(arenaName);
			sender.sendMessage(
					Duel.getMessageConfig().getString("arenas.arena-deleted").replace("%%name%%", arenaName));
			return true;
		} else if (subCommand.equalsIgnoreCase("join")) {
			if (!sender.hasPermission("duel.command.join")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate(
						"  &b\u25b6 &7/1vs1 join " + Duel.getMessageConfig().getString("commands.descriptions.user.join")));
				sender.sendMessage("");
				return true;
			}
			if (playerObject.getSetupData() != null) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.join-in-setup"));
				return true;
			}
			if (playerObject.inArena()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.join-in-game"));
				return true;
			}
			String arenaName = args[1];
			if (!Duel.getArenaManager().contains(arenaName)) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-found"));
				return true;
			}
			Arena arena2 = Duel.getArenaManager().getArena(arenaName);
			if (arena2.getArenaState() != Arena.ArenaState.WAIT) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-started"));
				return true;
			}
			if (arena2.isFull()) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-is-full"));
				return true;
			}
			arena2.addPlayer(playerObject);
			return true;
		} else if (subCommand.equalsIgnoreCase("start")) {
			if (!sender.hasPermission("duel.command.start")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate(
						"  &b\u25b6 &7/1vs1 start " + Duel.getMessageConfig().getString("commands.descriptions.admin.start")));
				sender.sendMessage("");
				return true;
			}
			Arena arena3 = Duel.getArenaManager().getArena(args[1]);
			if (arena3 != null) {
				if (arena3.getArenaState() != Arena.ArenaState.WAIT) {
					sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-started"));
				} else {
					if (arena3.getPlayers().isEmpty()) {
						sender.sendMessage(Duel.getMessageConfig().getString("errors.start-game"));
						return true;
					}
					arena3.setMaxPlayerSize(arena3.getPlayers().size());
					sender.sendMessage(
							Duel.getMessageConfig().getString("arenas.arena-force-start").replace("%%name%%", args[1]));
				}
			} else {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-found"));
			}
			return true;
		} else if (subCommand.equalsIgnoreCase("stop")) {
			if (!sender.hasPermission("duel.command.stop")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage("");
				sender.sendMessage(ChatUtils.colorTranslate(
						"  &b\u25b6 &7/1vs1 stop " + Duel.getMessageConfig().getString("commands.descriptions.admin.stop")));
				sender.sendMessage("");
				return true;
			}
			Arena arena3 = Duel.getArenaManager().getArena(args[1]);
			if (arena3 != null) {
				if (arena3.getArenaState() == Arena.ArenaState.WAIT) {
					sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-ended"));
					return true;
				} else {
					arena3.endGame();
					sender.sendMessage(
							Duel.getMessageConfig().getString("arenas.arena-force-end").replace("%%name%%", args[1]));
					return true;
				}
			} else {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.arena-found"));
			}
			return true;
		} else if (subCommand.equalsIgnoreCase("reload")) {
			if (!sender.hasPermission("duel.command.reload")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			try {
				Duel.getInstance().onDisable();
				Duel.getInstance().onEnable();
				sender.sendMessage(Duel.getMessageConfig().getString("commands.reload-success"));
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(Duel.getMessageConfig().getString("errors.reload-fail"));
			}
			return true;
		} else if (subCommand.equalsIgnoreCase("randomjoin")) {
			if (!sender.hasPermission("duel.command.randomjoin")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			EventUtils.onItemType(playerObject, null, "random_join", Action.RIGHT_CLICK_AIR);
			return true;
		} else if (subCommand.equalsIgnoreCase("list")) {
			if (!sender.hasPermission("duel.command.list")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			for (Arena arena : Duel.getArenaManager().getArenas()) {
				sender.sendMessage(arena.getArenaStatus());
			}
		} else if (subCommand.equalsIgnoreCase("leave")) {
			if (!sender.hasPermission("duel.command.leave")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (playerObject.inArena()) {
				Arena arena3 = playerObject.getArena();
				arena3.removePlayer(playerObject);
			} else {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.in-game"));
			}
			return true;
		} else if (subCommand.equalsIgnoreCase("play")) {
			if (!sender.hasPermission("duel.command.play")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			new PlayGUI(playerObject, 1);
			return true;
		} else if (subCommand.equalsIgnoreCase("stats")) {
			if (!sender.hasPermission("duel.command.stats")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length == 1) {
				new StatsGUI(playerObject);
			}
			final Player t = Bukkit.getPlayer(args[1]);
			if (t == null) {
				return true;
			}
			new StatsGUI(playerObject);
			return true;
		} else if (subCommand.equalsIgnoreCase("top")) {
			if (!sender.hasPermission("duel.command.top")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}
			if (args.length <= 1) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.no-top-type"));
				return true;
			}
			DataStorage.StatType statType = DataStorage.StatType.parseType(args[1]);
			if (statType == null) {
				sender.sendMessage(Duel.getMessageConfig().getString("errors.no-top-type"));
				return true;
			}
			sender.sendMessage(Duel.getMessageConfig().getString("commands.top-header"));
			List<DataStorage.StatObject> list = Duel.getDataStorage().getStats(statType);
			for (int i = 0; i < 10 && list.size() > i; ++i) {
				DataStorage.StatObject statObject = list.get(i);
				sender.sendMessage(Duel.getMessageConfig().getString("commands.top-message")
						.replace("%%index%%", String.valueOf(i + 1))
						.replace("%%name%%", statObject.getName()).replace("%%value%%", String.valueOf(ValueUtils.getValueByStatType(statObject, statType))));
			}
			sender.sendMessage(Duel.getMessageConfig().getString("commands.top-footer"));
			return true;
		} else {
			if (!subCommand.equalsIgnoreCase("duel")) {
				this.help(sender);
				return true;
			}
			if (!sender.hasPermission("duel.command.duel")) {
				sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
				return true;
			}

			sender.sendMessage(Duel.getMessageConfig().getString("commands.still-in-development"));
			return true;
		}
		return true;
	}

	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		List<String> tab = new LinkedList<>();
		if (args.length == 1) {
			if (sender.hasPermission("duel.command.help")) {
				tab.addAll(Arrays.asList("setup", "admin", "user"));
			}
			if (sender.hasPermission("duel.command.help.setup")) {
				tab.addAll(Arrays.asList("create", "delete", "setlobby", "addstatsnpc", "addplaynpc", "addtrailshopnpc",
						"addachievementnpc", "removestatsnpc", "removeplaynpc", "removetrailshopnpc",
						"removeachievementnpc"));
			}
			if (sender.hasPermission("duel.command.help.admin")) {
				tab.addAll(Arrays.asList("worldteleport", "start", "stop", "draw", "reload"));
			}
			if (sender.hasPermission("duel.command.help.hologram")) {
				tab.addAll(Arrays.asList("createhologram", "deletehologram", "updatehologram", "hologramForceUpdate"));
			}
			if (sender.hasPermission("duel.command.help.user")) {
				tab.addAll(Arrays.asList("join", "randomjoin", "play", "leave", "top", "stats", "list", "duel",
						"spectate"));
			}
		}
		return tab;
	}

	private void help(CommandSender sender) {
		if (!sender.hasPermission("duel.command.help")) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return;
		}
		sender.sendMessage(ChatUtils.colorTranslate("&f&m-----------------------------"));
		sender.sendMessage(ChatUtils.colorTranslate("   &bDuel by Yenil"));
		sender.sendMessage("");
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 setup "
				+ Duel.getMessageConfig().getString("commands.descriptions.default-setup")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 admin "
				+ Duel.getMessageConfig().getString("commands.descriptions.default-admin")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 hologram "
				+ Duel.getMessageConfig().getString("commands.descriptions.default-hologram")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 user " + Duel.getMessageConfig().getString("commands.descriptions.default-user")));
		sender.sendMessage("");
	}

	private void setup_help(CommandSender sender) {
		if (!sender.hasPermission("duel.command.help.setup")) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return;
		}
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 create "
				+ Duel.getMessageConfig().getString("commands.descriptions.setup.create")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 delete "
				+ Duel.getMessageConfig().getString("commands.descriptions.setup.delete")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 setlobby "
				+ Duel.getMessageConfig().getString("commands.descriptions.setup.setlobby")));
		sender.sendMessage("");
		if (APIUtils.isCitizens()) {
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addstatsnpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.addplaynpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addplaynpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.addplaynpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addtrailshopnpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.addplaynpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 addachievementnpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.addachievementnpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removestatsnpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.removestatsnpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removeplaynpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.removeplaynpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removetrailshopnpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.removetrailshopnpc")));
			sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 removeachievmentnpc "
					+ Duel.getMessageConfig().getString("commands.descriptions.setup.removeachievementnpc")));
			sender.sendMessage("");
		}
	}

	private void hologram_help(CommandSender sender) {
		if (!sender.hasPermission("duel.command.help.hologram")) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return;
		}
		sender.sendMessage("");
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 createhologram "
				+ Duel.getMessageConfig().getString("commands.descriptions.hologram.createhologram")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 deletehologram "
				+ Duel.getMessageConfig().getString("commands.descriptions.hologram.deletehologram")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 updatehologram "
				+ Duel.getMessageConfig().getString("commands.descriptions.hologram.updatehologram")));
		sender.sendMessage("");
	}

	private void admin_help(CommandSender sender) {
		if (!sender.hasPermission("duel.command.help.admin")) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return;
		}
		sender.sendMessage("");
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 worldteleport " + Duel.getMessageConfig().getString("commands.descriptions.admin.world-teleport")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 start " + Duel.getMessageConfig().getString("commands.descriptions.admin.start")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 stop " + Duel.getMessageConfig().getString("commands.descriptions.admin.stop")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 draw " + Duel.getMessageConfig().getString("commands.descriptions.admin.draw")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 reload "
				+ Duel.getMessageConfig().getString("commands.descriptions.admin.reload")));
		sender.sendMessage("");
	}

	private void user_help(CommandSender sender) {
		if (!sender.hasPermission("duel.command.help.user")) {
			sender.sendMessage(Duel.getMessageConfig().getString("no-permission"));
			return;
		}
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 join " + Duel.getMessageConfig().getString("commands.descriptions.user.join")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 randomjoin "
				+ Duel.getMessageConfig().getString("commands.descriptions.user.randomjoin")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 play " + Duel.getMessageConfig().getString("commands.descriptions.user.play")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 leave " + Duel.getMessageConfig().getString("commands.descriptions.user.leave")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 top " + Duel.getMessageConfig().getString("commands.descriptions.user.top")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 stats " + Duel.getMessageConfig().getString("commands.descriptions.user.stats")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 list " + Duel.getMessageConfig().getString("commands.descriptions.user.list")));
		sender.sendMessage(ChatUtils.colorTranslate(
				"  &b\u25b6 &7/1vs1 duel " + Duel.getMessageConfig().getString("commands.descriptions.user.duel")));
		sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/1vs1 spectate "
				+ Duel.getMessageConfig().getString("commands.descriptions.user.spectate")));
		sender.sendMessage("");
	}

	private void sendHologramType(CommandSender sender) {
		StringBuilder builder = new StringBuilder();
		for (HologramType type : HologramType.values()) {
			builder.append(type.name().toLowerCase()).append(", ");
		}

		String text = builder.toString();
		sender.sendMessage(Duel.getMessageConfig().getString("errors.invalid-hologram-type", Collections
				.singletonList(new Pair<>("%%type%%", text.substring(0, text.length() - 2)))));
	}
}
