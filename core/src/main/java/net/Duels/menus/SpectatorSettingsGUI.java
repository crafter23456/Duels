package net.Duels.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;

public class SpectatorSettingsGUI {
	private final PlayerObject playerObject;

	public SpectatorSettingsGUI(PlayerObject playerObject) {
		this.playerObject = playerObject;
	}

	public void createSpectatorSettings() {
		Player player = this.playerObject.getPlayer();
		Inventory inventory = Duel.getInstance().getServer().createInventory(this.playerObject.getPlayer(), 27,
				Duel.getMessageConfig().getString("guis.spectatorsettings.title"));
		inventory.setItem(11, Duel.getItemConfig().getSpectatorSettingsConfigItem("gui-spectatorsettings.nospeed-item")
				.toItem(this.playerObject));
		inventory.setItem(12, Duel.getItemConfig().getSpectatorSettingsConfigItem("gui-spectatorsettings.speedi-item")
				.toItem(this.playerObject));
		inventory.setItem(13, Duel.getItemConfig().getSpectatorSettingsConfigItem("gui-spectatorsettings.speedii-item")
				.toItem(this.playerObject));
		inventory.setItem(14, Duel.getItemConfig().getSpectatorSettingsConfigItem("gui-spectatorsettings.speediii-item")
				.toItem(this.playerObject));
		inventory.setItem(15, Duel.getItemConfig().getSpectatorSettingsConfigItem("gui-spectatorsettings.speediv-item")
				.toItem(this.playerObject));
		player.openInventory(inventory);
	}
}
