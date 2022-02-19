package net.Duels.hologram;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;
import lombok.Setter;
import net.Duels.Duel;
import net.Duels.nms.Hologram;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;

public abstract class HologramObject {
	
	@Getter
	private static final Pattern pattern = Pattern.compile("^[+-]?\\d*(\\.?\\d*).*[,]+.*$");

	@Getter
	private final UUID uuid;

	@Getter
	private final List<Hologram> holograms = new LinkedList<>();

	@Getter
	private final List<String> lines = new LinkedList<>();

	@Getter
	private final Location location;
	
	@Setter
	@Getter
	private boolean changedWorld = false;

	public HologramObject(UUID uuid, List<String> lines, Location location) {
		this.uuid = uuid;
		this.lines.addAll(lines);
		this.location = location;
	}

	public abstract void spawnHologram();

	public abstract void destoryHologram();

	public abstract void updateHologram();

	public boolean isInWorld(World world) {
		return location.getWorld().getUID().equals(world.getUID());
	}
	
	protected PlayerObject getPlayerObject() {
		return Duel.getPlayerController().getPlayer(this.uuid);
	}

	public static boolean isValidLine(String line) {
		return pattern.matcher(line).find();
	}

	public static Pair<Double, String> lineToData(String line) {
		int targetIndex = line.indexOf(",");
		double heigth = Double.parseDouble(line.substring(0, targetIndex));
		String text = line.substring(targetIndex + 1);

		return new Pair<>(heigth, text);
	}

}
