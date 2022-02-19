package net.Duels.config;

import lombok.Getter;
import net.Duels.Duel;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter
public abstract class BaseConfig {

	private final JavaPlugin plugin;

	private final File file;

	private final FileConfiguration config;

	private final String configName;

	public BaseConfig(JavaPlugin plugin, String name) {
		this.configName = name;
		this.plugin = plugin;

		File dataFolder = plugin.getDataFolder();
		if (!dataFolder.exists() && !dataFolder.mkdirs()) {
			throw new RuntimeException("Failed to create plugin folder!");
		}

		this.file = new File(dataFolder, this.configName);
		try {
			if (!file.exists() && !file.createNewFile()) {
				throw new RuntimeException("'" + this.configName + "' could not be created!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.config = YamlConfiguration.loadConfiguration(this.file);
		if (config.contains("do:not:touch:this")) {
			return;
		}
		this.config.options().copyDefaults(true);
		this.config.options().copyHeader(true);

		InputStream defaultInputStream = Duel.getInstance().getResource(this.configName);
		if (defaultInputStream != null) {
			this.config.setDefaults(YamlConfiguration.loadConfiguration(new BufferedReader(
					new InputStreamReader(defaultInputStream, StandardCharsets.UTF_8))));
		}

		this.save();
	}

	public abstract void load();

	public void save() {
		try {
			this.config.save(this.file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}