package net.Duels;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.Duels.api.CitizenDataStore;
import net.Duels.api.MVdWPlaceholderAPI;
import net.Duels.api.PlaceholderAPI;
import net.Duels.arenas.ArenaManager;
import net.Duels.commands.DuelCommand;
import net.Duels.commands.KitCommand;
import net.Duels.commands.MapCommand;
import net.Duels.config.impl.*;
import net.Duels.controllers.HologramController;
import net.Duels.controllers.NPCController;
import net.Duels.controllers.PlayerController;
import net.Duels.datastorage.DataStorage;
import net.Duels.kit.KitManager;
import net.Duels.listeners.CitizensListener;
import net.Duels.listeners.PlayerListener;
import net.Duels.nms.BossBar;
import net.Duels.nms.NMS;
import net.Duels.player.PlayerObject;
import net.Duels.runnables.*;
import net.Duels.utility.APIUtils;
import net.Duels.utility.KitUtils;
import net.Duels.utility.NMSUtils;
import net.Duels.utility.PlayerUtils;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.potion.PotionEffect;
import java.util.Objects;

public class Duel extends JavaPlugin implements PluginMessageListener {

	@Getter
	private static Duel instance;
	@Getter
	private static MainConfig mainConfig;
	@Getter
	private static MessageConfig messageConfig;
	@Getter
	private static SoundConfig soundConfig;
	@Getter
	private static ItemConfig itemConfig;
	@Getter
	private static ScoreboardConfig scoreboardConfig;
	@Getter
	private static RewardConfig rewardConfig;
	@Getter
	private static ArenaManager arenaManager;
	@Getter
	private static SignConfig signConfig;
	@Getter
	private static AchievementConfig achievementConfig;
	@Getter
	private static NPCConfig npcConfig;
	@Getter
	private static HologramConfig hologramConfig;
	@Getter
	private static DataStorage dataStorage;
	@Getter
	private static PlayerController playerController;
	@Getter
	private static HologramController hologramController;
	@Getter
	private static NPCController npcController;
	@Getter
	private static NMS nms;
	@Getter
	private static BossBar bossbar;
	@Getter
	private static KitManager kitManager;
	@Getter
	private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Duels" + ChatColor.DARK_GRAY + "]"
			+ ChatColor.RESET;
	@Getter
	private static boolean setup = false;

	public void onEnable() {
		instance = this;

		long startTime = System.currentTimeMillis();
		if (!this.loadNMS() || !this.loadBossBarNMS()) {
			return;
		}

		mainConfig = new MainConfig(this);
		mainConfig.load();
		messageConfig = new MessageConfig(this);
		messageConfig.load();
		soundConfig = new SoundConfig(this);
		soundConfig.load();
		itemConfig = new ItemConfig(this);
		itemConfig.load();
		scoreboardConfig = new ScoreboardConfig(this);
		scoreboardConfig.load();
		dataStorage = new DataStorage(this);
		arenaManager = new ArenaManager(this);
		arenaManager.load();
		signConfig = new SignConfig(this);
		signConfig.load();
		rewardConfig = new RewardConfig(this);
		rewardConfig.load();
		achievementConfig = new AchievementConfig(this);
		achievementConfig.load();
		hologramConfig = new HologramConfig(this);
		hologramConfig.load();
		playerController = new PlayerController();
		hologramController = new HologramController();

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		new RunnableUpdateScoreboard().runTaskTimer(this, 20L, 20L);
		new RunnableGameUpdate().runTaskTimer(this, 20L, 20L);
		new RunnableSignUpdate().runTaskTimer(this, 10L, 10L);
		new RunnablePlayerSave().runTaskTimer(this, 60L, 60L);
		new RunnableUpdateGUI().runTaskTimer(this, 20L, 20L);
		new RunnableBossBarUpdate().runTaskTimer(this, 20L, 0L);
		new RunnableHologramUpdate().runTaskTimer(this, 1200L, 1200L);

		Objects.requireNonNull(this.getCommand("1vs1")).setExecutor(new DuelCommand());
		Objects.requireNonNull(this.getCommand("1vs1")).setTabCompleter(new DuelCommand());
		Objects.requireNonNull(this.getCommand("map")).setExecutor(new MapCommand());

		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);

		if (mainConfig.isOptionUseKit()) {
			Objects.requireNonNull(this.getCommand("kit")).setExecutor(new KitCommand());
			Objects.requireNonNull(this.getCommand("kit")).setTabCompleter(new KitCommand());
			kitManager = new KitManager();
		}

		if (mainConfig.isMvdwPlaceHolderAPI()) {
			if (APIUtils.isMVdWPlaceholderAPI()) {
				new MVdWPlaceholderAPI().register();
				log(LOG_LEVEL.INFO, "The plugin successfully hooked the 'MVdWPlaceholder'");
			} else {
				log(LOG_LEVEL.ERROR, "The 'MVdWPlaceholderAPI' plugin was not found.");
			}
		}

		if (mainConfig.isPlaceHolderAPI()) {
			if (APIUtils.isPlaceholderAPI()) {
				PlaceholderAPI placeholderAPI = new PlaceholderAPI();
				placeholderAPI.register();
				if (!placeholderAPI.isRegistered()) {
					log(LOG_LEVEL.ERROR,
							"An unknown error occurred while registering the Placeholder API. Registration failed.");
				} else {
					log(LOG_LEVEL.INFO, "The plugin successfully hooked the 'PlaceholderAPI'");
				}
			} else {
				log(LOG_LEVEL.ERROR, "The 'PlaceholderAPI' plugin was not found.");
			}
		}

		if (mainConfig.isCitizensAPI()) {
			if (APIUtils.isCitizens()) {
				npcConfig = new NPCConfig(this);
				npcConfig.load();

				long updateDelay = npcConfig.getConfig().getLong("hologramUpdateDelay");

				if (CitizensAPI.getNamedNPCRegistry("duel") == null) {
					CitizensAPI.createNamedNPCRegistry("duel", new CitizenDataStore());
				}

				for (World world : this.getServer().getWorlds()) {
					for (Entity entity : world.getEntities()) {
						if (entity instanceof ArmorStand && (entity.hasMetadata("Duel")
								|| (entity.getCustomName() != null && entity.getCustomName().startsWith("§c§r")))) {
							entity.remove();
						}
					}
				}

				npcController = new NPCController(this);

				this.getServer().getPluginManager().registerEvents(new CitizensListener(), this);

				new RunnableNPCNameTagUpdate().runTaskTimer(this, 0L, 0L);
				new RunnableNPCSkinUpdate().runTaskTimer(this, 100L, 100L);
				new RunnableNPCUpdateHologram().runTaskTimer(this, updateDelay, updateDelay);

				log(LOG_LEVEL.INFO, "The plugin successfully hooked the 'Citizens'");
			} else {
				log(LOG_LEVEL.ERROR, "The 'Citizens' plugin was not found.");
			}
		}

		for (PlayerObject playerObject : playerController.getAll()) {
			Player player = playerObject.getPlayer();
			player.setGameMode(GameMode.ADVENTURE);
			player.setMaxHealth(20.0D);
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			PlayerUtils.teleportToLobby(player);
			KitUtils.joinItem(player, playerObject);
			getBossbar().addPlayer(player, "");
			getPlayerController().addPlayer(player.getUniqueId());
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
		}

		log(LOG_LEVEL.INFO,
				"The plugin has been activated (" + (System.currentTimeMillis() - startTime) / 1000.0 + "s)");
		setup = true;
	}

	public void onDisable() {
		if (!setup) {
			return;
		}

		HandlerList.unregisterAll(this);
		this.getServer().getScheduler().cancelTasks(this);

		getServer().getMessenger().unregisterOutgoingPluginChannel(this);
		getServer().getMessenger().unregisterIncomingPluginChannel(this);

		if (APIUtils.isCitizens()) {
			npcController.shutdown();
			if (CitizensAPI.getNamedNPCRegistry("duel") != null) {
				CitizensAPI.removeNamedNPCRegistry("duel");
			}
		}

		playerController.shutdown();
		arenaManager.shutdown();
		dataStorage.shutdown();
		hologramController.destoryAll();
		log(LOG_LEVEL.INFO, "The plugin has been disabled");
		for (PlayerObject playerObject : playerController.getAll()) {
			Player player = playerObject.getPlayer();
			getBossbar().removePlayer(player);
			getPlayerController().removePlayer(player.getUniqueId());
		}
	}

	private boolean loadNMS() {
		try {
			Class<?> targetNMS = Class.forName("net.Duels.nms.impl." + NMSUtils.getServerVersion() + ".NMSHandler");
			if (NMS.class.isAssignableFrom(targetNMS)) {
				nms = (NMS) targetNMS.newInstance();
				log(LOG_LEVEL.INFO, "NMS loaded successfully! [" + NMSUtils.getServerVersion() + "]");
			} else {
				log(LOG_LEVEL.ERROR, ChatColor.RED
						+ "I found the class but it is not related to NMS! Please report this to the developer!");
				this.getServer().getPluginManager().disablePlugin(this);
			}
			return true;
		} catch (Exception e) {
			log(LOG_LEVEL.ERROR,
					ChatColor.RED
							+ "The current server version does not support this plugin! That's too bad! [Current: "
							+ NMSUtils.getServerVersion() + "]");
			log(LOG_LEVEL.ERROR, ChatColor.RED + "Available versions: " + NMSUtils.getSupportVersions());
			this.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
	}

	private boolean loadBossBarNMS() {
		try {
			Class<?> targetNMS = Class.forName("net.Duels.nms.impl." + NMSUtils.getServerVersion() + ".NMSBossBar");
			if (BossBar.class.isAssignableFrom(targetNMS)) {
				bossbar = (BossBar) targetNMS.newInstance();
				log(LOG_LEVEL.INFO, "BossBarNMS loaded successfully! [" + NMSUtils.getServerVersion() + "]");
			} else {
				log(LOG_LEVEL.ERROR, ChatColor.RED
						+ "I found the class but it is not related to NMS! Please report this to the developer!");
				this.getServer().getPluginManager().disablePlugin(this);
			}
			return true;
		} catch (Exception e) {
			log(LOG_LEVEL.DEBUG, ChatColor.RED + "Dectected Error: " + ((e.getMessage() != null) ? e.getMessage() : "")
					+ " and " + e.getClass().getSimpleName());
			log(LOG_LEVEL.ERROR,
					ChatColor.RED
							+ "The current server version does not support this plugin! That's too bad! [Current: "
							+ NMSUtils.getServerVersion() + "]");
			log(LOG_LEVEL.ERROR, ChatColor.RED + "Available versions: " + NMSUtils.getSupportVersions());
			this.getServer().getPluginManager().disablePlugin(this);
			return false;
		}
	}

	public static void log(LOG_LEVEL level, String text) {
		if (level == LOG_LEVEL.DEBUG && setup
				&& !((Boolean) mainConfig.getMapping().getOrDefault("debug.use", Boolean.valueOf(false)))
						.booleanValue())
			return;
		getInstance().getServer().getConsoleSender().sendMessage(PREFIX + " " + ChatColor.DARK_GRAY
				+ "[" + level.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + text);
	}

	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		subchannel.equals("BungeeSend");
	}

	public enum LOG_LEVEL {
		INFO(ChatColor.GREEN + "INFO"), WARNING(ChatColor.YELLOW + "WARNING"), ERROR(ChatColor.RED + "ERROR"),
		DEBUG(ChatColor.AQUA + "DEBUG");

		private final String name;

		private LOG_LEVEL(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
