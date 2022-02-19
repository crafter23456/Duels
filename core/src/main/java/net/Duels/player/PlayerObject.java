package net.Duels.player;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import lombok.Getter;
import lombok.Setter;
import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.config.impl.SoundConfig;
import net.Duels.scoreboard.ScoreboardManager;

public class PlayerObject {

	private final UUID uuid;

	@Setter
	@Getter
	private String name, kitSelected;

	@Setter
	@Getter
	private int kills = 0, deaths = 0, wins = 0, lose = 0, winStreak = 0, bestStreak = 0, score = 0, coin = 0, xp = 0,
			lastPlayPage = 0;

	@Setter
	@Getter
	private long lastGoldenApple = -1L, visible = System.currentTimeMillis() - 3000L;

	@Setter
	@Getter
	private boolean playerVisible = true, spectator = false;

	@Setter
	@Getter
	private BukkitTask quitTask;
	@Setter
	@Getter
	private Arena arena = null;

	@Setter
	@Getter
	private SETUP_DATA setupData;

	public PlayerObject(UUID uuid) {
		this.uuid = uuid;
		this.name = this.getPlayer().getName();

		if (Duel.getPlayerController() != null && Duel.getPlayerController().containsPlayer(uuid)) {
			Duel.log(Duel.LOG_LEVEL.ERROR, Duel.getMainConfig().isDebug()
					? "Hey! I don't know any developer, I think we've redefined the PlayerObject already defined! Fix it right now!"
					: "An error has occurred. Please inform the management.");
			throw new RuntimeException("Programming has become unstable. Please forward this error to the developer.");
		}

		Duel.getDataStorage().loadPlayer(this);
		ScoreboardManager.firstScoreboard(this);
	}

	public void playSound(Sound sound, float pitch, float volume) {
		if (this.isOffline()) {
			return;
		}
		this.getPlayer().playSound(this.getLocation(), sound, pitch, volume);
	}

	public void playSound(String key) {
		SoundConfig.ConfigSound sound = Duel.getSoundConfig().getSound(key);
		if (sound == null) {
			return;
		}
		if (!sound.isValid()) {
			return;
		}
		Sound bukkitSound = sound.getBukkitSound();
		if (bukkitSound != null) {
			this.playSound(bukkitSound, sound.getPitch(), sound.getVolume());
		}
	}

	public Location getLocation() {
		if (this.isOffline()) {
			return null;
		}
		return this.getPlayer().getLocation();
	}

	public boolean isOnline() {
		return this.getPlayer() != null;
	}

	public boolean isOffline() {
		return this.getPlayer() == null;
	}

	public Player getPlayer() {
		return Duel.getInstance().getServer().getPlayer(this.uuid);
	}

	public String getDisplayName() {
		return this.getPlayer().getDisplayName();
	}

	public UUID getUniqueId() {
		return this.uuid;
	}

	public boolean inArena() {
		return this.arena != null;
	}

	public static class SETUP_DATA {

		@Getter
		@Setter
		private String name;

		@Getter
		@Setter
		private Location waitingLocation;

		@Getter
		@Setter
		private Location spectatorLocation;

		@Getter
		@Setter
		private Location spawn1;

		@Getter
		@Setter
		private Location spawn2;

		@Getter
		@Setter
		private double maxBuildY;

		public SETUP_DATA() {
			this.maxBuildY = -999.0;
		}


		public boolean compile() {
			return this.name != null && this.waitingLocation != null && this.spectatorLocation != null && this.spawn1 != null && this.spawn2 != null && this.maxBuildY != -999.0;
		}
	}
}