package net.Duels.utility;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.kit.Kit;
import net.Duels.menus.*;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EventUtils {

	public static void onItemType(PlayerObject playerObject, ItemStack itemStack, String type, Action action) {
		if (type.equalsIgnoreCase("play")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			new PlayGUI(playerObject, 1);
			playerObject.playSound("sounds.action.open-play-menu");
		} else if (type.equalsIgnoreCase("stats")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			new StatsGUI(playerObject);
			playerObject.playSound("sounds.action.open-stat-menu");
		} else if (type.equalsIgnoreCase("shop")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("commands.still-in-development"));
			playerObject.playSound("sounds.errors.open-shop");
		} else if (type.equalsIgnoreCase("close")) {
			if (playerObject.getPlayer().getOpenInventory() != null) {
				playerObject.getPlayer().closeInventory();
			}
		} else if (type.equalsIgnoreCase("bungee_leave")) {
			if (Duel.getMainConfig().isOptionBungee()) {
				ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
				dataOutput.writeUTF("Connect");
				dataOutput.writeUTF(Duel.getMainConfig().getBungeeServer());
				playerObject.getPlayer().sendPluginMessage(JavaPlugin.getPlugin(Duel.class), "BungeeCord",
						dataOutput.toByteArray());
			}
		} else if (type.equalsIgnoreCase("SET_WAITING_LOCATION")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			playerObject.getSetupData().setWaitingLocation(playerObject.getPlayer().getLocation());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.parts.wait-location"));
			playerObject.playSound("sounds.action.arena-set-wait-room");
		} else if (type.equalsIgnoreCase("SET_SPEC_LOCATION")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			playerObject.getSetupData().setSpectatorLocation(playerObject.getPlayer().getLocation());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.parts.spec-location"));
			playerObject.playSound("sounds.action.arena-set-spec-room");
		} else if (type.equalsIgnoreCase("SET_MAX_BUILD_Y")) {
	            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
	                return;
	            }
	            playerObject.getSetupData().setMaxBuildY(playerObject.getPlayer().getLocation().getY());
	            playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.parts.set-max-build-y-location"));
	            playerObject.playSound("sounds.action.arena-set-max-build-y");
		} else if (type.equalsIgnoreCase("SET_A_TEAM_LOCATION")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			playerObject.getSetupData().setSpawn1(playerObject.getPlayer().getLocation());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.parts.a-team-location"));
			playerObject.playSound("sounds.action.arena-set-a-team");
		} else if (type.equalsIgnoreCase("SET_B_TEAM_LOCATION")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			playerObject.getSetupData().setSpawn2(playerObject.getPlayer().getLocation());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.parts.b-team-location"));
			playerObject.playSound("sounds.action.arena-set-b-team");
		} else if (type.equalsIgnoreCase("ARENA_SAVE")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (!playerObject.getSetupData().compile()) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.arena-complete"));
				playerObject.playSound("sounds.errors.arena-complete");
				return;
			}
			Duel.getArenaManager().createArena(playerObject.getSetupData().getName(),
					playerObject.getSetupData().getWaitingLocation(),
					playerObject.getSetupData().getSpectatorLocation(), playerObject.getSetupData().getSpawn1(),
					playerObject.getSetupData().getSpawn2(), playerObject.getSetupData().getMaxBuildY());
			playerObject.setSetupData(null);
			KitUtils.joinItem(playerObject.getPlayer(), playerObject);
			ScoreboardManager.firstScoreboard(playerObject);
			PlayerUtils.teleportToLobby(playerObject.getPlayer());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.arena-created"));
			playerObject.playSound("sounds.action.arena-created");
		} else if (type.equalsIgnoreCase("join_to_arena")) {
			playerObject.getPlayer().closeInventory();
			if (!Duel.getNms().isCustomData(itemStack, "arena")) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.invalid-join-item"));
				playerObject.playSound("sounds.errors.invalid-item");
				return;
			}
			String arenaName = Duel.getNms().getCustomData(itemStack, "arena");
			if (!Duel.getArenaManager().contains(arenaName)) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.arena-found"));
				playerObject.playSound("sounds.errors.arena-found");
				return;
			}
			Arena arena3 = Duel.getArenaManager().getArena(arenaName);
			if (arena3.getArenaState() != Arena.ArenaState.WAIT) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.arena-started"));
				playerObject.playSound("sounds.errors.arena-started");
				return;
			}
			if (arena3.isFull()) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.arena-is-full"));
				playerObject.playSound("sounds.errors.arena-is-full");
				return;
			}
			arena3.addPlayer(playerObject);
		} else if (type.equalsIgnoreCase("random_join")) {
			playerObject.getPlayer().closeInventory();
			List<Arena> arenas = new LinkedList<Arena>(Duel.getArenaManager().getArenas());
			if (arenas.isEmpty()) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.no-games"));
				playerObject.playSound("sounds.errors.no-game");
				return;
			}
			List<Arena> availableGames = new LinkedList<Arena>();
			for (Arena arena4 : arenas) {
				if (arena4.getArenaState() == Arena.ArenaState.WAIT && !arena4.isFull()) {
					availableGames.add(arena4);
				}
			}
			if (availableGames.isEmpty()) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.no-games"));
				playerObject.playSound("sounds.errors.no-available-games");
				return;
			}
			Collections.shuffle(availableGames);
			Arena arena4 = availableGames.stream()
					.min((arena1, arena2) -> arena2.getPlayers().size() - arena1.getPlayers().size()).get();
			arena4.addPlayer(playerObject);
			playerObject.playSound("sounds.action.random-join");
		} else if (type.equalsIgnoreCase("LEAVE")) {
			if (!playerObject.inArena()) {
				return;
			}
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			Arena arena5 = playerObject.getArena();
			if (arena5.getArenaState() == Arena.ArenaState.WAIT || playerObject.isSpectator()) {
				if (playerObject.getQuitTask() != null) {
					Duel.getInstance().getServer().getScheduler().cancelTask(playerObject.getQuitTask().getTaskId());
					playerObject.setQuitTask(null);
					playerObject.getPlayer()
							.sendMessage(Duel.getMessageConfig().getString("arenas.ingame.leave-cancel"));
					playerObject.playSound("sounds.ingame.leave.remove-queue");
					return;
				}
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.ingame.leave-started"));
				playerObject.playSound("sounds.ingame.leave.queue");
				BukkitTask task = new BukkitRunnable() {
					public void run() {
						Player player = playerObject.getPlayer();
						if (player == null) {
							return;
						}
						if (!playerObject.isSpectator() && arena5.getArenaState() != Arena.ArenaState.WAIT) {
							return;
						}
						if (playerObject.isSpectator()) {
							arena5.removeSpectator(playerObject);
						} else {
							arena5.removePlayer(playerObject);
						}
						playerObject.setQuitTask(null);
						playerObject.playSound("sounds.ingame.leave.quited");
					}
				}.runTaskLater(Duel.getInstance(), 60L);
				playerObject.setQuitTask(task);
			}
		} else if (type.equalsIgnoreCase("golden_head")) {
			Player player = playerObject.getPlayer();
			if (!playerObject.inArena()) {
				return;
			}
			long lastTime = System.currentTimeMillis() - playerObject.getLastGoldenApple();
			int lastToSecond = 3 - (int) lastTime / 1000;
			if (lastTime <= 3000L) {
				playerObject.getPlayer()
						.sendMessage(Duel.getMessageConfig().getString("arenas.ingame.golden-head-cooldown")
								.replace("%%remain%%", String.valueOf(lastToSecond)));
				playerObject.playSound("sounds.ingame.golden-head-cooldown");
				return;
			}
			playerObject.setLastGoldenApple(System.currentTimeMillis());
			int amount = player.getItemInHand().getAmount();
			if (amount > 1) {
				player.getItemInHand().setAmount(amount - 1);
			} else {
				player.setItemInHand(new ItemStack(Material.AIR));
			}
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0), true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1), true);
			playerObject.playSound("sounds.ingame.golden-head-eat");
		} else if (type.equalsIgnoreCase("PLAY_GUI_NEXT_PAGE")) {
			int page = Integer.parseInt(Duel.getNms().getCustomData(itemStack, "page")) + 1;
			new PlayGUI(playerObject, page);
			playerObject.playSound("sounds.gui.next-page");
		} else if (type.equalsIgnoreCase("PLAY_GUI_BACK_PAGE")) {
			int page = Integer.parseInt(Duel.getNms().getCustomData(itemStack, "page")) - 1;
			new PlayGUI(playerObject, page);
			playerObject.playSound("sounds.gui.back-page");
		} else if (type.equalsIgnoreCase("ACHIEVEMENT_OPEN")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			new AchievementGUI(playerObject).createAchievementMenu(1);
			playerObject.playSound("sounds.action.open-achievement-menu");
		} else if (type.equalsIgnoreCase("ACHIEVEMENT_NEXT_PAGE")) {
			int page = Integer.parseInt(Duel.getNms().getCustomData(itemStack, "page"));
			new AchievementGUI(playerObject).createAchievementMenu(page);
			playerObject.playSound("sounds.gui.next-page");
		} else if (type.equalsIgnoreCase("ACHIEVEMENT_PREVIOUS_PAGE")) {
			int page = Integer.parseInt(Duel.getNms().getCustomData(itemStack, "page"));
			new AchievementGUI(playerObject).createAchievementMenu(page);
			playerObject.playSound("sounds.gui.back-page");
		} else if (type.equalsIgnoreCase("ACHIEVEMENT_CLOSE")) {
			playerObject.getPlayer().closeInventory();
		} else if (type.equalsIgnoreCase("TELEPORTER")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				Arena arena = playerObject.getArena();
				arena.arenaRandomTeleportPlayer(playerObject);
			}
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			new TeleporterGUI(playerObject).createTeleporter();
			}
		} else if (type.equalsIgnoreCase("SPECTATOR_SETTINGS")) {
			if (!playerObject.inArena()) {
				return;
			}
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			new SpectatorSettingsGUI(playerObject).createSpectatorSettings();
			playerObject.playSound("sounds.action.open-spectatorsettings-menu");
		} else if (type.equalsIgnoreCase("NO_SPEED")) {
			playerObject.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			playerObject.getPlayer().closeInventory();
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("guis.spectatorsettings.no-speed"));
		} else if (type.equalsIgnoreCase("SPEED_I")) {
			playerObject.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			playerObject.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0, true));
			playerObject.getPlayer().closeInventory();
			playerObject.getPlayer().sendMessage(
					Duel.getMessageConfig().getString("guis.spectatorsettings.speed").replace("%%speed%%", "I"));
		} else if (type.equalsIgnoreCase("SPEED_II")) {
			playerObject.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			playerObject.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 1, true));
			playerObject.getPlayer().closeInventory();
			playerObject.getPlayer().sendMessage(
					Duel.getMessageConfig().getString("guis.spectatorsettings.speed").replace("%%speed%%", "II"));
		} else if (type.equalsIgnoreCase("SPEED_III")) {
			playerObject.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			playerObject.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 2, true));
			playerObject.getPlayer().closeInventory();
			playerObject.getPlayer().sendMessage(
					Duel.getMessageConfig().getString("guis.spectatorsettings.speed").replace("%%speed%%", "III"));
		} else if (type.equalsIgnoreCase("SPEED_IV")) {
			playerObject.getPlayer().removePotionEffect(PotionEffectType.SPEED);
			playerObject.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 3, true));
			playerObject.getPlayer().closeInventory();
			playerObject.getPlayer().sendMessage(
					Duel.getMessageConfig().getString("guis.spectatorsettings.speed").replace("%%speed%%", "IV"));
		} else if (type.equalsIgnoreCase("KIT_SELECTOR")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			new KitSelectorGUI(playerObject).createKitSelectorMenu();
		} else if (type.equalsIgnoreCase("TELEPORT_ITEM")) {
			Player player = Duel.getInstance().getServer().getPlayer(Duel.getNms().getCustomData(itemStack, "target"));
			if (player == null) {
				playerObject.getPlayer()
						.sendMessage(Duel.getMessageConfig().getString("errors.teleporter-not-online-player"));
				return;
			}
			playerObject.getPlayer().teleport(player);
		} else if (type.equalsIgnoreCase("PLAYER_VISIBLE_ENABLE")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			Player player = playerObject.getPlayer();
			long lastTime = System.currentTimeMillis() - playerObject.getVisible();
			int lastToSecond = 3 - (int) lastTime / 1000;
			if (lastTime <= 3000L) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("player-visible.cooldown")
						.replace("%%cooldown%%", String.valueOf(lastToSecond)));
				return;
			}
			playerObject.setPlayerVisible(false);
			playerObject.setVisible(System.currentTimeMillis());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("player-visible.disable"));
			for (PlayerObject po : Duel.getPlayerController().getAll()) {
				Player p = playerObject.getPlayer();
				p.hidePlayer(po.getPlayer());
			}
			if (playerObject.isPlayerVisible()) {
				KitUtils.playerShowItem(player, playerObject);
			} else {
				KitUtils.playerHideItem(player, playerObject);
			}
		} else if (type.equalsIgnoreCase("PLAYER_VISIBLE_DISABLE")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			Player player = playerObject.getPlayer();
			long lastTime = System.currentTimeMillis() - playerObject.getVisible();
			int lastToSecond = 3 - (int) lastTime / 1000;
			if (lastTime <= 3000L) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("player-visible.cooldown")
						.replace("%%cooldown%%", String.valueOf(lastToSecond)));
				return;
			}
			playerObject.setPlayerVisible(true);
			playerObject.setVisible(System.currentTimeMillis());
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("player-visible.enable"));
			for (PlayerObject po : Duel.getPlayerController().getAll()) {
				Player p = playerObject.getPlayer();
				p.showPlayer(po.getPlayer());
			}
			if (playerObject.isPlayerVisible()) {
				KitUtils.playerShowItem(player, playerObject);
			} else {
				KitUtils.playerHideItem(player, playerObject);
			}
		} else if (type.equalsIgnoreCase("KIT_SELECT")) {
			String kitName = Duel.getNms().getCustomData(itemStack, "kit");
			Kit kit = Duel.getKitManager().getKit(kitName);
			if (kit == null) {
				playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("kit.error"));
				return;
			}
			playerObject.setKitSelected(kitName);
			playerObject.getPlayer()
					.sendMessage(Duel.getMessageConfig().getString("kit.select").replace("%%kit%%", kitName));
			playerObject.playSound("sounds.action.kit-select");
			if (playerObject.getPlayer().getOpenInventory() != null) {
				playerObject.getPlayer().closeInventory();
			}
		} else if (type.equalsIgnoreCase("PLAY_AGAIN")) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				return;
			}
			if (playerObject.isSpectator()) {
				playerObject.getArena().removeSpectator(playerObject);
				List<Arena> arenas = new LinkedList<>(Duel.getArenaManager().getArenas());
				if (arenas.isEmpty()) {
					playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.no-games"));
					playerObject.playSound("sounds.errors.no-game");
					return;
				}
				List<Arena> availableGames = new LinkedList<>();
				for (Arena arena4 : arenas) {
					if (arena4.getArenaState() == Arena.ArenaState.WAIT && !arena4.isFull()) {
						availableGames.add(arena4);
					}
				}
				if (availableGames.isEmpty()) {
					playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("errors.no-games"));
					playerObject.playSound("sounds.errors.no-available-games");
					return;
				}
				Collections.shuffle(availableGames);
				Arena arena4 = availableGames.stream()
						.min((arena1, arena2) -> arena2.getPlayers().size() - arena1.getPlayers().size()).get();
				arena4.addPlayer(playerObject);
				playerObject.playSound("sounds.action.random-join");
			}
		}
	}
}
