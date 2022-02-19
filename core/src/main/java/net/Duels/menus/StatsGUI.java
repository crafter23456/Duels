package net.Duels.menus;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Duels.Duel;
import net.Duels.config.impl.ItemConfig;
import net.Duels.player.PlayerObject;
import net.Duels.utility.RankUtils;

public class StatsGUI {
	private final PlayerObject playerObject;

	public StatsGUI(PlayerObject playerObject) {
		this.playerObject = playerObject;
		this.open();
	}

	private void open() {
		Inventory inventory = Duel.getInstance().getServer().createInventory(this.playerObject.getPlayer(), 27,
				Duel.getMessageConfig().getString("guis.stats.title"));
		for (ItemConfig.ConfigItem item : Duel.getItemConfig().getGui_mystats()) {
			if (!item.isEnable()) {
				continue;
			}
			ItemStack itemStack = item.toItem(this.playerObject);
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				String type = Duel.getNms().getCustomData(itemStack, "type");
				if (type.equalsIgnoreCase("my_stats")) {
					this.replaceLore(itemStack);
				}
			}
			inventory.setItem(item.getSlot(), itemStack);
		}
		this.playerObject.getPlayer().openInventory(inventory);
	}

	private void replaceLore(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<String> lore = itemMeta.getLore();
		List<String> newLore = new LinkedList<>();
		for (String line : lore) {
			line = line.replace("<player>", this.playerObject.getName())
					.replace("<rank>", RankUtils.getRank(this.playerObject.getScore()))
					.replace("<progress>", RankUtils.getRankProcces(this.playerObject.getScore()).trim())
					.replace("<kills>", String.valueOf(this.playerObject.getKills()))
					.replace("<deaths>", String.valueOf(this.playerObject.getDeaths()))
					.replace("<wins>", String.valueOf(this.playerObject.getWins()))
					.replace("<lose>", String.valueOf(this.playerObject.getLose()))
					.replace("<beststreaks>", String.valueOf(this.playerObject.getBestStreak()))
					.replace("<score>", String.valueOf(this.playerObject.getScore()))
					.replace("<coins>", String.valueOf(this.playerObject.getCoin()))
					.replace("<xp>", String.valueOf(this.playerObject.getXp()));
			newLore.add(line);
		}
		itemMeta.setLore(newLore);
		itemStack.setItemMeta(itemMeta);
	}
}
