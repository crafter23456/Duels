package net.Duels.config.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import net.Duels.Duel;
import net.Duels.config.BaseConfig;
import net.Duels.datastorage.DataStorage;
import net.Duels.utility.ConfigUtils;

public class MainConfig extends BaseConfig {

	@Getter
	private Location lobby;
	@Getter
	private String goldenHeadsTexture;
	
	@Setter
	@Getter
	private DataStorage.DataType dataType;

	@Getter
	private boolean optionUseLobbyScoreboard;
	@Getter
	private boolean optionUseLobbyItems;
	@Getter
	private boolean optionUseLobbyTablistName;
	@Getter
	private boolean optionUseRewards;
	@Getter
	private boolean optionBungee;
	@Getter
	private boolean optionJoinTeleport;
	@Getter
	private boolean optionShield;
	@Getter
	private boolean optionUseKit;
	@Getter
	private boolean optionBossbar;
	@Getter
	private boolean optionChat;
	@Getter
	private boolean debug;

	@Getter
	private String bungeeServer;

	@Getter
	private boolean placeHolderAPI, mvdwPlaceHolderAPI, citizensAPI;
	
	@Getter
	private List<String> blacklistWorlds;
	
	@Getter
	private Map<String, Object> mapping;

	public MainConfig(JavaPlugin plugin) {
		super(plugin, "config.yml");
	}

	@Override
	public void load() {
		this.mapping = new LinkedHashMap<>();

		for (String key : this.getConfig().getKeys(true)) {
			if (!this.getConfig().isConfigurationSection(key)) {
				this.mapping.put(key, this.getConfig().get(key));
			}
		}
		
		if (this.getConfig().contains("location.lobby")) {
			this.lobby = ConfigUtils.stringToLocation(this.getConfig().getString("location.lobby"));
		}
		
		this.optionUseLobbyScoreboard = this.getConfig().getBoolean("option.use-lobby-scoreboard");
		this.optionUseLobbyItems = this.getConfig().getBoolean("option.use-lobby-items");
		
		try {
			this.dataType = DataStorage.DataType.valueOf(this.getConfig().getString("option.save-type"));
		} catch (Exception e) {
			Duel.log(Duel.LOG_LEVEL.WARNING,
					"The config value is incorrect and has been replaced by default. (option.save-type)");
			this.dataType = DataStorage.DataType.FILE;
			this.getConfig().set("option.save-type", "FILE");
		}
		
		this.blacklistWorlds = this.getConfig().getStringList("blacklist-worlds");
		this.optionBungee = this.getConfig().getBoolean("option.bungee");
		this.optionJoinTeleport = this.getConfig().getBoolean("option.join-teleport");
		this.optionUseLobbyTablistName = this.getConfig().getBoolean("option.use-lobby-tablist-name");
		this.optionUseRewards = this.getConfig().getBoolean("option.rewards");
		this.optionUseKit = this.getConfig().getBoolean("option.kit");
		this.optionShield = this.getConfig().getBoolean("arena.shield");
		this.optionBossbar = this.getConfig().getBoolean("option.bossbar");
		this.optionChat = this.getConfig().getBoolean("option.chat");
		this.placeHolderAPI = this.getConfig().getBoolean("api.placeholder");
		this.mvdwPlaceHolderAPI = this.getConfig().getBoolean("api.mvdwplaceholder");
		this.citizensAPI = this.getConfig().getBoolean("api.citizens");
		this.goldenHeadsTexture = this.getConfig().getString("goldenheads.texture");
		this.debug = this.getConfig().getBoolean("debug.use");
		this.bungeeServer = this.getConfig().getString("bungeecord.server");
	}

	public void setLobby(Location lobby) {
		this.lobby = lobby;
		this.getConfig().set("location.lobby", ConfigUtils.locationToString(lobby));
		this.save();
	}

}
