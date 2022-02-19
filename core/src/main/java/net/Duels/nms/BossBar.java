package net.Duels.nms;

import org.bukkit.entity.Player;

public interface BossBar {
	void addPlayer(Player p0, String p1);

	void removePlayer(Player p0);

	void setTitle(Player p0, String p1);

	void setProgress(Player p0, double p1);

	void update(Player p0);

	void show(Player p0);

	void hide(Player p0);
}
