package net.Duels.nms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {

	Hologram createHologram();

	ItemStack addCustomData(ItemStack p0, String p1, String p2);

	ItemStack removeCustomData(ItemStack p0, String p1);

	String getCustomData(ItemStack p0, String p1);

	boolean isCustomData(ItemStack p0, String p1);

	double getAbsorptionHearts(Player p0);

	int getCitizenID();
}
