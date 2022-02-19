package net.Duels.runnables;

import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.menus.PlayGUI;
import net.Duels.player.PlayerObject;

public class RunnableUpdateGUI extends BukkitRunnable {

	public void run() {
		String title = Duel.getMessageConfig().getString("guis.play.title");
		for (PlayerObject playerObject : Duel.getPlayerController().getAll()) {
			if (playerObject == null) {
				InventoryView openInventory = playerObject.getPlayer().getOpenInventory();
				if (openInventory != null && title.equalsIgnoreCase(openInventory.getTitle())) {
					PlayGUI.updateArenaItem(openInventory.getTopInventory(), playerObject.getLastPlayPage());
					playerObject.getPlayer().updateInventory();
				}
			}
		}
	}
}
