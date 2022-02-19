package net.Duels.config.impl;

import java.util.LinkedList;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.config.BaseConfig;
import net.Duels.player.PlayerObject;
import net.Duels.utility.ItemUtils;

public class ItemConfig extends BaseConfig {

	@Getter
	private List<ConfigItem> lobby_items;
	@Getter
	private List<ConfigItem> ingame_items;
	@Getter
	private List<ConfigItem> spectator_items;
	@Getter
	private List<ConfigItem> setup_items;
	@Getter
	private List<ConfigItem> gui_mystats;

	private List<ConfigItem> gui_achievement;
	private List<ConfigItem> gui_spectatorsettings;
	private List<ConfigItem> player_visible_items;

	public ItemConfig(JavaPlugin plugin) {
		super(plugin, "items.yml");
	}

	@Override
	public void load() {
		this.lobby_items = new LinkedList<>(this.loadSection("lobby"));
		this.ingame_items = new LinkedList<>(this.loadSection("ingame"));
		this.spectator_items = new LinkedList<>(this.loadSection("spectator"));
		this.setup_items = new LinkedList<>(this.loadSection("setup"));
		this.gui_mystats = new LinkedList<>(this.loadSection("gui-stats"));
		this.gui_achievement = new LinkedList<>(this.loadSection("gui-achievement"));
		this.gui_spectatorsettings = new LinkedList<>(this.loadSection("gui-spectatorsettings"));
		this.player_visible_items = new LinkedList<>(this.loadSection("player-visible-items"));
	}

	public ConfigItem getAchievementConfigItem(String path) {
		for (ConfigItem configItem : this.gui_achievement) {
			if (configItem.getPath().equalsIgnoreCase(path)) {
				return configItem;
			}
		}
		return null;
	}

	public ConfigItem getSpectatorSettingsConfigItem(String path) {
		for (ConfigItem configItem : this.gui_spectatorsettings) {
			if (configItem.getPath().equalsIgnoreCase(path)) {
				return configItem;
			}
		}
		return null;
	}

	public ConfigItem getPlayerVisibleItem(String path) {
		for (ConfigItem configItem : this.player_visible_items) {
			if (configItem.getPath().equalsIgnoreCase(path)) {
				return configItem;
			}
		}
		return null;
	}

	public List<ConfigItem> loadSection(String section) {
		List<ConfigItem> items = new LinkedList<>();
		ConfigurationSection configSection = this.getConfig().getConfigurationSection(section);
		for (String key : configSection.getKeys(false)) {
			try {
				boolean enable = configSection.getBoolean(key + ".enable");
				int slot = configSection.getInt(key + ".slot");
				String type = configSection.getString(key + ".type");
				String material = configSection.getString(key + ".item.material");
				String data = configSection.getString(key + ".item.data");
				int amount = configSection.getInt(key + ".item.amount");
				String displayName = ChatColor.translateAlternateColorCodes('&',
						configSection.getString(key + ".item.displayName"));
				List<String> lore = configSection.getStringList(key + ".item.lores");
				List<String> newLore = new LinkedList<String>();
				for (String line : lore) {
					newLore.add(ChatColor.translateAlternateColorCodes('&', line));
				}
				if (material.equalsIgnoreCase("PLAYER_HEAD")) {
					ItemStack itemStack = ItemUtils.name(
							new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), amount, (short) 3),
							displayName, newLore.toArray(new String[0]));
					itemStack = Duel.getNms().addCustomData(ItemUtils.setItem(itemStack, true), "type", type);
					if (itemStack == null) {
						Duel.log(Duel.LOG_LEVEL.WARNING,
								"The item configuration data '" + key + "' is invalid. This data is ignored.");
					} else {
						items.add(new ConfigItem(enable, slot, itemStack, data, section + "." + key));
					}
				} else {
					XMaterial xMaterial = XMaterial.valueOf(material);
					ItemStack itemStack2 = ItemUtils.name(
							new ItemStack(xMaterial.parseMaterial(), amount, Byte.parseByte(data)), displayName,
							newLore.toArray(new String[0]));
					itemStack2 = Duel.getNms().addCustomData(ItemUtils.setItem(itemStack2, true), "type", type);
					if (itemStack2 == null) {
						Duel.log(Duel.LOG_LEVEL.WARNING,
								"The item configuration data '" + key + "' is invalid. This data is ignored");
					} else {
						items.add(
								new ConfigItem(enable, slot, itemStack2, "NONE", section + "." + key));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Duel.log(Duel.LOG_LEVEL.ERROR, ChatColor.RED + "An error occurred while loading the item " + key + ".");
			}
		}
		return items;
	}

	public static class ConfigItem {
		private final boolean enable;
		private final int slot;
		private final ItemStack itemStack;
		private final String data;
		private final String path;

		public ConfigItem(boolean enable, int slot, ItemStack itemStack, String data, String path) {
			this.enable = enable;
			this.slot = slot;
			this.itemStack = itemStack;
			this.data = data;
			this.path = path;
		}

		public ItemStack toItem(PlayerObject playerObject) {
			ItemStack itemStack = this.itemStack.clone();
			if (itemStack.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && itemStack.getData().getData() == 3) {
				SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
				meta.setOwner(this.data.replace("<player>", playerObject.getName()));
				itemStack.setItemMeta(meta);
			}
				return itemStack;
			}

		public boolean isEnable() {
			return this.enable;
		}

		public int getSlot() {
			return this.slot;
		}

		public ItemStack getItemStack() {
			return this.itemStack;
		}

		public String getData() {
			return this.data;
		}

		public String getPath() {
			return this.path;
		}
	}
}
