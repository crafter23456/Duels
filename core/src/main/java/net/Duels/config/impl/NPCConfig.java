package net.Duels.config.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.config.BaseConfig;

public class NPCConfig extends BaseConfig {
	
	@Getter
	private final Map<String, Object> mapping;

	public NPCConfig(JavaPlugin plugin) {
		super(plugin, "npcs.yml");
		this.mapping = new LinkedHashMap<>();
	}

	@Override
	public void load() {
		for (String key : this.getConfig().getKeys(true)) {
			if (!this.getConfig().isConfigurationSection(key)) {
				this.mapping.put(key, this.getConfig().get(key));
			}
		}
	}
	
}
