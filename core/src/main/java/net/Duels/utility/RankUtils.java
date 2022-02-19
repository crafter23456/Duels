package net.Duels.utility;

import org.bukkit.ChatColor;

public class RankUtils {
	
	public static String getRankProcces(int score) {
		if (hasRank(score, "Silver 1")) {
			return process(score, 10);
		}
		if (hasRank(score, "Silver 2")) {
			return process(score, 30);
		}
		if (hasRank(score, "Silver 3")) {
			return process(score, 60);
		}
		if (hasRank(score, "Silver 4")) {
			return process(score, 100);
		}
		if (hasRank(score, "Silver Elite")) {
			return process(score, 150);
		}
		if (hasRank(score, "Silver Master")) {
			return process(score, 170);
		}
		if (hasRank(score, "Gold Nova 1")) {
			return process(score, 180);
		}
		if (hasRank(score, "Gold Nova 2")) {
			return process(score, 200);
		}
		if (hasRank(score, "Gold Nova 3")) {
			return process(score, 220);
		}
		if (hasRank(score, "Gold Nova 4")) {
			return process(score, 250);
		}
		if (hasRank(score, "Nova Master")) {
			return process(score, 300);
		}
		if (hasRank(score, "Guardian 1")) {
			return process(score, 500);
		}
		if (hasRank(score, "Guardian 2")) {
			return process(score, 550);
		}
		if (hasRank(score, "Guardian Elite")) {
			return process(score, 650);
		}
		if (hasRank(score, "Guardin Master")) {
			return process(score, 750);
		}
		if (hasRank(score, "Legendary Eagle")) {
			return process(score, 1000);
		}
		if (hasRank(score, "Supreme")) {
			return process(score, 3000);
		}
		if (hasRank(score, "Global Elite")) {
			return process(score, 5000);
		}
		return ChatColor.RED + "NONE";
	}

	public static String process(int int1, int int2) {
		if (int2 >= 3000) {
			return "";
		}
		return ChatColor.translateAlternateColorCodes('&', " &e(" + int1 + "/" + int2 + ")");
	}

	public static String getRank(int score) {
		if (verifyRank(score, -9999, 10)) {
			return ChatColor.translateAlternateColorCodes('&', "&7Silver 1");
		}
		if (verifyRank(score, 10, 30)) {
			return ChatColor.translateAlternateColorCodes('&', "&7Silver 2");
		}
		if (verifyRank(score, 30, 60)) {
			return ChatColor.translateAlternateColorCodes('&', "&7Silver 3");
		}
		if (verifyRank(score, 60, 100)) {
			return ChatColor.translateAlternateColorCodes('&', "&7Silver 4");
		}
		if (verifyRank(score, 100, 150)) {
			return ChatColor.translateAlternateColorCodes('&', "&7Silver Elite");
		}
		if (verifyRank(score, 150, 170)) {
			return ChatColor.translateAlternateColorCodes('&', "&7Silver Master");
		}
		if (verifyRank(score, 170, 180)) {
			return ChatColor.translateAlternateColorCodes('&', "&6Gold Nova 1");
		}
		if (verifyRank(score, 180, 200)) {
			return ChatColor.translateAlternateColorCodes('&', "&6Gold Nova 2");
		}
		if (verifyRank(score, 200, 220)) {
			return ChatColor.translateAlternateColorCodes('&', "&6Gold Nova 3");
		}
		if (verifyRank(score, 220, 250)) {
			return ChatColor.translateAlternateColorCodes('&', "&6Gold Nova 4");
		}
		if (verifyRank(score, 250, 300)) {
			return ChatColor.translateAlternateColorCodes('&', "&6Nova Master");
		}
		if (verifyRank(score, 300, 500)) {
			return ChatColor.translateAlternateColorCodes('&', "&9Guardian 1");
		}
		if (verifyRank(score, 500, 550)) {
			return ChatColor.translateAlternateColorCodes('&', "&9Guardian 2");
		}
		if (verifyRank(score, 550, 650)) {
			return ChatColor.translateAlternateColorCodes('&', "&9Guardian Elite");
		}
		if (verifyRank(score, 650, 750)) {
			return ChatColor.translateAlternateColorCodes('&', "&9Guardian Master");
		}
		if (verifyRank(score, 750, 1000)) {
			return ChatColor.translateAlternateColorCodes('&', "&bLegendary Eagle");
		}
		if (verifyRank(score, 1000, 3000)) {
			return ChatColor.translateAlternateColorCodes('&', "&bSupreme");
		}
		if (verifyRank(score, 3000, 50000000)) {
			return ChatColor.translateAlternateColorCodes('&', "&aGlobal Elite");
		}
		return "NONE";
	}

	public static boolean verifyRank(int score, int int1, int int2) {
		return score >= int1 && score < int2;
	}

	public static boolean hasRank(int score, String s) {
		return getRank(score).contains(s);
	}
	
}
