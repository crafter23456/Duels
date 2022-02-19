package net.Duels.arenas.phase.impls;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.arenas.phase.Phase;
import net.Duels.config.impl.ItemConfig;
import net.Duels.events.DuelArenaCountingCancelEvent;
import net.Duels.events.DuelArenaCountingEvent;
import net.Duels.events.DuelArenaCountingStartEvent;
import net.Duels.events.DuelArenaJoinEvent;
import net.Duels.events.DuelArenaQuitEvent;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;
import net.Duels.utility.KitUtils;
import net.Duels.utility.PlayerUtils;

public class WaitPhase extends Phase {

	public WaitPhase(Arena game) {
		super(game, -0L, -0L);
	}

	@Override
	public void start() {

	}

	@Override
	public void update() {
		if (game.isFull() && !game.isCounting()) {
			DuelArenaCountingStartEvent event = new DuelArenaCountingStartEvent(game);
			Duel.getInstance().getServer().getPluginManager().callEvent(event);
			
			game.setCounting(true);
			game.setCount(0);
			return;
		}

		if (!game.isFull() && game.isCounting()) {
			DuelArenaCountingCancelEvent event = new DuelArenaCountingCancelEvent(game);
			Duel.getInstance().getServer().getPluginManager().callEvent(event);
			
			game.setCounting(false);
			game.setCount(0);
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.cancel-title"),
					Duel.getMessageConfig().getString("arenas.counting.cancel-title"));
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.cancel-message"));
			return;
		}

		if (!game.isCounting()) {
			return;
		}

		game.setCount(game.getCount() + 1);
		int remain = game.getMaxCount() - game.getCount();
		
		DuelArenaCountingEvent event = new DuelArenaCountingEvent(game, remain);
		Duel.getInstance().getServer().getPluginManager().callEvent(event);
		
		if (remain == 0) {
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.start-title"),
					Duel.getMessageConfig().getString("arenas.counting.start-subtitle"));
			game.playSound("sounds.ingame.counting.start");
			game.getPhaseSeries().next();
		} else if (remain % 30 == 0) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-division-30")
					.replace("%%remain%%", String.valueOf(remain)));
			game.playSound("sounds.ingame.counting.remaining-division-30");
		} else if (remain % 15 == 0) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-division-15")
					.replace("%%remain%%", String.valueOf(remain)));
			game.playSound("sounds.ingame.counting.remaining-division-15");
		} else if (remain == 5) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-5").replace("%%remain%%",
					String.valueOf(remain)));
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.remaining-5-title"),
					Duel.getMessageConfig().getString("arenas.counting.remaining-5-subtitle"));
			game.playSound("sounds.ingame.counting.remaining-5");
		} else if (remain == 4) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-4").replace("%%remain%%",
					String.valueOf(remain)));
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.remaining-4-title"),
					Duel.getMessageConfig().getString("arenas.counting.remaining-4-subtitle"));
			game.playSound("sounds.ingame.counting.remaining-4");
		} else if (remain == 3) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-3").replace("%%remain%%",
					String.valueOf(remain)));
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.remaining-3-title"),
					Duel.getMessageConfig().getString("arenas.counting.remaining-3-subtitle"));
			game.playSound("sounds.ingame.counting.remaining-3");
		} else if (remain == 2) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-2").replace("%%remain%%",
					String.valueOf(remain)));
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.remaining-2-title"),
					Duel.getMessageConfig().getString("arenas.counting.remaining-2-subtitle"));
			game.playSound("sounds.ingame.counting.remaining-2");
		} else if (remain == 1) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.remaining-1").replace("%%remain%%",
					String.valueOf(remain)));
			game.sendGameTitle(Duel.getMessageConfig().getString("arenas.counting.remaining-1-title"),
					Duel.getMessageConfig().getString("arenas.counting.remaining-1-subtitle"));
			game.playSound("sounds.ingame.counting.remaining-1");
		} else if (remain == -5) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.start-detect-abnormality"));
			game.endGame();
		} else if (remain == -10) {
			game.sendGameMessage(Duel.getMessageConfig().getString("arenas.counting.fail-start"));
			game.getPlayers().forEach(playerObject -> playerObject.getPlayer()
					.kickPlayer(Duel.getMessageConfig().getString("arenas.counting.fail-start-message")));
			game.reset();
		}

		for (PlayerObject playerObject : game.getPlayers()) {
			ScoreboardManager.updateScoreboard(playerObject);
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

		DuelArenaJoinEvent event = new DuelArenaJoinEvent(game, playerObject.getPlayer());
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

		game.getKills().put(playerObject.getUniqueId(), 0);
		game.getPlayers().add(playerObject);

		ScoreboardManager.firstScoreboard(playerObject);

		game.sendGameMessage(Duel.getMessageConfig().getString("arenas.ingame.joined")
				.replace("%%name%%", player.getName()).replace("%%displayname%%", player.getDisplayName())
				.replace("%%current%%", String.valueOf(game.getPlayers().size()))
				.replace("%%max%%", String.valueOf(game.getMaxPlayerSize())));

		if (game.isFull()) {
			game.onUpdate();
		}
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
		game.sendGameMessage(Duel.getMessageConfig().getString("arenas.ingame.quited")
				.replace("%%name%%", player.getName()).replace("%%displayname%%", player.getDisplayName())
				.replace("%%current%%", String.valueOf(game.getPlayers().size()))
				.replace("%%max%%", String.valueOf(game.getMaxPlayerSize())));
	}

}
