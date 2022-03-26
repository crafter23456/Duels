package net.Duels.kit;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import net.Duels.Duel;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.KitUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Getter
public class KitManager {

	private final LinkedHashMap<Player, Kit> playerKit = new LinkedHashMap<>();
	
	private final List<Kit> kits;
	
	private final File file;
	
	private final YamlConfiguration configuration;

	public KitManager() {
        this.kits = new LinkedList<>();
        this.file = new File(Duel.getInstance().getDataFolder(), "kits.yml");
        try {
            if (!this.file.exists() && !this.file.createNewFile()) {
                Duel.log(Duel.LOG_LEVEL.ERROR, "There was an error creating the kit file!");
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
        this.configuration.options().copyDefaults(true);
        this.configuration.setDefaults(YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(Duel.getInstance().getResource("kits.yml"), StandardCharsets.UTF_8))));
        this.loadKits();
    }

	public Kit getKit(String s) {
		return this.kits.stream().filter(kit -> kit.getName().equals(s)).findFirst().orElse(null);
	}

	public Kit getKitByDisplayName(String s) {
		return this.kits.stream().filter(kit -> ChatColor.stripColor(kit.getDisplayName()).equals(s)).findFirst()
				.orElse(null);
	}

	public void createKit(String name, String permission, String displayName, List<String> lore,
						  XMaterial material, int data, Inventory inventory, Player player) {
		this.configuration.set("Kits." + name + ".displayName", ChatUtils.colorTranslate(displayName));
		this.configuration.set("Kits." + name + ".permission", permission);
		this.configuration.set("Kits." + name + ".material.name", material.name());
		this.configuration.set("Kits." + name + ".material.data", data);
		this.configuration.set("Kits." + name + ".contents",
				(Object) KitUtils.inventoryToString(inventory).replace("§", "&"));
		this.configuration.set("Kits." + name + ".armor", KitUtils.armorToString(player).replace("§", "&"));
		this.configuration.set("Kits." + name + ".lore", lore);
		this.kits.add(new Kit(name, permission, displayName, lore, material, data, inventory,
				player.getInventory().getArmorContents()));
		this.saveConfig();
	}

	public void deleteKit(Kit kit) {
		this.configuration.set("Kits." + kit.getName(), null);
		this.saveConfig();
		if (this.kits.contains(kit)) {
			this.kits.remove(kit);
		}
	}

	public void saveKit(Kit kit) {
		String name = kit.getName();
		this.configuration.set("Kits." + name, null);
		this.configuration.set("Kits." + name + ".displayName", kit.getDisplayName());
		this.configuration.set("Kits." + name + ".permission", kit.getPermission());
		this.configuration.set("Kits." + name + ".material.name", kit.getMaterial().name());
		this.configuration.set("Kits." + name + ".material.data", kit.getMaterialData());
		this.configuration.set("Kits." + name + ".contents",
				KitUtils.inventoryToString(kit.getContents()).replace("§", "&"));
		this.configuration.set("Kits." + name + ".armor",
				KitUtils.armorToString(kit.getArmor()).replace("§", "&"));
		this.configuration.set("Kits." + name + ".lore", kit.getLore());
		this.saveConfig();
	}

	public void loadKits() {
		kits.clear();
		ConfigurationSection kitSection = this.configuration.getConfigurationSection("Kits");
		if (kitSection == null) {
			return;
		}
		for (String name : kitSection.getKeys(false)) {
			try {
				ConfigurationSection section = this.configuration.getConfigurationSection("Kits." + name);
				String displayName = section.getString("displayName");
				String permission = section.getString("permission");
				String itemType = section.getString("material.name");
				int itemData = section.getInt("material.data");
				Inventory inventory = KitUtils.stringToInventory(section.getString("contents").replace("&", "§"));
				List<String> lore = section.getStringList("lore");
				ItemStack[] armor = KitUtils.armorFromInventory(section.getString("armor").replace("&", "§"));
				this.kits.add(new Kit(name, permission, displayName, lore, XMaterial.valueOf(itemType), itemData,
						inventory, armor));
			} catch (IllegalArgumentException e) {
				if (e.getLocalizedMessage().contains("XMaterial")) {
					e.printStackTrace();
					Duel.log(Duel.LOG_LEVEL.ERROR, "invalid material Type");
				} else {
					e.printStackTrace();
					Duel.log(Duel.LOG_LEVEL.ERROR, "");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
				Duel.log(Duel.LOG_LEVEL.ERROR, "Failed Load" + name + " Kit");
			}
		}
	}

	public void saveConfig() {
		try {
			this.configuration.save(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
