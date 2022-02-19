package net.Duels.config.impl;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.config.BaseConfig;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class SoundConfig extends BaseConfig {
	
	@Getter
	private Map<String, Boolean> options;
	
	@Getter
	private Map<String, ConfigSound> sounds;

	public SoundConfig(JavaPlugin plugin) {
		super(plugin, "sounds.yml");
	}

	@Override
	public void load() {
		this.options = new LinkedHashMap<>();
		this.sounds = new LinkedHashMap<>();
		for (String key : this.getConfig().getKeys(true)) {
			boolean isBoolean = this.getConfig().isBoolean(key);
			if (isBoolean) {
				this.options.put(key, this.getConfig().getBoolean(key));
			}
		}
		boolean invalid_sound_replace = this.options.get("invalid-sound-replace");
		boolean invalid_sound_replace_approximate = this.options.get("invalid-sound-replace-approximate");
		boolean invalid_sound_contains = this.getConfig().contains("invalid-sound");
		String invalid_sound = invalid_sound_contains ? this.getConfig().getString("invalid-sound") : "none";
		if (!invalid_sound_contains) {
			Duel.log(Duel.LOG_LEVEL.WARNING,
					"While loading the sound, the essential element \"invalid-sound\" could not be found! Replaced with the default \"none\"!");
		}
		for (String key2 : this.getConfig().getKeys(true)) {
			boolean isString = this.getConfig().isString(key2);
			if (isString) {
				String data = this.getConfig().getString(key2);
				this.sounds.put(key2, ConfigSound.compileSound(data, invalid_sound_replace,
						invalid_sound_replace_approximate, invalid_sound));
			}
		}
	}

	public ConfigSound getSound(String key) {
		if (!this.sounds.containsKey(key)) {
			Duel.log(Duel.LOG_LEVEL.WARNING, "Can't find the sound \"" + key + "\"!");
		}
		return this.sounds.getOrDefault(key, null);
	}

	public static class ConfigSound {
		
		private static final Map<String, String> cachedSound = new LinkedHashMap<>();
		
		@Getter
		private final boolean valid;
		
		@Getter
		private final String sound;
		
		@Getter
		private final float pitch;
		
		@Getter
		private final float volume;
		
		@Getter
		private Sound cached;

		private ConfigSound(String sound, float pitch, float volume) {
			this.sound = sound;
			this.pitch = pitch;
			this.volume = volume;
			this.valid = this.checkValid();
		}

		public Sound getBukkitSound() {
			if (this.cached == null) {
				this.cached = (this.sound.equalsIgnoreCase("none") ? null : Sound.valueOf(this.sound));
			}
			return this.cached;
		}

		private boolean checkValid() {
			return this.getBukkitSound() != null;
		}

		private static ConfigSound compileSound(String data, boolean invalid_sound_replace,
				boolean invalid_sound_replace_approximate, String invalid_sound) {
			if (data == null || data.isEmpty() || !data.contains(",") || data.toLowerCase().startsWith("none")) {
				return emptySound();
			}
			String[] split = data.split(",");
			String sound = (split.length >= 1) ? split[0].replace(" ", "_").toUpperCase() : "none";
			float pitch = (split.length >= 2) ? Float.parseFloat(split[1]) : 1.0f;
			float volume = (split.length >= 3) ? Float.parseFloat(split[2]) : 1.0f;
			if (!contains(sound)) {
				Sound replaceSound = null;
				if (invalid_sound_replace_approximate) {
					replaceSound = approximate(sound);
				}
				if (replaceSound != null && invalid_sound_replace) {
					return compileSound(invalid_sound, false, false, "none");
				}
				if (replaceSound == null) {
					return emptySound();
				}
				sound = replaceSound.name();
			}
			return new ConfigSound(sound, pitch, volume);
		}

		private static ConfigSound emptySound() {
			return new ConfigSound("none", 0.0f, 0.0f);
		}

		private static boolean contains(String sound) {
			try {
				Sound.valueOf(sound.toUpperCase());
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		private static Sound approximate(String sound) {
			if (ConfigSound.cachedSound.containsKey(sound)) {
				return Sound.valueOf(ConfigSound.cachedSound.get(sound));
			}
			Sound[] values;
			for (int length = (values = Sound.values()).length, i = 0; i < length; ++i) {
				Sound s = values[i];
				String name = s.name();
				if (name.contains(sound)) {
					ConfigSound.cachedSound.put(sound, s.name());
					Duel.log(Duel.LOG_LEVEL.WARNING,
							"Sound replaced from \"" + sound + "\" to \"" + name + "\" (A Type)");
					return s;
				}
			}
			String[] split = sound.split("_");
			Map<Sound, Integer> similarity = new LinkedHashMap<>();
			Sound[] values2;
			for (int length2 = (values2 = Sound.values()).length, j = 0; j < length2; ++j) {
				Sound s2 = values2[j];
				String name2 = s2.name();
				String[] targetSplit = name2.split("_");
				String[] array;
				for (int length3 = (array = targetSplit).length, k = 0; k < length3; ++k) {
					String targetPart = array[k];
					String[] array2;
					for (int length4 = (array2 = split).length, l = 0; l < length4; ++l) {
						String part = array2[l];
						if (targetPart.equalsIgnoreCase(part)) {
							similarity.put(s2, similarity.getOrDefault(s2, 0) + 1);
						}
					}
				}
			}
			Sound targetSound = null;
			int cachedSimilarity = 0;
			for (Sound s3 : similarity.keySet()) {
				if (similarity.get(s3) >= 2 && cachedSimilarity < similarity.get(s3)) {
					targetSound = s3;
					cachedSimilarity = similarity.get(s3);
				}
			}
			if (targetSound != null) {
				ConfigSound.cachedSound.put(sound, targetSound.name());
				Duel.log(Duel.LOG_LEVEL.WARNING,
						"Sound replaced from \"" + sound + "\" to \"" + targetSound.name() + "\" (B Type)");
			}
			return targetSound;
		}

	}
}
