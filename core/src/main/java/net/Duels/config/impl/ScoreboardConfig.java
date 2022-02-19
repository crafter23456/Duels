package net.Duels.config.impl;

import java.util.*;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import net.Duels.config.BaseConfig;

public class ScoreboardConfig extends BaseConfig {

	private final Map<ScoreboardType, ScoreboardData> scoreboards;

	public ScoreboardConfig(JavaPlugin plugin) {
		super(plugin, "scoreboard.yml");
		this.scoreboards = new LinkedHashMap<>();
	}

	@Override
	public void load() {
		ScoreboardType[] values;
		for (int length = (values = ScoreboardType.values()).length, i = 0; i < length; ++i) {
			ScoreboardType type = values[i];
			this.loadScoreboard(type);
		}
	}

	private void loadScoreboard(ScoreboardType type) {
		String title = this.getConfig().getString(type.getIdentifier() + ".title");
		boolean text_Adornment = this.getConfig().getBoolean(type.getIdentifier() + ".text-adornment");
		List<String> lines = this.getConfig()
				.getStringList(type.getIdentifier() + ".lines");
		this.scoreboards.put(type, new ScoreboardData(title, text_Adornment, lines));
	}

	public Map<ScoreboardType, ScoreboardData> getScoreboards() {
		return this.scoreboards;
	}

	public enum ScoreboardType {
		LOBBY("lobby-scoreboard"), SETUP("uhc-duels.setup-scoreboard"), WAITING_PLAYER("uhc-duels.waiting-player-scoreboard"),
		COUNTING("uhc-duels.counting-scoreboard"), IN_GAME("uhc-duels.ingame-scoreboard"), ENDING("uhc-duels.ending-scoreboard"),
		SPECTATOR("uhc-duels.spectator-scoreboard");

		private final String identifier;

		private ScoreboardType(String identifier) {
			this.identifier = identifier;
		}

		public String getIdentifier() {
			return this.identifier;
		}
	}

	public static class ScoreboardData {

		@Getter
		private final String title;

		@Getter
		private final boolean text_Adornment;

		@Getter
		private final List<String> lines;

		public ScoreboardData(String title, boolean text_adornment, List<String> lines) {
			this.lines = new LinkedList<>();
			this.title = title;
			this.text_Adornment = text_adornment;
			this.lines.addAll(lines);
		}
	}
}
