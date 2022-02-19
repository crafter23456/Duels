package net.Duels.datastorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import net.Duels.utility.FileUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.Duel.LOG_LEVEL;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;
import net.Duels.utility.ValueUtils;

public class DataStorage {

	private final File directory;

	@Getter
	private final MySQL mySQL;
	
	@Getter
	private final Map<StatType, Pair<Long, List<StatObject>>> cached = new LinkedHashMap<>();

	public DataStorage(JavaPlugin plugin) {
		FileUtils.checkDirectory(this.directory = new File(plugin.getDataFolder(), "players"));

		if (Duel.getMainConfig().getDataType() == DataType.MYSQL) {
			try {
				Class.forName("java.sql.Connection");
			} catch (Exception ignored) {
				Duel.getMainConfig().setDataType(DataType.FILE);
				Duel.log(Duel.LOG_LEVEL.ERROR,
						"Error! The java installed on the device does not support mysql! Changed to save file.");
				this.mySQL = null;
				return;
			}
			this.mySQL = new MySQL();
		} else {
			this.mySQL = null;
		}
	}

	public void loadPlayer(PlayerObject object) {
		if (Duel.getMainConfig().getDataType() == DataType.FILE) {
			try {
				File playerFile = new File(this.directory, object.getUniqueId() + ".yml");
				if (!playerFile.exists() && !playerFile.createNewFile()) {
					Duel.log(Duel.LOG_LEVEL.ERROR,
							object.getName() + "'s data file could not be created");
					return;
				}
				FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
				config.options().copyDefaults(true);
				config.setDefaults(YamlConfiguration
						.loadConfiguration(new BufferedReader(new InputStreamReader(
								Duel.getInstance().getResource("playerFile.yml"), StandardCharsets.UTF_8))));
				object.setKills(config.getInt("kills"));
				object.setDeaths(config.getInt("deaths"));
				object.setWins(config.getInt("wins"));
				object.setLose(config.getInt("lose"));
				object.setWinStreak(config.getInt("winStreak"));
				object.setBestStreak(config.getInt("bestStreak"));
				object.setScore(config.getInt("score"));
				object.setCoin(config.getInt("coin"));
				object.setXp(config.getInt("xp"));
				object.setKitSelected(config.getString("kitSelected"));
			} catch (Exception e) {
				e.printStackTrace();
				Duel.log(Duel.LOG_LEVEL.ERROR, object.getName() + "'s data could not be loaded");
			}
		} else {
			if (this.mySQL.containsData(object.getUniqueId())) {
				this.mySQL.executeQuery("UPDATE `players` SET `player_name` = ? WHERE `uuid` = ?", object.getName(),
						object.getUniqueId().toString());
			} else {
				this.mySQL.executeQuery(
						"INSERT INTO `players` (player_id, uuid, player_name, kills, deaths, wins, lose, winStreak, bestStreak, score, coin, xp) VALUES (NULL, ?, ?, 0, 0, 0, 0, 0, 0, 0, 0, 0)",
						object.getUniqueId().toString(), object.getName());
			}
			Pair<ResultSet, PreparedStatement> doubleObject = this.mySQL.getResult(object.getUniqueId());
			ResultSet result = doubleObject.getA();
			try {
				if (result.next()) {
					object.setKills(result.getInt("kills"));
					object.setDeaths(result.getInt("deaths"));
					object.setWins(result.getInt("wins"));
					object.setLose(result.getInt("lose"));
					object.setWinStreak(result.getInt("winStreak"));
					object.setBestStreak(result.getInt("bestStreak"));
					object.setScore(result.getInt("score"));
					object.setCoin(result.getInt("coin"));
					object.setXp(result.getInt("xp"));
					object.setKitSelected(result.getString("kitSelected"));
				}
			} catch (Exception e2) {
				e2.printStackTrace();
				Duel.log(Duel.LOG_LEVEL.ERROR, object.getName() + "'s data could not be loaded");
				try {
					doubleObject.getB().close();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
				return;
			} finally {
				try {
					doubleObject.getB().close();
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
			try {
				doubleObject.getB().close();
			} catch (Exception e3) {
				e3.printStackTrace();
			}
		}
	}

	public void savePlayer(PlayerObject object) {
		if (Duel.getMainConfig().getDataType() == DataType.FILE) {
			try {
				File playerFile = new File(this.directory, object.getUniqueId() + ".yml");
				if (!playerFile.exists() && !playerFile.createNewFile()) {
					Duel.log(Duel.LOG_LEVEL.ERROR,
							object.getName() + "'s data file could not be created");
					return;
				}
				FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
				config.options().copyDefaults(true);
				if (config == null) {
					Duel.log(LOG_LEVEL.ERROR, "An error occurred while saving the player data");
					return;
				}
				config.setDefaults(YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Duel.getInstance().getResource("playerFile.yml"), StandardCharsets.UTF_8))));
				config.set("uuid", object.getUniqueId().toString());
				config.set("name", object.getName());
				config.set("kills", object.getWins());
				config.set("deaths", object.getDeaths());
				config.set("wins", object.getWins());
				config.set("lose", object.getLose());
				config.set("winStreak", object.getWinStreak());
				config.set("bestStreak", object.getBestStreak());
				config.set("score", object.getScore());
				config.set("coin", object.getCoin());
				config.set("xp", object.getXp());
				config.set("kitSelected", object.getKitSelected());
				config.save(playerFile);
			} catch (Exception e) {
				e.printStackTrace();
				Duel.log(Duel.LOG_LEVEL.ERROR, object.getName() + "'s data could not be loaded");
			}
		} else if (this.mySQL.containsData(object.getUniqueId())) {
			this.mySQL.executeQuery(
					"UPDATE `players` SET `player_name` = ?, `kills` = ?, `deaths` = ?, `wins` = ?, `lose` = ?, `winStreak` = ?, `bestStreak` = ?, `score` = ?, `coin` = ?, `xp` = ?, `kitSelected` = ? WHERE `uuid` = ?",
					object.getName(), object.getKills(), object.getDeaths(), object.getWins(), object.getLose(),
					object.getWinStreak(), object.getBestStreak(), object.getScore(), object.getCoin(), object.getXp(),
					object.getKitSelected(), object.getUniqueId().toString());
		} else {
			this.mySQL.executeQuery(
					"INSERT INTO `players` (player_id, uuid, player_name, kills, deaths, wins, lose, winStreak, bestStreak, score, coin, xp, kitSelected) VALUES (NULL, ?, ?, 0, 0, 0, 0, 0, 0, 0, 0, 0, '')",
					object.getUniqueId().toString(), object.getName());
		}
	}

	public List<StatObject> getStats(StatType statType) {
		Pair<Long, List<StatObject>> cache = this.cached.getOrDefault(statType, null);
		if (cache != null) {
			if (cache.getA() + 10000 >= System.currentTimeMillis()) {
				return cache.getB();
			}
			this.cached.remove(statType);
		}
		
		List<StatObject> list = new LinkedList<>();
		if (Duel.getMainConfig().getDataType() == DataType.FILE) {
			byte b;
			int i;
			File[] arrayOfFile;
			for (i = (arrayOfFile = Objects.requireNonNull(this.directory.listFiles())).length, b = 0; b < i;) {
				File file = arrayOfFile[b];
				if (file.getName().endsWith(".yml")) {
					YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
					yamlConfiguration.options().copyDefaults(true);
					yamlConfiguration.setDefaults((Configuration) YamlConfiguration.loadConfiguration(
							new BufferedReader(new InputStreamReader(Duel.getInstance().getResource("playerFile.yml"),
									StandardCharsets.UTF_8))));
					list.add(new StatObject(yamlConfiguration.getString("name"), yamlConfiguration.getInt("kills"),
							yamlConfiguration.getInt("deaths"), yamlConfiguration.getInt("wins"),
							yamlConfiguration.getInt("lose"), yamlConfiguration.getInt("winStreak"),
							yamlConfiguration.getInt("bestStreak"), yamlConfiguration.getInt("score"),
							yamlConfiguration.getInt("coin"), yamlConfiguration.getInt("xp")));
				}
				b++;
			}
		} else {
			Pair<ResultSet, PreparedStatement> doubleObject = this.mySQL.getResult();
			ResultSet result = doubleObject.getA();
			try {
				while (result.next())
					list.add(new StatObject(result.getString("player_name"), result.getInt("kills"),
							result.getInt("deaths"), result.getInt("wins"), result.getInt("lose"),
							result.getInt("winStreak"), result.getInt("bestStreak"), result.getInt("score"),
							result.getInt("coin"), result.getInt("xp")));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					doubleObject.getB().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		List<StatObject> targetData = list.stream().sorted((stat1, stat2) -> ValueUtils.getValueByStatType(stat2, statType)
				- ValueUtils.getValueByStatType(stat1, statType)).collect(Collectors.toList());
		this.cached.put(statType, new Pair<>(System.currentTimeMillis(), targetData));
		return targetData;
	}

	public void shutdown() {
		if (Duel.getMainConfig().getDataType() == DataType.MYSQL && this.mySQL != null) {
			try {
				this.mySQL.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public enum DataType {
		FILE, MYSQL;
	}

	public enum StatType {
		KILLS, DEATHS, WINS, LOSE, WINSTREAK, BESTSTREAK, SCORE, COIN, XP;

		public static StatType parseType(String name) {
			StatType[] values;
			for (int length = (values = values()).length, i = 0; i < length; ++i) {
				StatType statType = values[i];
				if (statType.name().equalsIgnoreCase(name)) {
					return statType;
				}
			}
			return null;
		}
	}

	public static class StatObject {

		@Getter
		private final String name;

		@Getter
		private final int kills;

		@Getter
		private final int deaths;

		@Getter
		private final int wins;

		@Getter
		private final int lose;

		@Getter
		private final int winStreak;

		@Getter
		private final int bestStreak;

		@Getter
		private final int score;

		@Getter
		private final int coin;

		@Getter
		private final int xp;

		public StatObject(String name, int kills, int deaths, int wins, int lose, int winStreak, int bestStreak,
				int score, int coin, int xp) {
			this.name = name;
			this.kills = kills;
			this.deaths = deaths;
			this.wins = wins;
			this.lose = lose;
			this.winStreak = winStreak;
			this.bestStreak = bestStreak;
			this.score = score;
			this.coin = coin;
			this.xp = xp;
		}
	}
}
