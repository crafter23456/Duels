package net.Duels.config.impl;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.config.BaseConfig;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.ConfigUtils;
import net.Duels.utility.TextUtils;

public class SignConfig extends BaseConfig {
	
	@Getter
	private List<ConfigSign> signs;

	public SignConfig(JavaPlugin plugin) {
		super(plugin, "signs.yml");
	}

	@Override
	public void load() {
		this.signs = new LinkedList<>();
		this.loadSign();
		this.update();
	}

	public void update() {
		List<ConfigSign> signs = new LinkedList<>(this.signs);
		for (ConfigSign configSign : signs) {
			Location location = configSign.getLocation();
			Block block = location.getBlock();
			Arena arena = Duel.getArenaManager().getArena(configSign.getName());
			if (!(block.getState() instanceof Sign)) {
				this.removeSign(location);
			} else if (arena == null) {
				this.removeSign(location);
				block.setType(Material.AIR);
			} else {
				Sign sign = (Sign) block.getState();
				sign.setLine(0, TextUtils.replaceText(ChatUtils.colorTranslate("&e-> &8[1vs1] &e<-")));
				sign.setLine(1, configSign.getName());
				sign.setLine(2, ChatUtils.colorTranslate("&5\u2022 " + arena.getArenaState().getChatColor() + "&l"
						+ arena.getStateToText() + " &5\u2022"));
				sign.setLine(3, TextUtils.replaceText(ChatUtils.colorTranslate(
						"&c-> &8&l" + arena.getPlayers().size() + "/" + arena.getMaxPlayerSize() + " &c<-")));
				sign.update();
			}
		}
	}

	public Block getAttachedBlock(Block b) {
		MaterialData m = b.getState().getData();
		BlockFace face = BlockFace.DOWN;
		if (m instanceof Attachable) {
			face = ((Attachable) m).getAttachedFace();
		}
		return b.getRelative(face);
	}

	private void loadSign() {
		ConfigurationSection signSection = this.getConfig().getConfigurationSection("signs");
		if (signSection == null) {
			return;
		}
		Set<String> paths = signSection.getKeys(false);
		if (paths == null) {
			return;
		}
		for (String key : paths) {
			try {
				String name = signSection.getString(key + ".name");
				BlockFace face = BlockFace.valueOf(signSection.getString(key + ".face"));
				Location location = ConfigUtils.stringToLocation(signSection.getString(key + ".location"));
				this.signs.add(new ConfigSign(key, name, location, face));
			} catch (Exception e) {
				e.printStackTrace();
				Duel.log(Duel.LOG_LEVEL.ERROR, ChatColor.RED + "An error occurred while loading the sign " + key + ".");
			}
		}
	}

	public ConfigSign getConfigSign(Location location) {
		for (ConfigSign configSign : this.signs) {
			if (configSign.getLocation().equals(location)) {
				return configSign;
			}
		}
		return null;
	}

	public void addSign(Block block, Arena arena) {
		if (!(block.getState() instanceof Sign)) {
			return;
		}
		String path = UUID.randomUUID().toString();
		String name = arena.getName();
		BlockFace face = block.getFace(block);
		Location location = block.getLocation();
		this.getConfig().set("signs." + path + ".name", name);
		this.getConfig().set("signs." + path + ".face", face.name());
		this.getConfig().set("signs." + path + ".location", ConfigUtils.locationToString(location));
		this.save();
		this.signs.add(new ConfigSign(path, name, location, face));
		this.update();
	}

	public void removeSign(Location location) {
		ConfigSign configSign = this.getConfigSign(location);
		if (configSign == null) {
			return;
		}
		this.getConfig().set("signs." + configSign.getPath(), null);
		this.save();
		this.signs.remove(configSign);
	}

	public static class ConfigSign {

		@Getter
		private final String path;

		@Getter
		private final String name;

		@Getter
		private final Location location;

		@Getter
		private final BlockFace blockFace;

		public ConfigSign(String path, String name, Location location, BlockFace blockFace) {
			this.path = path;
			this.name = name;
			this.location = location;
			this.blockFace = blockFace;
		}
	}
}
