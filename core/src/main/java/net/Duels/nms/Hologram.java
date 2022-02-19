package net.Duels.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Hologram {
	void spawn(Location p0, String p1);

	void sendTo(Player... p0);

	void update(Player... p0);

	void remove(Player... p0);

	void setArmorStandText(String p0);

	String getText();
}
