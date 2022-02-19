package net.Duels.utility;

import com.cryptomorin.xseries.XMaterial;
import net.Duels.Duel;
import net.Duels.config.impl.ItemConfig;
import net.Duels.kit.Kit;
import net.Duels.player.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class KitUtils {

	public static void joinItem(Player player, PlayerObject playerObject) {
		if (Duel.getMainConfig().isOptionUseLobbyItems()) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			for (ItemConfig.ConfigItem configItem : Duel.getItemConfig().getLobby_items()) {
				if (configItem.isEnable()) {
					player.getInventory().setItem(configItem.getSlot(), configItem.toItem(playerObject));
				}
			}
			player.updateInventory();
			if (playerObject.isPlayerVisible()) {
				playerShowItem(player, playerObject);
			} else {
				playerHideItem(player, playerObject);
			}
		}
	}

	public static void spectatorItem(Player player, PlayerObject playerObject) {
		for (ItemConfig.ConfigItem configItem : Duel.getItemConfig().getSpectator_items()) {
			if (configItem.isEnable()) {
				player.getInventory().setItem(configItem.getSlot(), configItem.toItem(playerObject));
			}
		}
		player.updateInventory();
	}

	public static void playerHideItem(Player player, PlayerObject playerObject) {
		ItemConfig.ConfigItem configItem = Duel.getItemConfig()
				.getPlayerVisibleItem("player-visible-items.player-visible-disable-item");
		if (configItem != null && configItem.isEnable()) {
			player.getInventory().setItem(configItem.getSlot(), configItem.toItem(playerObject));
		}
		player.updateInventory();
	}

	public static void playerShowItem(Player player, PlayerObject playerObject) {
		ItemConfig.ConfigItem configItem = Duel.getItemConfig()
				.getPlayerVisibleItem("player-visible-items.player-visible-enable-item");
		if (configItem != null && configItem.isEnable()) {
			player.getInventory().setItem(configItem.getSlot(), configItem.toItem(playerObject));
		}
		player.updateInventory();
	}

	public static void setupItem(Player player, PlayerObject playerObject) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		for (ItemConfig.ConfigItem configItem : Duel.getItemConfig().getSetup_items()) {
			if (configItem.isEnable()) {
				player.getInventory().setItem(configItem.getSlot(), configItem.toItem(playerObject));
			}
		}
		player.updateInventory();
	}

	public static void giveItem(Player player) {
		player.getInventory().setHelmet(new ItemBuilder(XMaterial.DIAMOND_HELMET.parseMaterial()).setUnbreakable().addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		player.getInventory().setChestplate(new ItemBuilder(XMaterial.DIAMOND_CHESTPLATE.parseMaterial()).setUnbreakable().addEnchant(Enchantment.PROTECTION_PROJECTILE, 2).build());
		player.getInventory().setLeggings(new ItemBuilder(XMaterial.DIAMOND_LEGGINGS.parseMaterial()).setUnbreakable().addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		player.getInventory().setBoots(new ItemBuilder(XMaterial.DIAMOND_BOOTS.parseMaterial()).setUnbreakable().addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).build());
		player.getInventory().setItem(0, new ItemBuilder(XMaterial.DIAMOND_SWORD.parseMaterial())
				.setUnbreakable().addEnchant(Enchantment.DAMAGE_ALL, 3).build());
		player.getInventory().setItem(1,
				new ItemBuilder(XMaterial.FISHING_ROD.parseMaterial()).setUnbreakable().build());
		player.getInventory().setItem(2, new ItemBuilder(XMaterial.BOW.parseMaterial())
				.setUnbreakable().addEnchant(Enchantment.ARROW_DAMAGE, 2).build());
		player.getInventory().setItem(3, new ItemBuilder(XMaterial.DIAMOND_AXE.parseMaterial())
				.setUnbreakable().addEnchant(Enchantment.DIG_SPEED, 3).build());
		player.getInventory().setItem(4,
				new ItemBuilder(XMaterial.GOLDEN_APPLE.parseMaterial(), 6, 0).build());
		player.getInventory().setItem(6,
				new ItemBuilder(XMaterial.LAVA_BUCKET.parseMaterial(), 1, 0).build());
		player.getInventory().setItem(7,
				new ItemBuilder(XMaterial.WATER_BUCKET.parseMaterial(), 1, 0).build());
		player.getInventory().setItem(8,
				new ItemBuilder(XMaterial.OAK_PLANKS.parseMaterial(), 64, 0).build());
		player.getInventory().setItem(9,
				new ItemBuilder(XMaterial.ARROW.parseMaterial(), 24, 0).build());
		player.getInventory().setItem(32,
				new ItemBuilder(XMaterial.DIAMOND_PICKAXE.parseMaterial()).setUnbreakable()
						.addEnchant(Enchantment.DIG_SPEED, 5).build());
		player.getInventory().setItem(33,
				new ItemBuilder(XMaterial.LAVA_BUCKET.parseMaterial(), 1, 0).build());
		player.getInventory().setItem(34,
				new ItemBuilder(XMaterial.WATER_BUCKET.parseMaterial(), 1, 0).build());
		player.getInventory().setItem(35,
				new ItemBuilder(XMaterial.OAK_PLANKS.parseMaterial(), 64, 0).build());

		giveGoldenHeadsHead(player);
	}

	public static void giveGoldenHeadsHead(Player player) {
		String goldenheadsUrl = Duel.getMainConfig().getGoldenHeadsTexture();
		ItemStack goldenheadsSkull = HeadUtils.getCustomSkull(goldenheadsUrl);
		ItemMeta itemMeta = goldenheadsSkull.getItemMeta();
		goldenheadsSkull.setAmount(3);
		itemMeta.setDisplayName(Duel.getMessageConfig().getString("arenas.ingame.golden-head"));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		goldenheadsSkull.setItemMeta(itemMeta);
		goldenheadsSkull = Duel.getNms().addCustomData(goldenheadsSkull, "type", "golden_head");
		player.getInventory().setItem(5, goldenheadsSkull);
	}

	public static Inventory stringToInventory(String s) {
		String[] split = s.split(";");
		int inventorySize = Integer.parseInt(split[0]);
		if (inventorySize % 9 != 0) {
			Duel.log(Duel.LOG_LEVEL.ERROR, "invalid inventoy size");
			return null;
		}
		Inventory inventory = Bukkit.getServer().createInventory(null,
				9 * (inventorySize % 9 + 1));
		for (int i = 0; i < split.length; ++i) {
			String[] split2 = split[i].split("#");
			int intValue = Integer.parseInt(split2[0]);
			if (intValue < inventory.getSize()) {
				ItemStack itemStack = null;
				Boolean b = false;
				String[] split3;
				for (int length = (split3 = split2[1].split(":")).length, j = 0; j < length; ++j) {
					String[] split4 = split3[j].split("@");
					if (split4[0].equals("t")) {
						Material material;
						try {
							material = XMaterial.valueOf(split4[1]).parseMaterial();
						} catch (Exception e) {
							material = Material.valueOf(split4[1]);
						}
						itemStack = new ItemStack(material);
						b = true;
					} else if (split4[0].equals("d") && b) {
						itemStack.setDurability(Short.parseShort(split4[1]));
					} else if (split4[0].equals("a") && b) {
						itemStack.setAmount(Integer.parseInt(split4[1]));
					} else if (split4[0].equals("n") && b) {
						ItemMeta itemMeta = itemStack.getItemMeta();
						itemMeta.setDisplayName(split4[1].replaceAll("&", "§"));
						itemStack.setItemMeta(itemMeta);
					} else if (split4[0].equals("e") && b) {
						itemStack.addUnsafeEnchantment(Enchantment.getByName(String.valueOf(split4[1])),
								Integer.parseInt(split4[2]));
					} else if (split4[0].equals("u") && b) {
						itemStack = ItemUtils.setUnbreakable(itemStack, true);
					}
				}
				inventory.setItem(intValue, itemStack);
			}
		}
		return inventory;
	}

	public static String inventoryToString(Inventory inventory) {
		String s = inventory.getContents().length + ";";
		int i = -1;
		ItemStack[] contents;
		for (int length = (contents = inventory.getContents()).length, j = 0; j < length; ++j) {
			ItemStack item = contents[j];
			++i;
			if (item != null) {
				String s2 = "t@" + item.getType().name();
				if (item.getDurability() != 0) {
					s2 = s2 + ":d@" + String.valueOf(item.getDurability());
				}
				if (item.getAmount() != 1) {
					s2 = s2 + ":a@" + item.getAmount();
				}
				if (ItemUtils.isUnbreakable(item)) {
					s2 = s2 + ":u@";
				}
				if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
					s2 = s2 + ":n@"
							+ item.getItemMeta().getDisplayName();
				}
				Map<Enchantment, Integer> enchantments = item.getEnchantments();
				if (enchantments.size() > 0) {
					for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
						s2 = s2 + ":e@" + entry.getKey().getName() + "@"
								+ entry.getValue();
					}
				}
				s = s + i + "#" + s2 + ";";
			}
		}
		return s;
	}

	public static String armorToString(Player player) {
		String s = String.valueOf(player.getInventory().getArmorContents().length) + ";";
		for (int i = 0; i < player.getInventory().getArmorContents().length; ++i) {
			ItemStack itemStack = player.getInventory().getArmorContents()[i];
			if (itemStack != null) {
				String s2 = "t@" + itemStack.getType().name();
				if (itemStack.getDurability() != 0) {
					s2 = s2 + ":d@" + String.valueOf(itemStack.getDurability());
				}
				if (itemStack.getAmount() != 1) {
					s2 = s2 + ":a@" + itemStack.getAmount();
				}
				if (ItemUtils.isUnbreakable(itemStack)) {
					s2 = s2 + ":u@";
				}
				if (itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName() != null) {
					s2 = s2 + ":n@"
							+ ChatUtils.colorTranslate(itemStack.getItemMeta().getDisplayName());
				}
				Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
				if (enchantments.size() > 0) {
					for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
						s2 = s2 + ":e@" + entry.getKey().getName() + "@"
								+ entry.getValue();
					}
				}
				s = s + i + "#" + s2 + ";";
			}
		}
		return s;
	}

	public static String armorToString(ItemStack[] armorContents) {
		String s = armorContents.length + ";";
		for (int i = 0; i < armorContents.length; ++i) {
			ItemStack itemStack = armorContents[i];
			if (itemStack != null) {
				String s2 = "t@" + itemStack.getType().name();
				if (itemStack.getDurability() != 0) {
					s2 = s2 + ":d@" + String.valueOf(itemStack.getDurability());
				}
				if (itemStack.getAmount() != 1) {
					s2 = s2 + ":a@" + itemStack.getAmount();
				}
				if (ItemUtils.isUnbreakable(itemStack)) {
					s2 = s2 + ":u@";
				}
				if (itemStack.hasItemMeta() && itemStack.getItemMeta().getDisplayName() != null) {
					s2 = s2 + ":n@"
							+ ChatUtils.colorTranslate(itemStack.getItemMeta().getDisplayName());
				}
				Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
				if (enchantments.size() > 0) {
					for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
						s2 = s2 + ":e@" + entry.getKey().getName() + "@"
								+ entry.getValue();
					}
				}
				s = s + i + "#" + s2 + ";";
			}
		}
		return s;
	}

	public static String itemStackToString(Player player) {
		String string = "";
		ItemStack itemInHand = player.getInventory().getItemInHand();
		if (itemInHand != null) {
			String s = "t@" + itemInHand.getType().name();
			if (itemInHand.getDurability() != 0) {
				s = s + ":d@" + String.valueOf(itemInHand.getDurability());
			}
			if (itemInHand.getAmount() != 1) {
				s = s + ":a@" + itemInHand.getAmount();
			}
			if (ItemUtils.isUnbreakable(itemInHand)) {
				s = s + ":u@";
			}
			Map<Enchantment, Integer> enchantments = itemInHand.getEnchantments();
			if (enchantments.size() > 0) {
				for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
					s = s + ":e@" + entry.getKey().getName() + "@" + entry.getValue();
				}
			}
			string = string + "#" + s + ";";
		}
		return string;
	}

	public static ItemStack itemStackFromString(String s) {
		String[] split = s.split("#");
		ItemStack itemStack = null;
		Boolean b = false;
		String[] split2;
		for (int length = (split2 = split[1].split(":")).length, i = 0; i < length; ++i) {
			String[] split3 = split2[i].split("@");
			if (split3[0].equals("t")) {
				Material material;
				try {
					material = XMaterial.valueOf(split3[1]).parseMaterial();
				} catch (Exception e) {
					material = Material.valueOf(split3[1]);
				}
				itemStack = new ItemStack(material);
				b = true;
			} else if (split3[0].equals("d") && b) {
				itemStack.setDurability(Short.parseShort(split3[1]));
			} else if (split3[0].equals("a") && b) {
				itemStack.setAmount(Integer.parseInt(split3[1]));
			} else if (split3[0].equals("e") && b) {
				itemStack.addUnsafeEnchantment(Enchantment.getByName(String.valueOf(split3[1])),
						Integer.parseInt(split3[2]));
			} else if (split3[0].equals("u") && b) {
				itemStack = ItemUtils.setUnbreakable(itemStack, true);
			}
		}
		return itemStack;
	}

	public static ItemStack[] armorFromInventory(String s) {
		String[] split = s.split(";");
		ItemStack itemStack = null;
		ItemStack itemStack2 = null;
		ItemStack itemStack3 = null;
		ItemStack itemStack4 = null;
		for (int i = 1; i < split.length; ++i) {
			String[] split2 = split[i].split("#");
			int intValue = Integer.parseInt(split2[0]);
			ItemStack itemStack5 = null;
			Boolean b = false;
			String[] split3;
			for (int length = (split3 = split2[1].split(":")).length, j = 0; j < length; ++j) {
				String[] split4 = split3[j].split("@");
				if (split4[0].equals("t")) {
					itemStack5 = new ItemStack(XMaterial.valueOf(String.valueOf(split4[1])).parseMaterial());
					b = true;
				} else if (split4[0].equals("d") && b) {
					itemStack5.setDurability(Short.parseShort(split4[1]));
				} else if (split4[0].equals("n") && b) {
					ItemMeta itemMeta = itemStack5.getItemMeta();
					itemMeta.setDisplayName(split4[1].replaceAll("&", "§"));
					ItemUtils.setUnbreakable(itemStack5, true);
					itemStack5.setItemMeta(itemMeta);
				} else if (split4[0].equals("a") && b) {
					itemStack5.setAmount(Integer.parseInt(split4[1]));
				} else if (split4[0].equals("e") && b) {
					itemStack5.addUnsafeEnchantment(Enchantment.getByName(String.valueOf(split4[1])),
							Integer.parseInt(split4[2]));
				} else if (split4[0].equals("u") && b) {
					itemStack5 = ItemUtils.setUnbreakable(itemStack5, true);
				}
			}
			if (intValue == 0) {
				itemStack = itemStack5;
			} else if (intValue == 1) {
				itemStack2 = itemStack5;
			} else if (intValue == 2) {
				itemStack3 = itemStack5;
			} else if (intValue == 3) {
				itemStack4 = itemStack5;
			}
		}
		return new ItemStack[] { itemStack, itemStack2, itemStack3, itemStack4 };
	}

	public static void giveKit(Player player, Kit kit) {
		ItemStack[] itemStack = new ItemStack[4];
		for (int i = 0; i < 4; ++i) {
			if (kit.getArmor()[i] != null) {
				itemStack[i] = kit.getArmor()[i];
			}
		}
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setArmorContents(itemStack);
		player.getInventory().setContents(kit.getContents().getContents());
		player.updateInventory();
	}
	
}
