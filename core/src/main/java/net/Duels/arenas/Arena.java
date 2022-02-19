package net.Duels.arenas;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import lombok.Getter;
import net.Duels.Duel;
import net.Duels.achievements.AchievementType;
import net.Duels.arenas.phase.Phase;
import net.Duels.arenas.phase.PhaseSeries;
import net.Duels.arenas.phase.impls.EndGamePhase;
import net.Duels.arenas.phase.impls.InGamePhase;
import net.Duels.arenas.phase.impls.WaitPhase;
import net.Duels.config.impl.SoundConfig;
import net.Duels.events.DuelArenaSpectatorJoinEvent;
import net.Duels.events.DuelArenaSpectatorQuitEvent;
import net.Duels.events.DuelArenaWinEvent;
import net.Duels.kit.Kit;
import net.Duels.player.PlayerObject;
import net.Duels.scoreboard.ScoreboardManager;
import net.Duels.utility.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Arena {

	private UUID currentUUID = UUID.randomUUID();
	private final LinkedHashMap<UUID, Integer> kills = new LinkedHashMap<>();
	private final List<PlayerObject> players = new LinkedList<>();
	private final List<PlayerObject> spectators = new LinkedList<>();
	private final List<PlayerObject> APlayers = new LinkedList<>();
	private final List<PlayerObject> BPlayers = new LinkedList<>();
	private final List<Item> droppedItem = new LinkedList<>();
	private final List<Block> placed = new LinkedList<>();
	private final List<Block> bucketPlaced = new LinkedList<>();
	private final String name;
	private final String displayName;
	private final Location waitingLocation;
	private final Location spectatorLocation;
	private final Location spawn1;
	private final Location spawn2;
	private final double maxBuildY;
	private boolean isOffline = false;
	private int offlineCount = 0;
	private int maxOfflineCount = 5;
	private boolean counting = false;
	private int maxCount = 6;
	private int count = 0;
	private int maxPlayerSize = 2;
	private ArenaState arenaState;
	private PhaseSeries phaseSeries;

	public Arena(String name, Location waitingLocation, Location spectatorLocation, Location spawn1, Location spawn2, double maxBuildY, String displayName) {
		this.name = name;
		this.waitingLocation = waitingLocation;
		this.spectatorLocation = spectatorLocation;
		this.spawn1 = spawn1;
		this.spawn2 = spawn2;
		this.maxBuildY = maxBuildY;
		this.displayName = displayName;
		this.initPhase();
		this.arenaState = ArenaState.WAIT;
	}

	public void onUpdate() {
		this.spectators.forEach(ScoreboardManager::updateScoreboard);

		Phase phase = this.phaseSeries.getCurrentPhase();
		if (phase != null) {
			phase.update();
		}
	}

	public void addPlayer(PlayerObject playerObject) {
		Phase phase = this.phaseSeries.getCurrentPhase();
		if (phase != null) {
			phase.addPlayer(playerObject);
		}
	}

	public void removePlayer(PlayerObject playerObject) {
		Phase phase = this.phaseSeries.getCurrentPhase();
		if (phase != null) {
			phase.removePlayer(playerObject);
		}
	}

	public void addSpectator(PlayerObject playerObject) {
		if (this.spectators.contains(playerObject)) {
			return;
		}

		DuelArenaSpectatorJoinEvent event = new DuelArenaSpectatorJoinEvent(this, playerObject.getPlayer());
		Duel.getInstance().getServer().getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}

		playerObject.setArena(this);
		playerObject.setSpectator(true);

		Player player = playerObject.getPlayer();
		player.setLevel(0);
		player.setMaxHealth(20.0);
		player.setHealth(player.getMaxHealth());
		player.setFireTicks(0);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);
		player.setFallDistance(0.0f);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setExp(0.0f);
		player.hidePlayer(playerObject.getPlayer());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().setHeldItemSlot(0);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0), true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0), true);
		player.teleport(this.spectatorLocation.clone());
		KitUtils.spectatorItem(player, playerObject);
		if (Duel.getMainConfig().isOptionBossbar()) {
			Duel.getBossbar().setTitle(player,
					Duel.getMessageConfig().getString("bossbar.waiting").replace("%%map%%", this.getDisplayName()));
			Duel.getBossbar().show(player);
			Duel.getBossbar().setProgress(player, 1.0);
		}
		this.spectators.add(playerObject);
		ScoreboardManager.firstScoreboard(playerObject);
		for (PlayerObject po : getPlayers()) {
			po.getPlayer().hidePlayer(player);
		}
	}

	public void removeSpectator(PlayerObject playerObject) {
		if (!this.spectators.contains(playerObject)) {
			return;
		}

		DuelArenaSpectatorQuitEvent event = new DuelArenaSpectatorQuitEvent(this, playerObject.getPlayer());
		Duel.getInstance().getServer().getPluginManager().callEvent(event);

		this.spectators.remove(playerObject);
		playerObject.setArena(null);
		playerObject.setSpectator(false);
		Player player = playerObject.getPlayer();
		player.setLevel(0);
		player.setMaxHealth(20.0);
		player.setHealth(player.getMaxHealth());
		player.setFireTicks(0);
		player.setGameMode(GameMode.ADVENTURE);
		player.setFoodLevel(20);
		player.setFallDistance(0.0f);
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setExp(0.0f);
		player.showPlayer(playerObject.getPlayer());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.getInventory().setHeldItemSlot(0);
		player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
		PlayerUtils.teleportToLobby(player);
		KitUtils.joinItem(player, playerObject);
		if (Duel.getMainConfig().isOptionBossbar()) {
			Duel.getBossbar().hide(player);
		}
		ScoreboardManager.firstScoreboard(playerObject);
		for (PlayerObject po : getPlayers()) {
			po.getPlayer().showPlayer(player);
		}
	}

	public void startGame() {
		int minute = Duel.getMainConfig().getConfig().getInt("arena.time");
		int second = 0;

		this.maxCount = 60 * minute + second;
		this.count = 0;
		this.counting = true;
		this.arenaState = ArenaState.PLAY;

		Collections.shuffle(this.players);
		for (PlayerObject playerObject : this.players) {
			if (this.APlayers.size() < this.BPlayers.size()) {
				this.APlayers.add(playerObject);
			} else {
				this.BPlayers.add(playerObject);
			}
		}

		for (PlayerObject playerObject : this.APlayers) {
			playerObject.getPlayer().teleport(this.spawn1.clone());
		}

		for (PlayerObject playerObject : this.BPlayers) {
			playerObject.getPlayer().teleport(this.spawn2.clone());
		}

		for (PlayerObject playerObject : this.players) {
			Player player = playerObject.getPlayer();

			player.setGameMode(GameMode.SURVIVAL);
			player.setLevel(0);
			player.setMaxHealth(20.0);
			player.setHealth(player.getMaxHealth());
			player.setFireTicks(0);
			player.setFoodLevel(20);
			player.setFlying(false);
			player.setAllowFlight(false);
			player.setExp(0.0f);
			player.setFallDistance(0.0f);
			player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
			player.getInventory().clear();
			player.getInventory().setHeldItemSlot(0);
			player.getInventory().setArmorContents(null);

			if (Duel.getMainConfig().isOptionUseKit()) {
				Kit kit = Duel.getKitManager().getKit(playerObject.getKitSelected());
				if (kit == null) {
					continue;
				}

				KitUtils.giveKit(playerObject.getPlayer(), kit);
			} else {
				KitUtils.giveItem(player);
			}
		}

		for (PlayerObject playerObject3 : this.players) {
			ScoreboardManager.firstScoreboard(playerObject3);
		}

		String line = Duel.getMessageConfig().getString("arenas.ingame.start-message.line");
		List<TextMode> currentTextMode = new LinkedList<TextMode>();
		for (String text : Duel.getMessageConfig().getList("arenas.ingame.start-message.messages")) {
			currentTextMode.clear();
			if (text.contains("{replace_text_mode}")) {
				currentTextMode.add(TextMode.REPLACE);
			}
			if (text.contains("{center_text_mode}")) {
				currentTextMode.add(TextMode.CENTER);
			}
			text = text.replace("{replace_text_mode}", "").replace("{center_text_mode}", "").replace("%%line%%", line)
					.replace("%%game_name%%", this.getName());
			if (currentTextMode.contains(TextMode.CENTER)) {
				if (currentTextMode.contains(TextMode.REPLACE)) {
					this.sendCenterGameMessage(TextUtils.replaceText(text));
				} else {
					this.sendCenterGameMessage(text);
				}
			} else if (currentTextMode.contains(TextMode.REPLACE)) {
				this.sendGameMessage(TextUtils.replaceText(text));
			} else {
				this.sendGameMessage(TextUtils.replaceText(text));
			}
		}

		this.spectators.forEach(ScoreboardManager::firstScoreboard);
	}

	public void endGame() {
		this.arenaState = ArenaState.END;
		this.players.forEach(ScoreboardManager::firstScoreboard);
		this.players.stream().filter(PlayerObject::isOnline)
				.forEach(playerObject -> ActionBar.sendActionBar(playerObject.getPlayer(), ""));
		this.spectators.forEach(ScoreboardManager::firstScoreboard);
		this.spectators.stream().filter(PlayerObject::isOnline)
				.forEach(playerObject -> ActionBar.sendActionBar(playerObject.getPlayer(), ""));
		this.removeDropItem();

		if (this.players.size() <= 0) {
			this.spectators.forEach(this::removeSpectator);
			List<PlayerObject> targetList = new LinkedList<>(this.players);
			for (PlayerObject playerObject : targetList) {
				this.removePlayer(playerObject);
			}
			this.reset();
			return;
		}

		boolean isDraw = this.getPlayers().size() >= this.maxPlayerSize;
		if (!isDraw) {
			PlayerObject spectator = this.getSpectators().isEmpty() ? null : this.getSpectators().get(0);
			PlayerObject winner = (spectator != null)
					? (this.APlayers.contains(spectator) ? (this.BPlayers.isEmpty() ? spectator : this.BPlayers.get(0))
					: (this.APlayers.isEmpty() ? spectator : this.APlayers.get(0)))
					: this.players.get(0);
			if (spectator != null) {
				if (spectator.getBestStreak() < spectator.getWinStreak()) {
					spectator.setBestStreak(spectator.getWinStreak());
					spectator.setWinStreak(0);
				}
				spectator.setLose(spectator.getLose() + 1);
				if (spectator.isOnline()) {
					Titles.sendTitle(spectator.getPlayer(), 5, 20, 5, Duel.getMessageConfig().getString("arenas.ingame.loser-title"), Duel.getMessageConfig().getString("arenas.ingame.loser-subtitle").replace("%%winner%%",
							winner.getPlayer().getName()));
				}
			}
			winner.setWins(winner.getWins() + 1);
			winner.setWinStreak(winner.getWinStreak() + 1);
			winner.setScore(winner.getScore() + 1);
			if (winner.getBestStreak() < winner.getWinStreak()) {
				winner.setBestStreak(winner.getWinStreak());
			}
			Bukkit.getPluginManager().callEvent(new DuelArenaWinEvent(winner.getArena(), winner.getPlayer()));
			Duel.getAchievementConfig().checkForReward(winner, winner.getWins(), AchievementType.WINS);
			Duel.getAchievementConfig().checkForReward(winner, winner.getScore(), AchievementType.SCORE);
			if (Duel.getMainConfig().isOptionUseRewards()) {
				for (String s : Duel.getRewardConfig().getWin_rewards()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							s.replace("%%winner%%", winner.getName()));
				}
			}
			if (this.isOffline) {
				Duel.getAchievementConfig().checkForReward(winner, winner.getKills(), AchievementType.KILLS);
				winner.setKills(winner.getKills() + 1);
			}
			if (winner.isOnline()) {
				Titles.sendTitle(winner.getPlayer(), 5, 20, 5, Duel.getMessageConfig().getString("arenas.ingame.winner-title"),
						Duel.getMessageConfig().getString("arenas.ingame.winner-subtitle").replace("%%winner%%",
								winner.getPlayer().getName()));
			}

			String line = Duel.getMessageConfig().getString("arenas.ingame.end-message.line");
			List<TextMode> currentTextMode = new LinkedList<>();
			for (String text : Duel.getMessageConfig().getList("arenas.ingame.end-message.messages")) {
				currentTextMode.clear();
				if (text.contains("{replace_text_mode}")) {
					currentTextMode.add(TextMode.REPLACE);
				}
				if (text.contains("{center_text_mode}")) {
					currentTextMode.add(TextMode.CENTER);
				}
				text = text.replace("{replace_text_mode}", "").replace("{center_text_mode}", "")
						.replace("%%line%%", line).replace("%%game_name%%", this.getName())
						.replace("%%winner%%", winner.getName());
				if (spectator != null) {
					text = text.replace("%%loser%%", spectator.getName());
				}
				if (currentTextMode.contains(TextMode.CENTER)) {
					if (currentTextMode.contains(TextMode.REPLACE)) {
						this.sendCenterGameMessage(TextUtils.replaceText(text));
					} else {
						this.sendCenterGameMessage(text);
					}
				} else if (currentTextMode.contains(TextMode.REPLACE)) {
					this.sendGameMessage(TextUtils.replaceText(text));
				} else {
					this.sendGameMessage(TextUtils.replaceText(text));
				}
			}

			new BukkitRunnable() {
				public void run() {
					if (winner.isOnline()) {
						winner.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("arenas.ingame.winner-message"));
					}
					if (spectator != null && spectator.isOnline()) {
						spectator.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("arenas.ingame.loser-message"));
					}
				}
			}.runTaskLater(Duel.getInstance(), 40L);
		} else {
			this.getPlayers().forEach(playerObject -> {
				if (playerObject.isOnline()) {
					Titles.sendTitle(playerObject.getPlayer(), 5, 20, 5, Duel.getMessageConfig().getString("arenas.ingame.draw-title"),
							Duel.getMessageConfig().getString("arenas.ingame.draw-subtitle"));
				}
			});
		}

		new BukkitRunnable() {
			public void run() {
				Arena.this.spectators.forEach(playerObject -> Arena.this.removeSpectator(playerObject));
				List<PlayerObject> targetList = new LinkedList<>(Arena.this.players);
				for (PlayerObject playerObject2 : targetList) {
					Arena.this.removePlayer(playerObject2);
				}
				Arena.this.reset();
			}
		}.runTaskLater(Duel.getInstance(), 100L);
	}

	public void onDeath(PlayerObject playerObject) {
		PlayerObject killer = null;
		Player player = playerObject.getPlayer();
		if (player.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) player.getLastDamageCause();
			if (event.getDamager() instanceof Player) {
				Player damagePlayer = (Player) event.getDamager();
				killer = Duel.getPlayerController().getPlayer(damagePlayer.getUniqueId());
			}
		}
		if (killer != null) {
			this.kills.put(killer.getUniqueId(), this.kills.getOrDefault(killer.getUniqueId(), 0) + 1);
		}
		playerObject.setBestStreak(0);
		playerObject.setDeaths(playerObject.getDeaths() + 1);
		this.getPlayers().remove(playerObject);
		this.addSpectator(playerObject);
		this.phaseSeries.next();
	}

	public void playSound(String key) {
		SoundConfig.ConfigSound sound = Duel.getSoundConfig().getSound(key);
		if (sound == null)
			return;
		if (!sound.isValid())
			return;
		Sound bukkitSound = sound.getBukkitSound();
		if (bukkitSound != null)
			this.players.stream().filter(PlayerObject::isOnline)
					.forEach(playerObject -> playerObject.playSound(bukkitSound, sound.getPitch(), sound.getVolume()));
	}

	public void sendGameMessage(String text) {
		this.players.stream().filter(PlayerObject::isOnline)
				.forEach(playerObject -> playerObject.getPlayer().sendMessage(ChatUtils.colorTranslate(text)));
	}

	public void sendGameTitle(String title, String subtitle) {
		this.players.stream().filter(PlayerObject::isOnline)
				.forEach(playerObject -> Titles.sendTitle(playerObject.getPlayer(), 5, 20, 5,
						ChatUtils.colorTranslate(title), ChatUtils.colorTranslate(subtitle)));
	}

	public void sendCenterGameMessage(String text) {
		this.players.stream().filter(PlayerObject::isOnline)
				.forEach(playerObject -> ChatUtils.sendCenteredMessage(playerObject.getPlayer(), text));
	}

	public boolean isFull() {
		return this.players.size() >= this.maxPlayerSize;
	}

	public void initPhase() {
		this.phaseSeries = new PhaseSeries();
		this.phaseSeries.add(new WaitPhase(this));
		this.phaseSeries.add(new InGamePhase(this));
		this.phaseSeries.add(new EndGamePhase(this));
		this.phaseSeries.start();
	}

	public void reset() {
		this.initPhase();
		this.replaceBlock();
		this.removeDropItem();

		this.currentUUID = UUID.randomUUID();
		this.arenaState = ArenaState.RESET;
		this.maxPlayerSize = 2;
		this.players.clear();
		this.spectators.clear();
		this.APlayers.clear();
		this.BPlayers.clear();
		this.counting = false;
		this.maxCount = 6;
		this.count = 0;
		this.placed.clear();
		this.bucketPlaced.clear();
		this.droppedItem.clear();
		this.kills.clear();
		this.arenaState = ArenaState.WAIT;
	}

	public void shutdown() {
		this.replaceBlock();
		this.removeDropItem();
	}

	private void replaceBlock() {
		for (Block block : this.placed) {
			block.setType(Material.AIR);
		}
		for (Block block : this.bucketPlaced) {
			List<Block> trackBlocks = new LinkedList<>();
			BucketUtils.trackWater(block, trackBlocks);
			BucketUtils.trackLava(block, trackBlocks);
			for (Block targetBlock : trackBlocks) {
				targetBlock.setType(Material.AIR);
			}
		}
	}

	private void removeDropItem() {
		for (Item item : this.droppedItem) {
			if (item != null && !item.isDead()) {
				item.remove();
			}
		}
	}

	public List<PlayerObject> getMyTeam(PlayerObject playerObject) {
		if (this.getAPlayers().contains(playerObject)) {
			return this.getAPlayers();
		}
		return this.getBPlayers();
	}

	public List<PlayerObject> getOtherTeam(PlayerObject playerObject) {
		if (this.getAPlayers().contains(playerObject)) {
			return this.getBPlayers();
		}
		return this.getAPlayers();
	}

	public PlayerObject getEnemy(PlayerObject playerObject) {
		if (this.APlayers.contains(playerObject)) {
			if (this.BPlayers.size() == 0) {
				return null;
			}
			return this.BPlayers.get(0);
		} else {
			if (this.APlayers.size() == 0) {
				return null;
			}
			return this.APlayers.get(0);
		}
	}

	public String getStateToText() {
		if (this.arenaState == ArenaState.WAIT) {
			return Duel.getMessageConfig().getString("arenas.status.wait");
		}
		if (this.arenaState == ArenaState.PLAY) {
			return Duel.getMessageConfig().getString("arenas.status.play");
		}
		if (this.arenaState == ArenaState.END) {
			return Duel.getMessageConfig().getString("arenas.status.end");
		}
		if (this.arenaState == ArenaState.RESET) {
			return Duel.getMessageConfig().getString("arenas.status.reset");
		}
		return Duel.getMessageConfig().getString("arenas.status.invalid");
	}

	public void arenaRandomTeleportPlayer(PlayerObject playerObject) {
		LinkedList<Player> list = new LinkedList<>();
		for (Player player2 : Bukkit.getOnlinePlayers()) {
			if (this.players.contains(player2)) {
				list.add(player2);
			}
		}
		if (list.isEmpty()) {
			playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.no-teleport-player"));
			return;
		}
		Player player3 = list.get(new Random().nextInt(list.size()));
		playerObject.getPlayer().sendMessage(Duel.getMessageConfig().getString("arenas.random-teleport-player").replace("%%player%%", player3.getDisplayName()));
		playerObject.getPlayer().teleport(player3);
	}

	public String ingameTime() {
		int second = this.isOffline ? (this.maxOfflineCount - this.offlineCount) : (this.maxCount - this.count);
		int minute = second / 60;
		second -= minute * 60;
		return "0" + minute + ":" + ((second <= 9) ? "0" : "") + second;
	}

	public UUID getCurrentUUID() {
		return this.currentUUID;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		String map = Duel.getArenaManager().getConfig().getString("arenas." + name + ".name");
		if (Duel.getArenaManager().getConfig() == null) {
			return this.displayName;
		}
		if (Duel.getArenaManager().getConfig().getString("arenas." + name + ".name") == null) {
			return this.displayName;
		}
		if (map == null) {
			return "null";
		}
		return map;
	}

	public Location getWaitingLocation() {
		return this.waitingLocation;
	}

	public Location getSpectatorLocation() {
		return this.spectatorLocation;
	}

	public Location getSpawn1() {
		return this.spawn1;
	}

	public Location getSpawn2() {
		return this.spawn2;
	}

	public double getMaxBuildY() {
		return this.maxBuildY;
	}

	public HashMap<UUID, Integer> getKills() {
		return this.kills;
	}

	public List<PlayerObject> getPlayers() {
		return this.players;
	}

	public List<PlayerObject> getSpectators() {
		return this.spectators;
	}

	public List<PlayerObject> getAPlayers() {
		return this.APlayers;
	}

	public List<PlayerObject> getBPlayers() {
		return this.BPlayers;
	}

	public List<Item> getDroppedItem() {
		return this.droppedItem;
	}

	public List<Block> getPlaced() {
		return this.placed;
	}

	public List<Block> getBucketPlaced() {
		return this.bucketPlaced;
	}

	public void setOffline(boolean isOffline) {
		this.isOffline = isOffline;
	}

	public boolean isOffline() {
		return this.isOffline;
	}

	public void setOfflineCount(int offlineCount) {
		this.offlineCount = offlineCount;
	}

	public int getOfflineCount() {
		return this.offlineCount;
	}

	public void setMaxOfflineCount(int maxOfflineCount) {
		this.maxOfflineCount = maxOfflineCount;
	}

	public int getMaxOfflineCount() {
		return this.maxOfflineCount;
	}

	public void setCounting(boolean counting) {
		this.counting = counting;
	}

	public boolean isCounting() {
		return this.counting;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public int getMaxCount() {
		return this.maxCount;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return this.count;
	}

	public void setMaxPlayerSize(int maxPlayerSize) {
		this.maxPlayerSize = maxPlayerSize;
	}

	public int getMaxPlayerSize() {
		return this.maxPlayerSize;
	}

	public ArenaState getArenaState() {
		return this.arenaState;
	}

	public PhaseSeries getPhaseSeries() {
		return phaseSeries;
	}

	public String getArenaStatus() {
		return "§e" + this.name + "§8: " + this.getMaxPlayerSize() + " §8| " + getStateToText();
	}

	public enum ArenaState {
		RESET(ChatColor.BLACK, XMaterial.BLACK_TERRACOTTA, (byte) 15), WAIT(ChatColor.GREEN, XMaterial.LIME_TERRACOTTA, (byte) 5),
		PLAY(ChatColor.RED, XMaterial.RED_TERRACOTTA, (byte) 14), END(ChatColor.BLUE, XMaterial.CYAN_TERRACOTTA, (byte) 9);

		@Getter
		private final ChatColor chatColor;

		@Getter
		private final XMaterial material;

		@Getter
		private final byte color;

		private ArenaState(ChatColor chatColor, XMaterial material, byte color) {
			this.chatColor = chatColor;
			this.material = material;
			this.color = color;
		}
	}

	public enum TextMode {
		CENTER, REPLACE;
	}
}
