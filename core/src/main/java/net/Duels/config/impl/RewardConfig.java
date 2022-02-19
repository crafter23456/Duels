package net.Duels.config.impl;

import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.config.BaseConfig;

public class RewardConfig extends BaseConfig {

	@Getter
	private List<String> win_rewards;

	public RewardConfig(JavaPlugin plugin) {
		super(plugin, "rewards.yml");
	}

	@Override
	public void load() {
		this.win_rewards = this.getConfig().getStringList("win-rewards");
	}

}
