package net.Duels.arenas.phase.impls;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.Duels.Duel;
import net.Duels.achievements.AchievementType;
import net.Duels.arenas.Arena;
import net.Duels.arenas.phase.Phase;
import net.Duels.events.DuelArenaQuitEvent;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;
import net.Duels.utility.KitUtils;
import net.Duels.utility.PlayerUtils;

public class EndGamePhase extends Phase {

	public EndGamePhase(Arena game) {
		super(game, -0L, -0L);
	}

	@Override
	public void start() {
		game.endGame();
	}

	@Override
	public void update() {

	}

	@Override
	public void end() {

	}

	@Override
	public void addPlayer(PlayerObject playerObject) {
		
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
		playerObject.setKills(playerObject.getKills() + game.getKills().getOrDefault(playerObject.getUniqueId(), 0));
		Duel.getAchievementConfig().checkForReward(playerObject, playerObject.getKills(), AchievementType.KILLS);
		game.getKills().remove(playerObject.getUniqueId());
		game.getPlayers().remove(playerObject);
		game.getSpectators().remove(playerObject);
	}

}
