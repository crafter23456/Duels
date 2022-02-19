package net.Duels.config.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.config.BaseConfig;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.Pair;

public class MessageConfig extends BaseConfig {
	
	@Getter
	private final Map<String, List<String>> messages;

	public MessageConfig(JavaPlugin plugin) {
		super(plugin, "messages.yml");
		this.messages = new LinkedHashMap<>();
	}

	@Override
	public void load() {
		for (String key : this.getConfig().getKeys(true)) {
			boolean isSection = this.getConfig().isConfigurationSection(key);
			if (isSection) {
				continue;
			}
			boolean isList = this.getConfig().isList(key);
			if (!isList) {
				boolean isString = this.getConfig().isString(key);
				if (isString) {
					this.messages.put(key, Collections.singletonList(
							ChatUtils.colorTranslate(this.getConfig().getString(key).replace("<right_chat>", "\u00BB"))));
				} else {
					this.messages.put(key, Collections.singletonList(ChatColor.RED + key));
					Duel.log(Duel.LOG_LEVEL.ERROR, key + "While loading the key '" + key
							+ "' from the message file, the type was incorrect and could not be loaded!");
				}
			} else {
				this.messages.put(key, ChatUtils.colorTranslate(this.getConfig().getStringList(key)));
			}
		}
	}

	public String getString(String key) {
		if (!this.messages.containsKey(key)) {
			Duel.log(Duel.LOG_LEVEL.ERROR, "No message found for " + key + "!");
		}
		return this.getValue(key).isEmpty() ? ""
				: ((this.getValue(key).size() >= 1) ? this.getValue(key).get(0)
						: (ChatColor.RED + "Out of Message Size (" + key + ")"));
	}

	public String getString(String key, List<Pair<String, String>> objects) {
		if (!this.messages.containsKey(key)) {
			Duel.log(Duel.LOG_LEVEL.ERROR, "No message found for " + key + "!");
		}
		String value = this.getValue(key).isEmpty() ? ""
				: ((this.getValue(key).size() >= 1) ? this.getValue(key).get(0)
						: (ChatColor.RED + "Out of Message Size (" + key + ")"));
		if (objects.size() >= 1) {
			for (Pair<String, String> targetValue : objects) {
				value = value.replace(targetValue.getA(), targetValue.getB());
			}
		}
		return value;
	}

	@SafeVarargs
	public final List<String> getList(String key, Pair<String, String>... objects) {
		if (!this.messages.containsKey(key)) {
			Duel.log(Duel.LOG_LEVEL.ERROR, "No message found for " + key + "!");
		}

		if (objects == null || objects.length <= 0) {
			return this.getValue(key);
		}

		List<String> list = new LinkedList<>();
		for (String text : this.getValue(key)) {
			for (Pair<String, String> object : objects) {
				text = text.replace(object.getA(), object.getB());
			}
			list.add(text);
		}
		return list;
	}

	private List<String> getValue(String key) {
		return this.messages.getOrDefault(key, new LinkedList<>());
	}
	
}
