package net.Duels.menus;

import java.util.Arrays;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;

public class PlayGUI {
	private final PlayerObject playerObject;
	private final int page;

	public PlayGUI(PlayerObject playerObject, int page) {
		this.playerObject = playerObject;
		this.page = page;
		this.playerObject.setLastPlayPage(this.page);
		this.open();
	}

	private void open() {
		String title = Duel.getMessageConfig().getString("guis.play.title");
		InventoryView openInventory = this.playerObject.getPlayer().getOpenInventory();
		Inventory inventory = Duel.getInstance().getServer()
				.createInventory(this.playerObject.getPlayer(), 54, title);
		boolean update = false;
		if (openInventory != null) {
			if (openInventory.getTitle().equalsIgnoreCase(title)) {
				inventory = openInventory.getTopInventory();
				inventory.clear();
				update = true;
			} else {
				this.playerObject.getPlayer().closeInventory();
			}
		}
		for (int i = 0; i <= 53; ++i) {
			if (i >= 45 || i <= 8 || i % 9 == 0 || i == 17 || i == 26 || i == 35 || i == 44) {
				inventory.setItem(i, this.borderItem());
			}
		}
		if (this.getMaxPageSize() >= 2 && this.page < this.getMaxPageSize()) {
			inventory.setItem(53, this.nextPageItem());
		}
		if (this.getMaxPageSize() >= 2 && this.page >= 2) {
			inventory.setItem(45, this.backPageItem());
		}
		updateArenaItem(inventory, this.page);
		if (!update) {
			this.playerObject.getPlayer().openInventory(inventory);
		} else {
			this.playerObject.getPlayer().updateInventory();
		}
		this.playerObject.setLastPlayPage(this.page);
	}

	public static void updateArenaItem(Inventory inventory, int page) {
		int slot = 9;
		List<Arena> arenas = Duel.getArenaManager().getArenas();
		if (arenas.isEmpty()) {
			inventory.setItem(22, emptyItem());
			return;
		}
		inventory.setItem(49, randomItem());
		while (slot != 43) {
			slot = nextSlot(slot);
			inventory.setItem(slot, new ItemStack(Material.AIR));
		}
		slot = 9;
		for (int i = 28 * (page - 1); i < arenas.size(); ++i) {
			Arena arena = arenas.get(i);
			slot = nextSlot(slot);
			if (slot == 44) {
				break;
			}
			inventory.setItem(slot, arenaItem(arena));
		}
	}

	private static int nextSlot(int nowSlot) {
		if (nowSlot == 16) {
			nowSlot = 19;
		} else if (nowSlot == 25) {
			nowSlot = 28;
		} else if (nowSlot == 34) {
			nowSlot = 37;
		} else {
			++nowSlot;
		}
		return nowSlot;
	}

	public int getMaxPageSize() {
		return Duel.getArenaManager().getArenas().size() / 28 + 1;
	}

	private ItemStack nextPageItem() {
		ItemStack itemStack = new ItemStack(Material.ARROW, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Duel.getMessageConfig().getString("guis.play.next-page-item-name"));
		itemStack.setItemMeta(itemMeta);
		return Duel.getNms().addCustomData(Duel.getNms().addCustomData(itemStack, "type", "PLAY_GUI_NEXT_PAGE"), "page",
				String.valueOf(this.page));
	}

	private ItemStack backPageItem() {
		ItemStack itemStack = new ItemStack(Material.ARROW, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Duel.getMessageConfig().getString("guis.play.back-page-item-name"));
		itemStack.setItemMeta(itemMeta);
		return Duel.getNms().addCustomData(Duel.getNms().addCustomData(itemStack, "type", "PLAY_GUI_BACK_PAGE"), "page",
				String.valueOf(this.page));
	}

	private static ItemStack emptyItem() {
		ItemStack itemStack = new ItemStack(Material.PAPER, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatColor.RED + "There are no ready games on this server!");
		itemMeta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "System Item", "",
				ChatColor.GRAY + "If you are a manager,", ChatColor.GRAY + "please prepare the game!", ""));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	private static ItemStack arenaItem(Arena arena) {
		ItemStack itemStack =new ItemStack(arena.getArenaState().getMaterial().parseMaterial(), 1, arena.getArenaState().getColor());
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Duel.getMessageConfig().getString("guis.play.arena-item-name")
				.replace("%%arena_state_color%%", arena.getArenaState().getChatColor().toString())
				.replace("%%arena_name%%", arena.getDisplayName()));
		itemMeta.setLore(Duel.getMessageConfig().getList("guis.play.arena-item-lore",
				new Pair<>("%%current_player_size%%", "" + arena.getPlayers().size()),
				new Pair<>("%%max_player_size%%", "" + arena.getMaxPlayerSize()),
				new Pair<>("%%current_state%%", arena.getStateToText())));
		itemStack.setItemMeta(itemMeta);
		itemStack = Duel.getNms().addCustomData(itemStack, "arena", arena.getName());
		return Duel.getNms().addCustomData(itemStack, "type", "join_to_arena");
	}

	private ItemStack borderItem() {
		ItemStack itemStack = new ItemStack(XMaterial.YELLOW_STAINED_GLASS_PANE.parseMaterial(), 1, (short) 4);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName("§r");
		itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_PLACED_ON,
				ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(itemMeta);
		return Duel.getNms().addCustomData(itemStack, "type", "skip_this_border_item");
	}

	private static ItemStack randomItem() {
		ItemStack itemStack = new ItemStack(XMaterial.CLOCK.parseMaterial());
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(Duel.getMessageConfig().getString("guis.play.random-item-name"));
		itemMeta.setLore(Duel.getMessageConfig().getList("guis.play.random-item-lore"));
		itemStack.setItemMeta(itemMeta);
		return Duel.getNms().addCustomData(itemStack, "type", "random_join");
	}
}
