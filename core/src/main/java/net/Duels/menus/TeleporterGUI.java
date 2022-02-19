package net.Duels.menus;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;
import net.Duels.utility.ItemUtils;

public class TeleporterGUI {
	
	private final PlayerObject playerObject;

	public TeleporterGUI(PlayerObject playerObject) {
		this.playerObject = playerObject;
	}

	public void createTeleporter() {
		Player player = this.playerObject.getPlayer();
		Arena arena = this.playerObject.getArena();
		Inventory inventory = Duel.getInstance().getServer().createInventory(this.playerObject.getPlayer(), 27,
				Duel.getMessageConfig().getString("guis.teleporter.title"));
		for (int i = 0; i < arena.getPlayers().size(); ++i) {
			PlayerObject targetPlayer = arena.getPlayers().get(i);
			Player playerInstance = targetPlayer.getPlayer();
			double health = playerInstance.getHealth() * 100.0 / playerInstance.getHealthScale();
			ItemStack itemStack = ItemUtils.skull(XMaterial.PLAYER_HEAD.parseMaterial(), 1,
					(byte) SkullType.PLAYER.ordinal(),
					Duel.getMessageConfig().getString("guis.teleporter.name").replace("%%displayName%%",
							targetPlayer.getDisplayName()),
					Duel.getMessageConfig().getList("guis.teleporter.lore",
							new Pair("%%health%%",
									String.valueOf(Math.round(health * 100.0) / 100.0)),
							new Pair("%%food%%",
									String.valueOf(playerInstance.getFoodLevel()))),
					targetPlayer.getName());
			itemStack = Duel.getNms().addCustomData(itemStack, "type", "TELEPORT_ITEM");
			itemStack = Duel.getNms().addCustomData(itemStack, "target", playerInstance.getName());
			inventory.setItem(i + 10, itemStack);
		}
		player.openInventory(inventory);
	}
	
}
