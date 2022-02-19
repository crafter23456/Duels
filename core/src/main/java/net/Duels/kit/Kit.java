package net.Duels.kit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import net.Duels.utility.ItemBuilder;

public class Kit {

	@Setter
	@Getter
	private String name, permission, displayName;

	@Setter
	@Getter
	private XMaterial material;

	@Setter
	@Getter
	private int materialData;

	@Setter
	@Getter
	private Inventory contents;

	@Setter
	@Getter
	private ItemStack displayItemStack;

	@Setter
	@Getter
	private ItemStack[] armor;

	@Setter
	@Getter
	private List<String> lore;

	public Kit(String name, String permission, String displayName, List<String> list, XMaterial material,
			int materialData, Inventory contents, ItemStack[] armor) {
		this.lore = new LinkedList<>();
		this.name = name;
		this.permission = permission;
		this.displayName = displayName;
		this.lore = list;
		this.material = material;
		this.materialData = materialData;
		this.contents = contents;
		this.armor = armor;
		this.displayItemStack = new ItemBuilder(material.parseMaterial(), 1, materialData).setDisplayName(displayName)
				.setLore(list).build();
	}

	public String getKitStatus() {
		return "§e" + this.name + "§8: " + this.permission + " §8| " + this.displayName;
	}

}
