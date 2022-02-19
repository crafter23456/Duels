package net.Duels.menus;

import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.Duels.Duel;
import net.Duels.kit.Kit;
import net.Duels.player.PlayerObject;
import net.Duels.utility.ItemBuilder;

public class KitSelectorGUI {
	private final PlayerObject playerObject;

	public KitSelectorGUI(PlayerObject playerObject) {
		this.playerObject = playerObject;
	}

	public void createKitSelectorMenu() {
		Player player = this.playerObject.getPlayer();
		Inventory inventory = Duel.getInstance().getServer().createInventory(
				this.playerObject.getPlayer(), getInventorySize(),
				Duel.getMessageConfig().getString("guis.kitselector.title"));

		List<Kit> kits = Duel.getKitManager().getKits();
		for (int i = 0; i < (Math.min(kits.size(), 54)); ++i) {
			Kit kit = kits.get(i);
			ItemStack kitItem = kit.getDisplayItemStack();
			kitItem = Duel.getNms().addCustomData(kitItem, "type", "KIT_SELECT");
			kitItem = Duel.getNms().addCustomData(kitItem, "kit", kit.getName());
			inventory.setItem(i,
					player.hasPermission(kit.getPermission()) ? kitItem
							: new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial(), 1, 14)
									.setDisplayName(kit.getName()).build());
			player.openInventory(inventory);
		}
	}

	public int getInventorySize() {
		if (Duel.getKitManager().getKits().size() <= 0) {
			return 9;
		}
		int n = (int) Math.ceil(Duel.getKitManager().getKits().size() / 9.0);
		return (n > 5) ? 54 : (n * 9);
	}
}
