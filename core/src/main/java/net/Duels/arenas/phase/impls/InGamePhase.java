package net.Duels.arenas.phase.impls;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.arenas.phase.Phase;
import net.Duels.config.impl.ItemConfig;
import net.Duels.events.DuelArenaQuitEvent;
import net.Duels.events.DuelArenaReJoinEvent;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;
import net.Duels.utility.KitUtils;
import net.Duels.utility.MathUtils;
import net.Duels.utility.PlayerUtils;

public class InGamePhase extends Phase {

	public InGamePhase(Arena game) {
		super(game, -0L, -0L);
	}

	@Override
	public void start() {
		game.startGame();
	}

	@Override
	public void update() {
		if (game.getPlayers().stream().filter(PlayerObject::isOnline).count() <= 0L) {
			game.reset();
			return;
		}

		if (!game.isOffline()) {
			game.setCount(game.getCount() + 1);
			int remain = game.getMaxCount() - game.getCount();
			if (remain <= 0) {
				game.getPhaseSeries().next();
			}
		} else {
			game.setOfflineCount(game.getOfflineCount() + 1);
			int remain = game.getMaxOfflineCount() - game.getOfflineCount();
			if (remain <= 0) {
				game.getPhaseSeries().next();
			}
		}

		double max = game.isOffline() ? game.getMaxOfflineCount() : game.getMaxCount();
		double sub = game.isOffline() ? game.getOfflineCount() : game.getCount();
		double progress = 1.0 - MathUtils.getPercent(max, sub);
		String title = Duel.getMessageConfig().getString("bossbar.timeleft").replace("%%time%%", game.ingameTime());
		for (PlayerObject playerObject3 : game.getPlayers()) {
			if (!playerObject3.isOffline()) {
				ScoreboardManager.updateScoreboard(playerObject3);
				if (Duel.getMainConfig().isOptionBossbar()) {
					Duel.getBossbar().setTitle(playerObject3.getPlayer(), title);
					Duel.getBossbar().setProgress(playerObject3.getPlayer(), progress);
				}
			}
		}
	}

	@Override
	public void end() {

	}

	@Override
	public void addPlayer(PlayerObject playerObject) {
		if (game.getPlayers().contains(playerObject)) {
			return;
		}

		DuelArenaReJoinEvent event = new DuelArenaReJoinEvent(game, playerObject.getPlayer());
		Duel.getInstance().getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		playerObject.setArena(game);
		Player player = playerObject.getPlayer();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().setHeldItemSlot(0);
		player.updateInventory();
		player.setLevel(0);
		player.setMaxHealth(20.0);
		player.setHealth(player.getMaxHealth());
		player.setFireTicks(0);
		player.setFoodLevel(20);
		player.setFallDistance(0.0f);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGameMode(GameMode.ADVENTURE);
		player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
		player.teleport(game.getWaitingLocation().clone());
		if (Duel.getMainConfig().isOptionBossbar()) {
			Duel.getBossbar().setTitle(player,
					Duel.getMessageConfig().getString("bossbar.waiting").replace("%%map%%", game.getDisplayName()));
			Duel.getBossbar().show(player);
			Duel.getBossbar().setProgress(player, 1.0);
		}

		for (ItemConfig.ConfigItem configItem : Duel.getItemConfig().getIngame_items()) {
			if (configItem.isEnable()) {
				player.getInventory().setItem(configItem.getSlot(), configItem.toItem(playerObject));
			}
		}

		player.updateInventory();

		game.getPlayers().add(playerObject);

		ScoreboardManager.firstScoreboard(playerObject);
	}

	@Override
	public void removePlayer(PlayerObject playerObject) {
		if (!game.getPlayers().contains(playerObject)) {
			return;
		}

		DuelArenaQuitEvent event = new DuelArenaQuitEvent(game, playerObject.getPlayer());
		Player player = playerObject.getPlayer();

		Duel.getInstance().getServer().getPluginManager().callEvent(event);
		if (Duel.getMainConfig().isOptionBossbar()) {
			Duel.getBossbar().hide(player);
		}
		playerObject.setArena(null);
		ScoreboardManager.firstScoreboard(playerObject);

		if (!Duel.getMainConfig().isOptionUseLobbyScoreboard()) {
			playerObject.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}

		game.sendGameMessage(Duel.getMessageConfig().getString("arenas.ingame.player-quited")
				.replace("%%name%%", player.getName()).replace("%%displayname%%", player.getDisplayName()));
		player.setGameMode(GameMode.ADVENTURE);
		player.setMaxHealth(20.0);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setPlayerListName(player.getPlayerListName());
		player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setExp(0.0f);
		player.setLevel(0);
		player.setFallDistance(0.0f);
		player.setFireTicks(0);
		PlayerUtils.teleportToLobby(player);
		KitUtils.joinItem(player, playerObject);
		game.getKills().remove(playerObject.getUniqueId());
		game.getPlayers().remove(playerObject);
		game.endGame();
	}

}
