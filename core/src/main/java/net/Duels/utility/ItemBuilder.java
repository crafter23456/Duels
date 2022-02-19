package net.Duels.utility;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
	
	private final ItemStack itemStack;

	public ItemBuilder addUnsafeEnchant(Enchantment enchantment, int n) {
		this.itemStack.addUnsafeEnchantment(enchantment, n);
		return this;
	}

	public ItemBuilder addEnchant(Enchantment enchantment, int n, boolean b) {
		if (b) {
			ItemMeta itemMeta = this.itemStack.getItemMeta();
			itemMeta.addEnchant(enchantment, n, true);
			this.itemStack.setItemMeta(itemMeta);
		}
		return this;
	}

	public ItemBuilder(Material material, int n, int n2) {
		if (n == 0) {
			this.itemStack = new ItemStack(material, 1, (short) n2);
			return;
		}
		this.itemStack = new ItemStack(material, n, (short) n2);
	}

	public ItemBuilder(Material material) {
		this.itemStack = new ItemStack(material);
	}

	public ItemBuilder(ItemStack itemStack, int n) {
		if (n == 0) {
			this.itemStack = itemStack;
			return;
		}
		this.itemStack = itemStack;
	}

	public ItemBuilder setDisplayName(String s) {
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		itemMeta.setDisplayName(ChatUtils.colorTranslate(s));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		this.itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setLore(String... array) {
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		itemMeta.setLore(Arrays.asList(array));
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		this.itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		if (lore.size() == 0) {
			return this;
		}
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		for (int i = 0; i < lore.size(); ++i) {
			lore.set(i, lore.get(i).replace("&", "§"));
		}
		itemMeta.setLore(lore);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		this.itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemStack build() {
		return this.itemStack;
	}

	public ItemBuilder setUnbreakable() {
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		try {
			Method metaSpigot = ItemMeta.class.getDeclaredMethod("spigot");
			Method setUnbreakable = metaSpigot.getReturnType().getDeclaredMethod("setUnbreakable", boolean.class);
			Object spigot = metaSpigot.invoke(itemMeta);
			setUnbreakable.invoke(spigot, true);
		} catch (Exception ex) {
			itemMeta.setUnbreakable(true);
		}
		this.itemStack.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder addEnchant(Enchantment enchantment, int n) {
		ItemMeta itemMeta = this.itemStack.getItemMeta();
		itemMeta.addEnchant(enchantment, n, true);
		this.itemStack.setItemMeta(itemMeta);
		return this;
	}
	
}
