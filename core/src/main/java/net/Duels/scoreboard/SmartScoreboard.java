package net.Duels.scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SmartScoreboard {
	private final Scoreboard scoreboard;
	private Objective objective;
	protected int currentLine;
	protected Map<Integer, String> last;
	protected Map<Integer, String> cache;

	public SmartScoreboard(Scoreboard scoreboard, Objective objective) {
		this.currentLine = 16;
		this.last = new LinkedHashMap<>();
		this.cache = new LinkedHashMap<>();
		this.scoreboard = scoreboard;
		this.objective = objective;
	}

	public void set(Player player) {
		player.setScoreboard(this.scoreboard);
	}

	public boolean add(String s) {
		if (this.currentLine <= 0) {
			return false;
		}
		this.cache.put(this.currentLine--, s.replace("&", ""));
		return true;
	}

	public boolean blank() {
		return this.add("");
	}

	public void updateScoreboard() {
		this.currentLine = this.cache.size();
		this.cache.values().forEach(this::update);
		this.last = null;
		this.cache = null;
	}

	protected boolean update(String s) {
		if (s.length() > 32 && this.last.containsKey(this.currentLine)
				&& !this.last.get(this.currentLine).equals(s.substring(16, 32))) {
			this.scoreboard.resetScores(this.last.get(this.currentLine));
		}
		String prefix = (s.length() > 32) ? s.substring(0, 16) : "";
		String s2 = (s.length() > 32) ? s.substring(16, 32)
				: String.valueOf(ChatColor.values()[this.currentLine - 1]);
		String suffix = (s.length() > 32) ? s.substring(32, Math.min(s.length(), 48)) : "";
		if (!s.isEmpty() && prefix == "") {
			prefix = s.substring(0, Math.min(s.length(), 16));
		}
		if (s.length() > 16 && suffix.equals("")) {
			s = ChatColor.getLastColors(prefix) + s.replace(prefix, "");
			suffix = s.substring(0, Math.min(s.length(), 16));
		}
		Team team = this.scoreboard.getTeam("[team:" + this.currentLine + "]");
		if (team == null) {
			team = this.scoreboard.registerNewTeam("[team:" + this.currentLine + "]");
		}
		if (prefix.endsWith("§")) {
			prefix = prefix.substring(0, prefix.length() - 1);
			if (suffix.startsWith(ChatColor.getLastColors(prefix))) {
				String temp1 = suffix.substring(0, 1);
				String temp2 = suffix.substring(2);
				suffix = temp1 + temp2;
			}
		}
		if (suffix.startsWith("§l")) {
			suffix = prefix.substring(prefix.length() - 2) + suffix;
			prefix = prefix.substring(0, prefix.length() - 2);
		}
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		if (!team.hasEntry(s2)) {
			team.addEntry(s2);
		}
		this.objective.getScore(s2).setScore(this.currentLine);
		this.last.put(this.currentLine--, s2);
		return true;
	}

	public void reset() {
		for (Team team : this.getTeams()) {
			team.unregister();
		}
		this.cache.clear();
		this.last.clear();
		this.currentLine = 16;
		String name = this.objective.getName();
		String displayname = this.objective.getDisplayName();
		DisplaySlot slot = this.objective.getDisplaySlot();
		this.objective.unregister();
		(this.objective = this.scoreboard.registerNewObjective(name, "dummy")).setDisplayName(displayname);
		this.objective.setDisplaySlot(slot);
	}

	public Team registerTeam(String s) {
		return this.scoreboard.registerNewTeam(s);
	}

	public Team getTeam(String s) {
		return this.scoreboard.getTeam(s);
	}

	public Set<Team> getTeams() {
		return this.scoreboard.getTeams();
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
}
