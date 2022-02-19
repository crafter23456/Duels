package net.Duels.utility;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtils {

	public static ItemStack getPotion(PotionType color, PotionEffectType effect, int duration, int amplifier) {
		ItemStack pt = new Potion(color).toItemStack(1);
		PotionMeta pm = (PotionMeta) pt.getItemMeta();
		pm.addCustomEffect(new PotionEffect(effect, duration * 20, amplifier - 1), true);
		pt.setItemMeta(pm);
		return pt;
	}

	public static ItemStack addPotionEffect(ItemStack potion, PotionEffectType effect, int duration, int amplifier) {
		PotionMeta pm = (PotionMeta) potion.getItemMeta();
		pm.addCustomEffect(new PotionEffect(effect, duration * 20, amplifier - 1), true);
		potion.setItemMeta(pm);
		return potion;
	}

	public static ItemStack getSplashPotion(PotionType color, PotionEffectType effect, int duration, int amplifier) {
		ItemStack pt = new ItemStack(Material.POTION);
		Potion stack = new Potion(color);
		stack.setSplash(true);
		pt = stack.toItemStack(1);
		PotionMeta pm = (PotionMeta) pt.getItemMeta();
		pm.addCustomEffect(new PotionEffect(effect, duration * 20, amplifier - 1), true);
		pt.setItemMeta(pm);
		return pt;
	}

	public static ItemStack parseItem(List<String> item) {
		if (item.size() < 2) {
			return null;
		}
		ItemStack itemStack = null;
		try {
			if (item.get(0).contains(":")) {
				Material material = Material.getMaterial(item.get(0).split(":")[0].toUpperCase());
				int amount = Integer.parseInt(item.get(1));
				if (amount < 1) {
					return null;
				}
				short data = (short) Integer.parseInt(item.get(0).split(":")[1].toUpperCase());
				itemStack = new ItemStack(material, amount, data);
			} else {
				itemStack = new ItemStack(Material.getMaterial(item.get(0).toUpperCase()),
						Integer.parseInt(item.get(1)));
			}
			if (item.size() > 2) {
				for (int x = 2; x < item.size(); ++x) {
					if (item.get(x).split(":")[0].equalsIgnoreCase("name")) {
						ItemMeta itemMeta = itemStack.getItemMeta();
						itemMeta.setDisplayName(item.get(x).split(":")[1]);
						itemStack.setItemMeta(itemMeta);
					} else if (item.get(x).split(":")[0].equalsIgnoreCase("color")) {
						if (itemStack.getType().equals(Material.LEATHER_BOOTS)
								|| itemStack.getType().equals(Material.LEATHER_LEGGINGS)
								|| itemStack.getType().equals(Material.LEATHER_HELMET)
								|| itemStack.getType().equals(Material.LEATHER_CHESTPLATE)) {
							LeatherArmorMeta itemMeta2 = (LeatherArmorMeta) itemStack.getItemMeta();
							itemMeta2.setColor(getColor(item.get(x).split(":")[1]));
							itemStack.setItemMeta(itemMeta2);
						}
					} else {
						itemStack.addUnsafeEnchantment(getEnchant(item.get(x).split(":")[0]),
								Integer.parseInt(item.get(x).split(":")[1]));
					}
				}
			}
		} catch (Exception sex) {
		}
		return itemStack;
	}

	public static ItemStack addUnsafeEnchantment(ItemStack item, Enchantment enchantment, int level) {
		item.addUnsafeEnchantment(enchantment, level);
		return item;
	}

	public static boolean isUnbreakable(ItemStack itemstack) {
		if (itemstack.getType() == Material.AIR) {
			return false;
		}

		ItemMeta meta = itemstack.getItemMeta();
		try {
			for (Method method : meta.getClass().getDeclaredMethods()) {
				if (method.getName().equalsIgnoreCase("isUnbreakable")) {
					method.setAccessible(true);
					return (boolean) method.invoke(meta);
				}
			}
			throw new RuntimeException("L");
		} catch (Exception ignored) {
			try {
				return meta.spigot().isUnbreakable();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	public static ItemStack setUnbreakable(ItemStack itemstack, boolean unbreakable) {
		ItemMeta meta = itemstack.getItemMeta();
		Label_0104:
		{
			try {
				Method[] declaredMethods;
				for (int length = (declaredMethods = ItemMeta.class
						.getDeclaredMethods()).length, i = 0; i < length; ++i) {
					Method method = declaredMethods[i];
					if (method.getName().equalsIgnoreCase("setUnbreakable")) {
						System.out.println("");
						method.setAccessible(true);
						method.invoke(meta, unbreakable);
						break Label_0104;
					}
				}
				throw new RuntimeException("L");
			} catch (Exception ignored) {
				meta.spigot().setUnbreakable(unbreakable);
			}
		}
		addhideFlag(itemstack);
		itemstack.setItemMeta(meta);
		return itemstack;
	}

	public static ItemStack setItem(ItemStack itemstack, boolean item) {
		ItemMeta meta = itemstack.getItemMeta();
		addhideFlag(itemstack);
		itemstack.setItemMeta(meta);
		return itemstack;
	}

	public static PotionEffect parseEffect(List<String> effect) {
		if (effect.size() < 2) {
			return null;
		}
		PotionEffect potionEffect = null;
		try {
			PotionEffectType pType = getPotionType(effect.get(0));
			int length;
			if (Integer.parseInt(effect.get(1)) == -1) {
				length = Integer.MAX_VALUE;
			} else {
				length = 20 * Integer.parseInt(effect.get(1));
			}
			int level = Integer.parseInt(effect.get(2));
			potionEffect = new PotionEffect(pType, length, level);
		} catch (Exception ex) {
		}
		return potionEffect;
	}

	public static ItemStack getHead(String name) {
		ItemStack head = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), 1, (short) 3);
		SkullMeta skull = (SkullMeta) head.getItemMeta();
		skull.setOwner(name);
		head.setItemMeta(skull);
		return head;
	}

	private static PotionEffectType getPotionType(String type) {
		switch (type.toLowerCase()) {
			case "healthboost": {
				return PotionEffectType.HEALTH_BOOST;
			}
			case "invisibility": {
				return PotionEffectType.INVISIBILITY;
			}
			case "absorption": {
				return PotionEffectType.ABSORPTION;
			}
			case "hunger": {
				return PotionEffectType.HUNGER;
			}
			case "slowness": {
				return PotionEffectType.SLOW;
			}
			case "nausea": {
				return PotionEffectType.CONFUSION;
			}
			case "poison": {
				return PotionEffectType.POISON;
			}
			case "nightvision": {
				return PotionEffectType.NIGHT_VISION;
			}
			case "wither": {
				return PotionEffectType.WITHER;
			}
			case "weakness": {
				return PotionEffectType.WEAKNESS;
			}
			case "waterbreathing": {
				return PotionEffectType.WATER_BREATHING;
			}
			case "saturation": {
				return PotionEffectType.SATURATION;
			}
			case "haste": {
				return PotionEffectType.FAST_DIGGING;
			}
			case "speed": {
				return PotionEffectType.SPEED;
			}
			case "blindness": {
				return PotionEffectType.BLINDNESS;
			}
			case "miningfatique": {
				return PotionEffectType.SLOW_DIGGING;
			}
			case "jumpboost": {
				return PotionEffectType.JUMP;
			}
			case "instantdamage": {
				return PotionEffectType.HARM;
			}
			case "instanthealth": {
				return PotionEffectType.HEAL;
			}
			case "regeneration": {
				return PotionEffectType.REGENERATION;
			}
			case "strength": {
				return PotionEffectType.INCREASE_DAMAGE;
			}
			case "fireresistance": {
				return PotionEffectType.FIRE_RESISTANCE;
			}
			case "resistance": {
				return PotionEffectType.DAMAGE_RESISTANCE;
			}
			default:
				break;
		}
		return null;
	}

	private static Enchantment getEnchant(String enchant) {
		switch (enchant.toLowerCase()) {
			case "depthstrider": {
				return Enchantment.DEPTH_STRIDER;
			}
			case "blastprotection": {
				return Enchantment.PROTECTION_EXPLOSIONS;
			}
			case "fireprotection": {
				return Enchantment.PROTECTION_FIRE;
			}
			case "aquaaffinity": {
				return Enchantment.WATER_WORKER;
			}
			case "protection": {
				return Enchantment.PROTECTION_ENVIRONMENTAL;
			}
			case "sharpness": {
				return Enchantment.DAMAGE_ALL;
			}
			case "luckofthesea": {
				return Enchantment.LUCK;
			}
			case "thorns": {
				return Enchantment.THORNS;
			}
			case "fortune": {
				return Enchantment.LOOT_BONUS_BLOCKS;
			}
			case "fireaspect": {
				return Enchantment.FIRE_ASPECT;
			}
			case "luck": {
				return Enchantment.LUCK;
			}
			case "lure": {
				return Enchantment.LURE;
			}
			case "flame": {
				return Enchantment.ARROW_FIRE;
			}
			case "power": {
				return Enchantment.ARROW_DAMAGE;
			}
			case "punch": {
				return Enchantment.ARROW_KNOCKBACK;
			}
			case "smite": {
				return Enchantment.DAMAGE_UNDEAD;
			}
			case "infinity": {
				return Enchantment.ARROW_INFINITE;
			}
			case "projectileprotection": {
				return Enchantment.PROTECTION_PROJECTILE;
			}
			case "looting": {
				return Enchantment.LOOT_BONUS_MOBS;
			}
			case "featherfall": {
				return Enchantment.PROTECTION_FALL;
			}
			case "baneofarthropods": {
				return Enchantment.DAMAGE_ARTHROPODS;
			}
			case "respiration": {
				return Enchantment.OXYGEN;
			}
			case "efficiency": {
				return Enchantment.DIG_SPEED;
			}
			case "knockback": {
				return Enchantment.KNOCKBACK;
			}
			case "silktouch": {
				return Enchantment.SILK_TOUCH;
			}
			case "unbreaking": {
				return Enchantment.DURABILITY;
			}
			default:
				break;
		}
		return null;
	}

	public static ItemStack addhideFlag(ItemStack item) {
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS,
				ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
		item.setItemMeta(itemMeta);
		return item;
	}

	public static boolean isEnchanted(ItemStack itemStack) {
		return !itemStack.getEnchantments().isEmpty();
	}

	public static Color getColor(String c) {
		switch (c) {
			case "maroon": {
				return Color.MAROON;
			}
			case "orange": {
				return Color.ORANGE;
			}
			case "purple": {
				return Color.PURPLE;
			}
			case "silver": {
				return Color.SILVER;
			}
			case "yellow": {
				return Color.YELLOW;
			}
			case "fuschia": {
				return Color.FUCHSIA;
			}
			case "red": {
				return Color.RED;
			}
			case "aqua": {
				return Color.AQUA;
			}
			case "blue": {
				return Color.BLUE;
			}
			case "gray": {
				return Color.GRAY;
			}
			case "lime": {
				return Color.LIME;
			}
			case "navy": {
				return Color.NAVY;
			}
			case "teal": {
				return Color.TEAL;
			}
			case "black": {
				return Color.BLACK;
			}
			case "green": {
				return Color.GREEN;
			}
			case "olvie": {
				return Color.OLIVE;
			}
			case "white": {
				return Color.WHITE;
			}
			default:
				break;
		}
		return Color.NAVY;
	}

	public static ItemStack skull(Material material, int n, short n2, String displayName, String s, String owner) {
		ItemStack itemStack = new ItemStack(material, n, n2);
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		skullMeta.setOwner(owner);
		skullMeta.setDisplayName(displayName);
		skullMeta.setLore((s.isEmpty() ? new ArrayList<>() : Arrays.asList(s.split("\\n"))));
		itemStack.setItemMeta(skullMeta);
		return itemStack;
	}

	public static ItemStack skull(Material material, int amount, byte data, String displayName, List<String> lore,
								  String owner) {
		ItemStack itemStack = new ItemStack(material, amount, data);
		if (!(itemStack.getItemMeta() instanceof SkullMeta)) {
			itemStack = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), amount, data);
		}
		SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
		skullMeta.setOwner(owner);
		skullMeta.setDisplayName(displayName);
		if (lore == null || !lore.isEmpty()) {
			skullMeta.setLore(lore);
		}
		itemStack.setItemMeta(skullMeta);
		return itemStack;
	}

	public static ItemStack name(ItemStack itemStack, String name, String... lores) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta != null) {
			if (!name.isEmpty()) {
				itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			}
			if (lores != null && lores.length > 0) {
				List<String> loreList = new ArrayList<>(lores.length);
				for (String lore : lores) {
					loreList.add(ChatColor.translateAlternateColorCodes('&', lore));
				}
				itemMeta.setLore(loreList);
			} else {
				itemMeta.setLore(null);
			}
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS,
					ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
			itemStack.setItemMeta(itemMeta);
		}
		return itemStack;
	}
}
