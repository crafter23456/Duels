package net.Duels.config.impl;

import java.util.*;

import net.Duels.achievements.Achievement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.achievements.AchievementCommandType;
import net.Duels.achievements.AchievementType;
import net.Duels.config.BaseConfig;
import net.Duels.player.PlayerObject;

public class AchievementConfig extends BaseConfig {
	
	@Getter
	private final Map<AchievementType, List<Achievement>> achievements;

	public AchievementConfig(JavaPlugin plugin) {
		super(plugin, "achievements.yml");
		this.achievements = new LinkedHashMap<>();
	}

	@Override
	public void load() {
		for (AchievementType type : AchievementType.values()) {
			this.loadAchievement(type);
		}
	}

	private void loadAchievement(AchievementType targetType) {
		List<Achievement> achievements = new LinkedList<>();
		ConfigurationSection section = this.getConfig().getConfigurationSection("achievements");
		for (String key : section.getKeys(false)) {
			AchievementType type = this.parseAchievementType(section.getString(key + ".type"));
			if (targetType != type) {
				continue;
			}
			String name = section.getString(key + ".name");
			String description = section.getString(key + ".description");
			String sound = section.getString(key + ".rewards.sound");
			int amount = section.getInt(key + ".amount");
			int xp = section.getInt(key + ".rewards.xp");
			int coin = section.getInt(key + ".rewards.coin");
			List<String> messages = section.getStringList(key + ".messages");
			List<String> commands = section
					.getStringList(key + ".rewards.commands");
			AchievementCommandType commandType = this
					.parseAchievementCommandType(section.getString(key + ".rewards.command-sender"));
			achievements.add(
					new Achievement(type, commandType, name, description, sound, amount, xp, coin, commands, messages));
		}
		this.achievements.put(targetType, achievements);
	}

	public void checkForReward(PlayerObject playerObject, int targetAmount, AchievementType type) {
		for (Achievement achievement : this.achievements.get(type)) {
			if (achievement.getAmount() == targetAmount) {
				achievement.reward(playerObject);
			}
		}
	}

	public AchievementType parseAchievementType(String name) {
		try {
			return AchievementType.valueOf(name);
		} catch (Exception e) {
			return null;
		}
	}

	public AchievementCommandType parseAchievementCommandType(String name) {
		try {
			return AchievementCommandType.valueOf(name);
		} catch (Exception e) {
			Duel.log(Duel.LOG_LEVEL.ERROR,
					"Replaced with CONSOLE because the achievement command type was not found (old value: \"" + name
							+ "\")");
			return AchievementCommandType.CONSOLE;
		}
	}

}
