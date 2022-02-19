package net.Duels.hologram.impls;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.AtomicDouble;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.Duel.LOG_LEVEL;
import net.Duels.config.impl.HologramConfig.HologramType;
import net.Duels.datastorage.DataStorage.StatObject;
import net.Duels.datastorage.DataStorage.StatType;
import net.Duels.hologram.HologramObject;
import net.Duels.nms.Hologram;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;

public class LeaderboardHologram extends HologramObject {

	@Getter
	private final HologramType type;

	public LeaderboardHologram(UUID uuid, List<String> lines, Location location, HologramType type) {
		super(uuid, lines, location);
		this.type = type;
	}

	@Override
	public void spawnHologram() {
		PlayerObject playerObject = this.getPlayerObject();
		if (playerObject == null) {
			return;
		}

		Player player = playerObject.getPlayer();

		if (this.getHolograms().size() >= 1) {
			return;
		}

		List<Hologram> holograms = new LinkedList<>();
		AtomicDouble stackHeight = new AtomicDouble(0.0D);

		this.getLines().forEach(line -> {
			if (!isValidLine(line)) {
				Duel.log(LOG_LEVEL.WARNING,
						"While reading and reflecting the hologram line, there is a wrong line and flip it: " + line);
				return;
			}

			Pair<Double, String> value = lineToData(line);
			String text = value.getB();

			Location targetLocation = this.getLocation().clone().subtract(0.0D, stackHeight.addAndGet(value.getA()),
					0.0D);

			if (text.trim().isEmpty()) {
				return;
			}

			text = this.replaceStats(playerObject, text);

			Hologram hologram = Duel.getNms().createHologram();
			hologram.spawn(targetLocation, text);
			holograms.add(hologram);
		});

		holograms.forEach(hologram -> hologram.sendTo(player));
		this.getHolograms().addAll(holograms);
	}

	@Override
	public void destoryHologram() {
		PlayerObject playerObject = this.getPlayerObject();
		if (playerObject == null) {
			return;
		}

		Player player = playerObject.getPlayer();

		if (this.getHolograms().size() <= 0) {
			return;
		}

		this.getHolograms().forEach(hologram -> hologram.remove(player));
		this.getHolograms().clear();
	}

	@Override
	public void updateHologram() {
		PlayerObject playerObject = this.getPlayerObject();
		if (playerObject == null) {
			return;
		}

		Player player = playerObject.getPlayer();

		if (this.getHolograms().size() <= 0) {
			return;
		}

		AtomicInteger index = new AtomicInteger(0);
		this.getLines().forEach(line -> {
			if (!isValidLine(line)) {
				Duel.log(LOG_LEVEL.WARNING,
						"While reading and reflecting the hologram line, there is a wrong line and flip it: " + line);
				return;
			}

			Pair<Double, String> value = lineToData(line);
			String text = value.getB();

			if (text.trim().isEmpty()) {
				return;
			}
			
			
			Hologram hologram = this.getHolograms().get(index.getAndIncrement());
			
			text = this.replaceStats(playerObject, text);
			hologram.setArmorStandText(text);
			hologram.update(player);
		});
	}

	private String replaceStats(PlayerObject playerObject, String text) {
		String toReturn = text;
		StatType type = this.toStatType();
		List<StatObject> list = Duel.getDataStorage().getStats(type);
		String[] variables = StringUtils.substringsBetween(text, "%%", "%%");
		if (variables == null) {
			return text;
		}

		for (String var : variables) {
			String value = this.toValue(var, list);
			if (value == null) {
				return text;
			}
			toReturn = toReturn.replaceAll("\\%%" + var + "%%", value);
		}

		return toReturn;
	}

	private String toValue(String text, List<StatObject> list) {
		String[] parts = text.split("_");
		if (parts.length != 2) {
			return "<Invalid Length>";
		}

		if (!this.isInteger(parts[1])) {
			return "<Invalid Integer>";
		}

		int index = Integer.parseInt(parts[1]) - 1;
		if (list.size() <= index) {
			return "<No Data>";
		}

		if (parts[0].equalsIgnoreCase("player")) {
			return list.get(index).getName();
		} else if (parts[0].equalsIgnoreCase("kills")) {
			return "" + list.get(index).getKills();
		} else if (parts[0].equalsIgnoreCase("wins")) {
			return "" + list.get(index).getWins();
		} else if (parts[0].equalsIgnoreCase("winstreak")) {
			return "" + list.get(index).getWinStreak();
		} else if (parts[0].equalsIgnoreCase("beststreak")) {
			return "" + list.get(index).getBestStreak();
		} else if (parts[0].equalsIgnoreCase("score")) {
			return "" + list.get(index).getScore();
		} else if (parts[0].equalsIgnoreCase("coin")) {
			return "" + list.get(index).getCoin();
		} else {
			return "<No Data>";
		}
	}

	private boolean isInteger(String text) {
		try {
			Integer.valueOf(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private StatType toStatType() {
		if (this.type == HologramType.LEADERBOARD_KILLS) {
			return StatType.KILLS;
		}
		return StatType.KILLS;
	}

}
