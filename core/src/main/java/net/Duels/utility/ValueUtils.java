package net.Duels.utility;

import net.Duels.datastorage.DataStorage;

public class ValueUtils {
	
	public static int getValueByStatType(DataStorage.StatObject object, DataStorage.StatType statType) {
		if (statType == DataStorage.StatType.KILLS) {
			return object.getKills();
		}
		if (statType == DataStorage.StatType.DEATHS) {
			return object.getDeaths();
		}
		if (statType == DataStorage.StatType.WINS) {
			return object.getWins();
		}
		if (statType == DataStorage.StatType.LOSE) {
			return object.getLose();
		}
		if (statType == DataStorage.StatType.WINSTREAK) {
			return object.getWinStreak();
		}
		if (statType == DataStorage.StatType.BESTSTREAK) {
			return object.getBestStreak();
		}
		if (statType == DataStorage.StatType.SCORE) {
			return object.getScore();
		}
		if (statType == DataStorage.StatType.COIN) {
			return object.getCoin();
		}
		if (statType == DataStorage.StatType.XP) {
			return object.getXp();
		}
		return object.getKills();
	}
	
}
